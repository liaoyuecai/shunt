package cn.shunt.server;

public class Initiate {

    public static void main(String[] args) {
        new ShuntServer().init();
        new GatewayServer().init();
    }

}
