package cn.shunt.client;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class ShuntAppServer {
    final ExecutorService executor;

    private final String gatewayHost;
    private final int gatewayPort;

    public ShuntAppServer(int poolSize, String gatewayHost, int gatewayPort) {
        this.executor = Executors.newFixedThreadPool(poolSize);
        this.gatewayHost = gatewayHost;
        this.gatewayPort = gatewayPort;

    }

    public ShuntAppServer(String gatewayHost, int gatewayPort) {
        this.executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() * 2 + 1);
        this.gatewayHost = gatewayHost;
        this.gatewayPort = gatewayPort;
    }

    public void Initiate(ChannelFactory factory) throws InterruptedException {
        ApplicationManager.factory = factory;
        new ApplicationClient(gatewayHost, gatewayPort).init();
    }
}
