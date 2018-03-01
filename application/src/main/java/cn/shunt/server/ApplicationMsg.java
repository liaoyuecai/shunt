package cn.shunt.server;


import java.nio.charset.Charset;

class ApplicationMsg {
    final static int REGISTER = 0;
    final static int REGISTER_ACK = 1;
    final static int REGISTER_FINISH = 2;
    final static int CONNECTION = 3;
    final static Charset charCode = Charset.forName("utf-8");
    int msgId;
    byte[] data;
    String address;
    long number;

    ApplicationMsg(int msgId, byte[] data, String address) {
        this.msgId = msgId;
        this.data = data;
        this.address = address;
    }

    ApplicationMsg(int msgId, long number) {
        this.msgId = msgId;
        this.number = number;
    }
    ApplicationMsg(int msgId){
        this.msgId = msgId;
    }
}
