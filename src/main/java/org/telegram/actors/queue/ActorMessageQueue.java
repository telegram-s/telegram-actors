package org.telegram.actors.queue;

import org.telegram.actors.dispatch.SimpleDispatchQueue;

/**
 * Created by ex3ndr on 03.04.14.
 */
public class ActorMessageQueue extends SimpleDispatchQueue<ActorMessage> {
    public void postToQueueUniq(ActorMessage message, long atTime) {
        synchronized (pendingMessages) {
            for (Object msg2 : pendingMessages.toArray()) {
                Message msg = (Message) msg2;
                if (msg.action.getActor() == message.getActor() && msg.action.getMessage().equals(message.getMessage())) {
                    pendingMessages.remove(msg);
                }
            }
        }
        putToQueue(message, atTime);
    }
}
