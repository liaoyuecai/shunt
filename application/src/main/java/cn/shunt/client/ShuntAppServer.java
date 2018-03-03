package cn.shunt.client;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class ShuntAppServer {

    private final String gatewayHost;
    private final int gatewayPort;

    public ShuntAppServer(String gatewayHost, int gatewayPort) {
        this.gatewayHost = gatewayHost;
        this.gatewayPort = gatewayPort;

    }

    public void Initiate(ChannelFactory factory) throws InterruptedException {
        ApplicationManager.factory = factory;
        new ApplicationClient(gatewayHost, gatewayPort).init();
    }
}
