package cn.shunt.server;

class GatewayData {
    String address;
    byte[] data;

    public GatewayData(String address, byte[] data) {
        this.address = address;
        this.data = data;
    }
}
