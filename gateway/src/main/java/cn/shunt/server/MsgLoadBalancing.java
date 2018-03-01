package cn.shunt.server;

import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.StampedLock;

/*
 * 负载均衡控制
 * 链接接入时分配给持有链接最少的应用服务器
 * 控制应用服务器的注销与注册，注销时重新对链接进行分配
 * 并发控制使用读写锁
 */
final class MsgLoadBalancing {
    private static final Logger logger = LoggerFactory.getLogger(MsgLoadBalancing.class);
    private static final StampedLock lock = new StampedLock();
    private static Map<Long, ApplicationService> services = new ConcurrentHashMap();
    private static Map<String, Client> channelMap = new ConcurrentHashMap();

    static void register(ApplicationService service) {
        long stamp = lock.writeLock();
        try {
            services.put(service.number, service);
            try {
                Thread.sleep(10010);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } finally {
            lock.unlockWrite(stamp);
        }
    }

    /**
     * 应用服务器注销时，将其持有的连接分配给其他应用服务器
     * 以轮询的方式，将每一个连接平均分配给应用服务器
     *
     * @param number
     */
    static void disRegister(long number) {
        long stamp = lock.writeLock();
        try {
            ApplicationService service = services.get(number);
            services.remove(number);
            int sum = services.size() - 1;
            if (sum > 0) {
                List<String> list = service.getAddresses();
                if (!list.isEmpty()) {
                    List<Long> serviceList = new ArrayList(services.keySet());
                    int i = 0;
                    for (String address : list) {
                        services.get(serviceList.get(i)).putAddress(address);
                        i++;
                        if (i > sum ) {
                            i = 0;
                        }
                    }
                }
            }
        } finally {
            lock.unlockWrite(stamp);
        }
    }


    /**
     * 外部链接接入
     * 判断有无应用服务器注册，如无应用服务器注册，则拒绝链接并记录日志
     *
     * @param ctx
     */
    static void connect(ChannelHandlerContext ctx) {
        long stamp = lock.readLock();
        try {
            String address = ctx.channel().remoteAddress().toString();
            if (services.size() < 1) {
                ctx.channel().close();
                logger.error("No usable application service, connect well been close, remoteAddress: %s", address);
                return;
            }
            ApplicationService service = getMinLoad();
            channelMap.put(address, new Client(service.number, ctx));
            service.putAddress(address);
        } finally {
            lock.unlockRead(stamp);
        }
    }

    /**
     * 外部链接注销
     *
     * @param ctx
     */
    static void disConnect(ChannelHandlerContext ctx) {
        long stamp = lock.writeLock();
        try {
            String address = ctx.channel().remoteAddress().toString();
            long number = channelMap.get(address).applicationNUmber;
            if (number == 0 || !services.containsKey(number)) {
                logger.warn("Some connect's application service has been lost, remoteAddress: %s", address);
                channelMap.remove(address);
                return;
            }
            ApplicationService service = services.get(number);
            channelMap.remove(address);
            service.removeAddress(address);
        } finally {
            lock.unlockWrite(stamp);
        }
    }

    /**
     * 将消息发送到应用服务器
     * 此处要重新构建消息
     *
     * @param data
     * @param ctx
     */
    static void writeToApplicationService(GatewayData data, ChannelHandlerContext ctx) {
        long stamp = lock.readLock();
        try {
            String address = ctx.channel().remoteAddress().toString();
            long number = channelMap.get(address).applicationNUmber;
            if (number == 0 || !services.containsKey(number)) {
                logger.warn("Some connect's application service has been lost, remoteAddress: %s", address);
                return;
            }
            //此处可能出现链接丢失，以后处理
            services.get(number).channel.writeAndFlush(new ApplicationMsg(ApplicationMsg.CONNECTION, data.data, address));
        } finally {
            lock.unlockRead(stamp);
        }

    }

    /**
     * 向外写出数据
     *
     * @param data
     */
    static void writeOut(GatewayData data) {
        long stamp = lock.readLock();
        try {
            String address = data.address;
            if (channelMap.containsKey(address)) {
                channelMap.get(address).ctx.channel().writeAndFlush(data.data);
            } else {
                //考虑到性能问题，并未对应用服务器向外传输的消息应答成功或失败，在此日志记录下因为微小延时而造成的消息丢失
                logger.error("Connect lost, message not send");
            }
        } finally {
            lock.unlockRead(stamp);
        }
    }

    /**
     * 获取连接数最小的应用服务
     *
     * @return
     */
    static ApplicationService getMinLoad() {
        long stamp = lock.readLock();
        try {
            List<Map.Entry<Long, ApplicationService>> list = new LinkedList<>(services.entrySet());
            Collections.sort(list, new Comparator<Map.Entry<Long, ApplicationService>>() {
                @Override
                public int compare(Map.Entry<Long, ApplicationService> o1, Map.Entry<Long, ApplicationService> o2) {
                    return (o1.getValue()).getChannelSum() - (o2.getValue()).getChannelSum();
                }
            });
            return services.get(list.get(0).getKey());
        } finally {
            lock.unlockRead(stamp);
        }
    }

    static final class Client {
        long applicationNUmber = 0;
        ChannelHandlerContext ctx;

        public Client(long applicationNUmber, ChannelHandlerContext ctx) {
            this.applicationNUmber = applicationNUmber;
            this.ctx = ctx;
        }
    }
}
