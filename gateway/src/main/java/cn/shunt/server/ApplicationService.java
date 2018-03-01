package cn.shunt.server;

import io.netty.channel.Channel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

class ApplicationService {
    long number;
    private int channelSum = 0;
    Channel channel;

    private List<String> addresses;

    ApplicationService(long number, Channel channel) {
        this.number = number;
        this.channel = channel;
        this.addresses = Collections.synchronizedList(new ArrayList());
    }

    void putAddress(String address) {
        if (!addresses.contains(address)) {
            addresses.add(address);
            channelSum++;
        }
    }

    void removeAddress(String address) {
        if (addresses.contains(address)) {
            addresses.remove(address);
            channelSum--;
        }
    }

    int getChannelSum() {
        return channelSum;
    }

    List<String> getAddresses() {
        return addresses;
    }
}
