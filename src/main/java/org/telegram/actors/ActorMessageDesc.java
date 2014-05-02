package org.telegram.actors;

import java.lang.reflect.Method;

/**
 * Created by ex3ndr on 03.04.14.
 */
public class ActorMessageDesc {
    private final String name;
    private final Class[] args;
    private boolean isSingleShot;
    private Method method;

    public ActorMessageDesc(String name, Class[] args) {
        this(name, args, null);
    }

    public ActorMessageDesc(String name, Class[] args, Method method) {
        this.name = name;
        this.args = args;
        this.method = method;
    }

    public String getName() {
        return name;
    }

    public Class[] getArgs() {
        return args;
    }

    public boolean isSingleShot() {
        return isSingleShot;
    }

    public Method getMethod() {
        return method;
    }

    public ActorMessageDesc enableSingleShot() {
        this.isSingleShot = true;
        return this;
    }

    public ActorMessageDesc disableSingleShot() {
        this.isSingleShot = false;
        return this;
    }
}
