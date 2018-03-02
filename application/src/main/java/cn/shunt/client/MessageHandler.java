package cn.shunt.client;

interface MessageHandler {
    void connect(MessageChannel channel);
    void disconnect(MessageChannel channel);
    void receive(MessageChannel channel,Object o);
}
