package org.telegram.actors;

import java.util.HashMap;

/**
 * Created by ex3ndr on 17.03.14.
 */
public class ActorSystem {
    private static final String TAG = "ActorSystem";
    private HashMap<String, ActorThreadRaw> threads;

    public ActorSystem() {
        threads = new HashMap<String, ActorThreadRaw>();
    }

    public void addThread(String name, int priority) {
        threads.put(name, new ActorThreadRaw(name, priority));
    }

    public void addThread(String name) {
        threads.put(name, new ActorThreadRaw(name, Thread.MIN_PRIORITY));
    }

    public ActorThreadRaw findThread(String name) {
        return threads.get(name);
    }

    public void runThreads() {
        for (ActorThreadRaw thread : threads.values()) {
            thread.start();
        }
    }

    public void onUnhandledMessage(Actor actor, String name, Object[] args, ActorReference reference) {
    }
}