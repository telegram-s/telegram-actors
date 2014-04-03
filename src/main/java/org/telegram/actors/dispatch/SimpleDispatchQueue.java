package org.telegram.actors.dispatch;

import java.util.ArrayList;

/**
 * Created by ex3ndr on 03.04.14.
 */
public class SimpleDispatchQueue<T> extends DispatchQueue<T> {

    protected final ArrayList<Message> pendingMessages = new ArrayList<Message>();
    protected final ArrayList<Message> freeMessages = new ArrayList<Message>();

    @Override
    public T dispatch(long time) {
        synchronized (pendingMessages) {
            for (Message message : pendingMessages) {
                if (message.destTime <= time) {
                    pendingMessages.remove(message);
                    T res = message.action;
                    recycle(message);
                    return res;
                }
            }
        }
        return null;
    }

    @Override
    public long waitDelay(long time) {
        long res = FOREVER;
        synchronized (pendingMessages) {
            for (Message message : pendingMessages) {
                res = Math.min(message.destTime - time, res);
            }
        }
        return Math.max(res, 0);
    }

    @Override
    public void putToQueueImpl(T action, long atTime) {
        Message message = obtainMessage();
        message.setMessage(action, atTime);
        synchronized (pendingMessages) {
            pendingMessages.add(message);
        }
    }

    protected Message obtainMessage() {
        synchronized (freeMessages) {
            if (freeMessages.size() > 0) {
                return freeMessages.remove(0);
            }
        }
        return new Message();
    }

    protected void recycle(Message message) {
        synchronized (freeMessages) {
            freeMessages.add(message);
        }
    }

    protected class Message {
        public long destTime;
        public T action;

        public void setMessage(T action, long destTime) {
            this.action = action;
            this.destTime = destTime;
        }
    }
}
