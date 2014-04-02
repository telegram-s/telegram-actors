package org.telegram.actor;

/**
 * Created by ex3ndr on 17.03.14.
 */
public class ActorReference {
    private ActorThreadRaw thread;
    private Actor actor;

    public ActorReference(Actor actor, ActorThreadRaw thread) {
        this.actor = actor;
        this.thread = thread;
    }

    public void talk(String message, ActorReference sender, Object... args) {
        thread.deliverMessage(actor, message, args, sender);
    }

    public void talkDelayed(String message, ActorReference sender, long delay, Object... args) {
        thread.deliverMessageDelayed(actor, message, args, sender, delay);
    }
}
