package cn.shunt.client;


import io.netty.channel.Channel;

import java.util.concurrent.LinkedBlockingQueue;

public final class MessageChannel {
    static final LinkedBlockingQueue<ApplicationMsg> msgQueue = new LinkedBlockingQueue<ApplicationMsg>();
    MessageDecoder decoder;
    MessageEncoder encoder;
    MessageHandler handler;
    Channel channel;
    String remoteAddress;

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

    public void write(Object o) {
        try {
            ApplicationMsg msg = this.encoder.encode(this, ApplicationMsg.CONNECTION, remoteAddress, o);
            if (msg != null)
                ApplicationManager.sendMsg(msg);
        } catch (Exception e) {
            handler.exceptionCatch(this, e);
        }
    }
}
