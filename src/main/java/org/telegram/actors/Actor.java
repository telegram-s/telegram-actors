package org.telegram.actors;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by ex3ndr on 17.03.14.
 */
public abstract class Actor {

    private ActorReference reference;
    protected ActorSystem actorSystem;
    private ConcurrentHashMap<String, ActorMessageDesc> kinds = new ConcurrentHashMap<String, ActorMessageDesc>();
    private String name;
    private boolean isStarted = false;

    public Actor(ActorSystem system, String actorName, String threadName) {
        this.name = actorName;
        this.actorSystem = system;
        this.reference = new ActorReference(this, system, threadName);
        registerMethods();
    }

    public ActorMessageDesc findDesc(String name) {
        return kinds.get(name);
    }

    public String getName() {
        return name;
    }

    public ActorReference self() {
        return reference;
    }

    // -------------------
    // Method Registration
    // -------------------

    protected void registerMethods() {

    }

    protected ActorMessageDesc registerKind(String name, Class... args) {
        if (kinds.containsKey(name)) {
            throw new UnsupportedOperationException("Already added message with name '" + name + "'");
        }
        ActorMessageDesc desc = new ActorMessageDesc(name, args);
        kinds.put(name, desc);
        return desc;
    }

    // ------------------
    // Message processing
    // ------------------

    public final void receiveMessage(String name, Object[] args, ActorReference sender) throws Exception {
        if (!isStarted) {
            isStarted = true;
            onStart();
        }

        ActorMessageDesc kind = kinds.get(name);
        if (kind == null) {
            unhandled(name, args, sender);
            return;
        }

        if (kind.getArgs().length != args.length) {
            unhandled(name, args, sender);
            return;
        }

        if (!kind.getName().equals(name)) {
            unhandled(name, args, sender);
            return;
        }

        for (int i = 0; i < kind.getArgs().length; i++) {
            if (args[i] == null) {
                continue;
            }
            if (kind.getArgs()[i].isPrimitive()) {
                continue;
            }
            if (!kind.getArgs()[i].isAssignableFrom(args[i].getClass())) {
                unhandled(name, args, sender);
                return;
            }
        }

        processInt(kind, name, args, sender);
    }

    private void processInt(ActorMessageDesc kind, String name, Object[] args, ActorReference sender) throws Exception {
        if (kind.isBackOffEnabled()) {
            try {
                process(name, args, sender);
            } catch (Exception e) {
                self().talkDelayed(name, sender, 1000, args);
            }
        } else {
            process(name, args, sender);
        }
    }

    protected abstract void process(String name, Object[] args, ActorReference sender) throws Exception;

    // ------------------
    // Various methods
    // ------------------
    protected void onStart() {

    }

    public void onException(Exception e) {

    }

    protected void unhandled(String name, Object[] args, ActorReference sender) {
        actorSystem.onUnhandledMessage(this, name, args, sender);
    }
}
