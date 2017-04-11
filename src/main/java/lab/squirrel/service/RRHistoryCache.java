package lab.squirrel.service;

import lab.squirrel.pojo.CallbackMsg;
import lab.squirrel.pojo.IncomingXmlMsg;

import java.util.LinkedList;

public class RRHistoryCache {
    private static final int CAPACITY = 50;
    private LinkedList<Object[]> cache;
    private int cap;

    public RRHistoryCache() {
        this(CAPACITY);
    }

    public RRHistoryCache(int cap) {
        cache = new LinkedList<>();
        this.cap = cap;
    }

    public void put(IncomingXmlMsg in, CallbackMsg response) {
        if (cache.size() >= cap) {
            cache.poll();
        }
        cache.offer(new Object[]{in.getMsgId(), response});
    }

    public CallbackMsg get(IncomingXmlMsg in) {
        for (Object[] objects : cache) {
            if (objects[0].toString().equals(in.getMsgId())) {
                return (CallbackMsg) objects[1];
            }
        }
        return null;
    }


}
