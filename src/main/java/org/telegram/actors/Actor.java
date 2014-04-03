package org.telegram.actors;

import java.util.ArrayList;

/**
 * Created by ex3ndr on 17.03.14.
 */
public abstract class Actor {
    protected static final int FLAG_ASYNC = 1;
    protected static final int FLAG_REPEATABLE = 2;

    private ActorReference reference;
    protected ActorSystem actorSystem;
    private ArrayList<MessageKind> kinds = new ArrayList<MessageKind>();
    private String name;

    public Actor(ActorSystem system, String actorName, String threadName) {
        this.name = actorName;
        this.actorSystem = system;
        this.reference = new ActorReference(this, system, threadName);

        registerMethods();
    }

    public String getName() {
        return name;
    }

    protected void registerMethods() {

    }

    protected void registerKind(String name, Class... args) {
        registerKind(name, 0, args);
    }

    protected void registerKind(String name, int flags, Class... args) {
        kinds.add(new MessageKind(name, args, flags));
    }

    public final void receiveMessage(String name, Object[] args, ActorReference sender) throws Exception {
        outer:
        for (MessageKind kind : kinds) {
            if (kind.getArgs().length != args.length) {
                continue;
            }

            if (!kind.getName().equals(name)) {
                continue;
            }

            for (int i = 0; i < kind.getArgs().length; i++) {
                if (args[i] == null) {
                    continue;
                }
                if (kind.getArgs()[i].isPrimitive()) {
                    continue;
                }
                if (!kind.getArgs()[i].isAssignableFrom(args[i].getClass())) {
                    continue outer;
                }
            }

            receive(name, args, sender);
            return;
        }
        receiveRaw(name, args, sender);
    }

    protected abstract void receive(String name, Object[] args, ActorReference sender) throws Exception;

    protected void receiveRaw(String name, Object[] args, ActorReference sender) throws Exception {
        unhandled(name, args, sender);
    }

    protected void unhandled(String name, Object[] args, ActorReference sender) {
        actorSystem.onUnhandledMessage(this, name, args, sender);
    }

    public ActorReference self() {
        return reference;
    }

    public void onException(Exception e) {

    }

    private class MessageKind {
        private String name;
        private String timeoutName;
        private Class[] args;
        private int flags;

        private MessageKind(String name, Class[] args, int flags) {
            this.name = name;
            this.args = args;
            this.flags = flags;
        }

        public String getName() {
            return name;
        }

        public Class[] getArgs() {
            return args;
        }
    }
}
