package org.telegram.actors.queue;

import org.telegram.actors.Actor;
import org.telegram.actors.ActorReference;

import java.util.ArrayList;

/**
 * Created by ex3ndr on 03.04.14.
 */
public class ActorMessage {

    private static class ActorMessageHolder {
        public static final ArrayList<ActorMessage> HOLDER_INSTANCE = new ArrayList<ActorMessage>();
    }

    private static ArrayList<ActorMessage> getCache() {
        return ActorMessageHolder.HOLDER_INSTANCE;
    }

    public static ActorMessage obtain(Actor actor, String message, Object[] args, ActorReference sender) {
        ArrayList<ActorMessage> cache = getCache();
        synchronized (cache) {
            if (cache.size() > 0) {
                ActorMessage res = cache.remove(0);
                res.set(actor, message, args, sender);
                return res;
            }
        }

        return new ActorMessage(actor, message, args, sender);
    }

    public static void recycle(ActorMessage msg) {
        ArrayList<ActorMessage> cache = getCache();
        synchronized (cache) {
            cache.add(msg);
        }
    }

    private Actor actor;
    private String message;
    private Object[] args;
    private ActorReference sender;

    private ActorMessage(Actor actor, String message, Object[] args, ActorReference sender) {
        this.actor = actor;
        this.message = message;
        this.args = args;
        this.sender = sender;
    }

    public void set(Actor actor, String message, Object[] args, ActorReference sender) {
        this.actor = actor;
        this.message = message;
        this.args = args;
        this.sender = sender;
    }

    public Actor getActor() {
        return actor;
    }

    public String getMessage() {
        return message;
    }

    public Object[] getArgs() {
        return args;
    }

    public ActorReference getSender() {
        return sender;
    }
}
