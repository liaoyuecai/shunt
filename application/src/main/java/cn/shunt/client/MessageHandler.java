package cn.shunt.client;

public abstract class MessageHandler {
    abstract void connect(MessageChannel channel);

    abstract void disconnect(MessageChannel channel);

    abstract void receive(MessageChannel channel, Object o);
}
