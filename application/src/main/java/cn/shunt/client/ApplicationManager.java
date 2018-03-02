package cn.shunt.client;


import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.locks.StampedLock;

final class ApplicationManager {
    private static final StampedLock lock = new StampedLock();
    static final LinkedBlockingQueue<ApplicationMsg> msgQueue = new LinkedBlockingQueue<ApplicationMsg>();
    private static final Map<String, MessageChannel> channelMap = new ConcurrentHashMap();
    static ChannelFactory factory;

    static void register(String address) {
        long stamp = lock.writeLock();
        try {
            if (!channelMap.containsKey(address)) {
                MessageChannel channel = new MessageChannel(address);
                factory.initChannel(channel);
                channelMap.put(address, channel);
                channel.handler.connect(channel);
            }
        } finally {
            lock.unlockWrite(stamp);
        }
    }

    static void disRegister(String address) {
        long stamp = lock.writeLock();
        try {
            if (channelMap.containsKey(address)) {
                MessageChannel channel = channelMap.get(address);
                channel.handler.disconnect(channel);
                channelMap.remove(address);
            }
        } finally {
            lock.unlockWrite(stamp);
        }
    }

    static void receive(ApplicationMsg msg) throws InterruptedException {
        msgQueue.put(msg);
    }
}
