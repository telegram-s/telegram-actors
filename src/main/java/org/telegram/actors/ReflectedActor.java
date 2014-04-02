package org.telegram.actor;

import java.lang.reflect.Method;
import java.util.HashMap;

/**
 * Created by ex3ndr on 24.03.14.
 */
public abstract class ReflectedActor extends Actor {

    private HashMap<String, Method> eventMethods;

    public ReflectedActor(ActorSystem system, String actorName, String threadName) {
        super(system, actorName, threadName);
    }

    @Override
    protected void registerMethods() {
        eventMethods = new HashMap<String, Method>();
        Method[] methods = getClass().getDeclaredMethods();
        for (Method m : methods) {
            String methodName = m.getName();
            if (methodName.startsWith("on") && methodName.endsWith("Message")) {
                m.setAccessible(true);
                String stateName = methodName.substring(2, methodName.length() - 7);
                stateName = stateName.substring(0, 1).toLowerCase() + stateName.substring(1);
                registerKind(stateName, m.getParameterTypes());
                eventMethods.put(stateName, m);
            }
//            if (methodName.startsWith("on") && methodName.endsWith("Async")) {
//                m.setAccessible(true);
//                String stateName = methodName.substring(2, methodName.length() - 7);
//                // Async methods
//            }
        }
    }

    @Override
    protected void receive(String name, Object[] args, ActorReference sender) throws Exception {
        eventMethods.get(name).invoke(this, args);
    }
}