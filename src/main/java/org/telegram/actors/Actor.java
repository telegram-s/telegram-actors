package org.telegram.actors;

import java.lang.reflect.Method;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by ex3ndr on 17.03.14.
 */
public abstract class Actor {
    public static final String MESSAGE_PING = "#ping";
    public static final String MESSAGE_KILL = "#kill";

    private static final int STATE_CREATED = 0;
    private static final int STATE_STARTED = 1;
    private static final int STATE_CLOSED = 2;

    private ActorSystem actorSystem;
    private ActorReference reference;
    private String name;
    private int state;
    private ConcurrentHashMap<String, ActorMessageDesc> kinds = new ConcurrentHashMap<String, ActorMessageDesc>();

    public Actor(ActorSystem system, String actorName, String threadName) {
        this.name = actorName;
        this.actorSystem = system;
        this.reference = new ActorReference(this, system, threadName);
        this.state = STATE_CREATED;
        registerAutoMethods();
        registerMethods();
    }

    @Deprecated
    public ActorMessageDesc findDesc(String name) {
        return kinds.get(name);
    }

    public String getName() {
        return name;
    }

    public ActorReference self() {
        return reference;
    }

    public ActorSystem getActorSystem() {
        return actorSystem;
    }

    // -------------------
    // Method Registration
    // -------------------
    private void registerAutoMethods() {
        Method[] methods = getClass().getDeclaredMethods();
        for (Method m : methods) {
            if (m.isAnnotationPresent(ActorMessage.class)) {
                m.setAccessible(true);
                ActorMessage annotation = m.getAnnotation(ActorMessage.class);
                ActorMessageDesc desc = registerKind(annotation.message(), m, m.getParameterTypes());
                if (annotation.isSingleShot()) {
                    desc.enableSingleShot();
                }
            }
        }
    }

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

    protected ActorMessageDesc registerKind(String name, Method method, Class... args) {
        if (kinds.containsKey(name)) {
            throw new UnsupportedOperationException("Already added message with name '" + name + "'");
        }
        ActorMessageDesc desc = new ActorMessageDesc(name, args, method);
        kinds.put(name, desc);
        return desc;
    }

    // ------------------
    // Message processing
    // ------------------
    public final void handleMessage(String name, Object[] args, ActorReference sender) {
        // Just ignore such messages for convenience
        if (name == null) {
            return;
        }

        // Ignore messages for closed actor
        if (state == STATE_CLOSED) {
            return;
        }

        if (MESSAGE_KILL.equals(name)) {
            if (state == STATE_STARTED) {
                // If this crash, we, crash application
                onStop();
                state = STATE_CLOSED;
            }
            state = STATE_CLOSED;
            return;
        } else {
            if (state == STATE_CREATED) {
                // If this crash, we, crash application
                onStart();
                state = STATE_STARTED;
            }
        }

        // Ignoring ping message
        if (MESSAGE_PING.equals(name)) {
            return;
        }
        try {
            ActorMessageDesc kind = kinds.get(name);
            if (kind != null) {
                if (kind.getArgs().length != args.length) {
                    incorrect(name, args, sender, "Incorrect args count, expected: " + kind.getArgs().length + ", got: " + args.length);
                    return;
                }

                for (int i = 0; i < kind.getArgs().length; i++) {
                    // TODO: Correct primitive type check
                    if (kind.getArgs()[i].isPrimitive()) {
                        continue;
                    }
                    if (!kind.getArgs()[i].isAssignableFrom(args[i].getClass())) {
                        incorrect(name, args, sender, "Incorrect argument #" + i + ", expected type: " + kind.getArgs()[i].getSimpleName() + ", got: " + args[i].getClass().getSimpleName());
                        return;
                    }
                }
            }

            if (kind != null && kind.getMethod() != null) {
                kind.getMethod().invoke(this, args);
            } else {
                handleRaw(name, args, sender);
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    protected void handleRaw(String name, Object[] args, ActorReference sender) {
        unhandled(name, args, sender);
    }

    // ------------------
    // Create/Destroy methods
    // ------------------
    protected void onStart() {

    }

    protected void onStop() {

    }

    // ------------------
    // Processing unexpected behaviours
    // ------------------
    protected void unhandled(String name, Object[] args, ActorReference sender) {

    }

    protected void incorrect(String name, Object[] args, ActorReference sender, String reason) {

    }
}
