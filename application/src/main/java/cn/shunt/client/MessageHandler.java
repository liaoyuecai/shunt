package cn.shunt.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class MessageHandler {
    private static final Logger logger = LoggerFactory.getLogger(MessageHandler.class);

    abstract void connect(MessageChannel channel);

    abstract void disconnect(MessageChannel channel);

    abstract void receive(MessageChannel channel, Object o);

    void exceptionCatch(MessageChannel channel, Exception e) {
        logger.error("Message handle exception", e);
    }
}
