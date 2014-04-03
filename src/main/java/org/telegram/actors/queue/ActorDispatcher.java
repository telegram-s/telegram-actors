package org.telegram.actors.queue;

import org.telegram.actors.dispatch.MessageDispatcher;
import org.telegram.actors.dispatch.DispatchQueue;
import org.telegram.actors.log.Logger;

/**
 * Created by ex3ndr on 03.04.14.
 */
public class ActorDispatcher extends MessageDispatcher<ActorMessage> {

    private static final String TAG = "ActorDispatcher";

    public ActorDispatcher(int priority, DispatchQueue<ActorMessage> queue) {
        super(priority, queue);
    }

    @Override
    protected void dispatchAction(ActorMessage message) {
        Logger.d(TAG, "Dispatching action: " + message.getMessage() + " for " + message.getActor().getName());
        try {
            message.getActor().receiveMessage(message.getMessage(), message.getArgs(), message.getSender());
        } catch (Exception e) {
            // e.printStackTrace();
            message.getActor().onException(e);
        }
        ActorMessage.recycle(message);
    }
}
