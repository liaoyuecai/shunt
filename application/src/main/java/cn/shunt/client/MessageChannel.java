package cn.shunt.client;


public class MessageChannel {
    MessageDecoder decoder;
    MessageEncoder encoder;
    MessageHandler handler;

    String  remoteAddress;

    public MessageChannel(String remoteAddress) {
        this.remoteAddress = remoteAddress;
    }

    public String getRemoteAddress() {
        return remoteAddress;
    }

    public MessageChannel add(MessageDecoder decoder) {
        this.decoder = decoder;
        return this;
    }

    public MessageChannel add(MessageEncoder encoder) {
        this.encoder = encoder;
        return this;
    }


    public MessageChannel add(MessageHandler handler) {
        this.handler = handler;
        return this;
    }
}
