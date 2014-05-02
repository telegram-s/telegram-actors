package org.telegram.actors;

/**
 * Created by ex3ndr on 19.04.14.
 */
public class ActionActor extends Actor {

    private Object[] actorArgs;
    private long timeout;

    public ActionActor(Object[] actorArgs, long timeout, ActorSystem system, String actorName, String threadName) {
        super(system, actorName, threadName);
        this.actorArgs = actorArgs;
        this.timeout = timeout;
    }

    protected void onStarted() {

    }

    protected void complete() {

    }

    protected void onEnded() {

    }

    protected void notifyClients(Object... args) {

    }
}
