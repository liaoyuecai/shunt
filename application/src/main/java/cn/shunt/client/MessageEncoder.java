package cn.shunt.client;

public abstract class MessageEncoder {
    final ApplicationMsg encode(MessageChannel channel, int msgId, String address, Object o) {
        try {
            byte[] bytes = this.encode(channel, o);
            if (bytes != null)
                return new ApplicationMsg(msgId, bytes, address);
            else
                throw new NullPointerException("Write data is null");
        } catch (Exception e) {
            channel.handler.exceptionCatch(channel, e);
        }
        return null;
    }

    abstract byte[] encode(MessageChannel channel, Object o);
}
