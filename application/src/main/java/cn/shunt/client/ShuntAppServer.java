package cn.shunt.client;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class ShuntAppServer {

    private final String gatewayHost;
    private final int gatewayPort;

    public ShuntAppServer(String gatewayHost, int gatewayPort, ChannelFactory factory) {
        this.gatewayHost = gatewayHost;
        this.gatewayPort = gatewayPort;

    }

    public void Initiate(ChannelFactory factory) throws InterruptedException {
        ApplicationManager.initClient(gatewayHost, gatewayPort, factory);
    }
}
