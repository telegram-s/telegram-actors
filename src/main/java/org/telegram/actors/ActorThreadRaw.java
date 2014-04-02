package org.telegram.actors;

import java.util.ArrayList;

/**
 * Created by ex3ndr on 02.04.14.
 */
public class ActorThreadRaw extends Thread {
    private ArrayList<DeliverMessage> pendingMessages = new ArrayList<DeliverMessage>();

    private boolean isClosed = false;

    public ActorThreadRaw(String threadName, int priority) {
        super(threadName);
        setPriority(priority);
    }

    private DeliverMessage pickMessage() {
        long currentDate = System.nanoTime();
        synchronized (pendingMessages) {
            for (DeliverMessage message : pendingMessages) {
                if (message.destNanoTime < currentDate) {
                    pendingMessages.remove(message);
                    return message;
                }
            }
        }
        return null;
    }

    private long getMinDelta() {
        long currentDate = System.nanoTime();
        long res = 15000;
        synchronized (pendingMessages) {
            for (DeliverMessage message : pendingMessages) {
                res = Math.min((message.destNanoTime - currentDate) / 1000000, res);
            }
        }
        return Math.max(res, 0);
    }

    @Override
    public void run() {
        while (!isClosed) {
            DeliverMessage message = pickMessage();
            if (message == null) {
                synchronized (pendingMessages) {
                    try {
                        pendingMessages.wait(getMinDelta());
                        continue;
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        return;
                    }
                }
            }

            try {
                message.actor.receiveMessage(message.message, message.args, message.sender);
            } catch (Exception e) {
                message.actor.onException(e);
            }
        }
    }

    private void postMessage(DeliverMessage message) {
        synchronized (pendingMessages) {
            pendingMessages.add(message);
            pendingMessages.notifyAll();
        }
    }

    public void deliverMessage(Actor reference, String message, Object[] args, ActorReference sender) {
        deliverMessageDelayed(reference, message, args, sender, 0);
    }

    public void deliverMessageDelayed(Actor reference, String message, Object[] args, ActorReference sender, long delay) {
        DeliverMessage deliverMessage = new DeliverMessage();
        deliverMessage.actor = reference;
        deliverMessage.message = message;
        deliverMessage.args = args;
        deliverMessage.sender = sender;
        deliverMessage.destNanoTime = System.nanoTime() + delay * 1000000L;
        postMessage(deliverMessage);
    }

    private class DeliverMessage {
        public Actor actor;
        public String message;
        public Object[] args;
        public ActorReference sender;
        public long destNanoTime;

        private DeliverMessage() {

        }
    }
}
