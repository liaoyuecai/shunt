package cn.shunt.client;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.locks.StampedLock;

final class ApplicationManager {
    private static final StampedLock lock = new StampedLock();
    private final static ExecutorService executor = Executors.newCachedThreadPool();
    ;
    static final LinkedBlockingQueue<ApplicationMsg> msgQueue = new LinkedBlockingQueue<ApplicationMsg>();
    private static final Map<String, MessageChannel> channelMap = new ConcurrentHashMap();
    private static List<MessageWorker> workers = new ArrayList<>();
    static ChannelFactory factory;
    final static ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();

    private static int handleSum = 0;
    private static int startQueueSize = 0;
    private static int efficient = 0;

    static synchronized void handleOver() {
        handleSum++;
    }


    /*
     * 定时任务，定时检测队列数据的处理效率，以此判断是否需要新增或删除线程
     * 当间隔时间内收到数据超过处理数据大于50时，如效率为正时将其重置为0，记录效率（efficient）-1
     * 当间隔时间内收到数据小于或等于处理数据时，如效率为负时将其重置为0，记录效率（efficient）+1
     * 当效率大于5时，说明近期持续5次以上接收速度小于处理速度，此时关闭线程池中一个线程，并将效率重置为0
     * 当效率小于-5时，说明近期持续5次以上处理速度小于接收速度，此时新增一个线程入线程池，并将效率重置为0
     * 关闭线程时必须保证至少有一个线程，如只有一个线程时不执行关闭操作
     */
    static {
        MessageWorker worker = new MessageWorker();
        executor.execute(worker);
        workers.add(worker);
        Runnable timeTask = new Runnable() {
            @Override
            public void run() {
                int endQueue = msgQueue.size();
                int untreated = endQueue - startQueueSize - handleSum;
                if (untreated > 50) {
                    if (efficient > 0)
                        efficient = 0;
                    efficient--;
                } else if (untreated < 1) {
                    if (efficient < 0)
                        efficient = 0;
                    efficient++;
                } else {
                    if (efficient != 0)
                        efficient = 0;
                }
                handleSum = 0;
                startQueueSize = endQueue;
                if (efficient < -5) {
                    MessageWorker worker = new MessageWorker();
                    executor.execute(worker);
                    workers.add(worker);
                } else if (efficient > 5) {
                    if (workers.size() > 1) {
                        MessageWorker worker = workers.get(0);
                        workers.remove(worker);
                        worker.destroy();
                    }
                }
            }
        };
        executorService.scheduleAtFixedRate(timeTask, 0, 5000, TimeUnit.MILLISECONDS);
    }

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
