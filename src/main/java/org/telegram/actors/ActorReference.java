package org.telegram.actors;

import org.telegram.actors.queue.ActorMessage;

/**
 * Created by ex3ndr on 17.03.14.
 */
public class ActorReference {
    private int threadId;
    private Actor actor;
    private ActorSystem system;

    public ActorReference(Actor actor, ActorSystem system, String name) {
        this.actor = actor;
        this.system = system;
        this.threadId = system.getThreadId(name);
    }

    public void talk(String message, ActorReference sender, Object... args) {
        system.sendMessage(threadId, ActorMessage.obtain(actor, message, args, sender));
    }

    public void talkDelayed(String message, ActorReference sender, long delay, Object... args) {
        system.sendMessage(threadId, ActorMessage.obtain(actor, message, args, sender), delay);
    }

    public void ping(ActorReference sender) {
        system.sendMessage(threadId, ActorMessage.obtain(actor, Actor.MESSAGE_PING, new Object[0], sender));
    }

    public void kill() {
        system.sendMessage(threadId, ActorMessage.obtain(actor, Actor.MESSAGE_KILL, new Object[0], null));
    }
}
