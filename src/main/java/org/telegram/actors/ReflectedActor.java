package org.telegram.actors;

import java.lang.reflect.Method;
import java.util.HashMap;

/**
 * Created by ex3ndr on 24.03.14.
 */
public abstract class ReflectedActor extends Actor {

    private HashMap<String, Method> eventMethods;
    private Method[] methods;

    public ReflectedActor(ActorSystem system, String actorName, String threadName) {
        super(system, actorName, threadName);
    }

    private void loadMethods() {
        if (methods == null) {
            methods = getClass().getDeclaredMethods();
        }
        if (eventMethods == null) {
            eventMethods = new HashMap<String, Method>();
        }
    }

    @Override
    protected void registerMethods() {
        loadMethods();
        for (Method m : methods) {
            String methodName = m.getName();
            if (methodName.startsWith("on") && methodName.endsWith("Message")) {
                m.setAccessible(true);
                String stateName = methodName.substring(2, methodName.length() - 7);
                stateName = stateName.substring(0, 1).toLowerCase() + stateName.substring(1);
                registerKind(stateName, m.getParameterTypes());
                eventMethods.put(stateName, m);
            }
        }
    }

    protected ActorMessageDesc registerMethod(String name) {
        loadMethods();
        for (Method m : methods) {
            String methodName = m.getName();
            if (methodName.startsWith("on") && methodName.endsWith("Message")) {
                m.setAccessible(true);
                String stateName = methodName.substring(2, methodName.length() - 7);
                stateName = stateName.substring(0, 1).toLowerCase() + stateName.substring(1);
                if (stateName.equals(name)) {
                    eventMethods.put(stateName, m);
                    return registerKind(stateName, m.getParameterTypes());
                }


            }
        }
        throw new UnsupportedOperationException("Unable to find method for '" + name + "'");
    }

    @Override
    protected void process(String name, Object[] args, ActorReference sender) throws Exception {
        eventMethods.get(name).invoke(this, args);
    }
}