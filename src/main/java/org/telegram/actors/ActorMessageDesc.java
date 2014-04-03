package org.telegram.actors;

/**
 * Created by ex3ndr on 03.04.14.
 */
public class ActorMessageDesc {
    private static final long DEFAULT_BACKOFF_MIN = 100;
    private static final long DEFAULT_BACKOFF_MAX = 15000;
    private static final int DEFAULT_BACKOFF_ATTEMPTS = 50;

    private final String name;
    private final Class[] args;

    private boolean isBackOffEnabled;
    private boolean isSingleShot;
    private long backoffMinDelay;
    private long backoffMaxDelay;
    private int backoffMaxDelayAttempts;

    public ActorMessageDesc(String name, Class[] args) {
        this.name = name;
        this.args = args;
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

    public boolean isBackOffEnabled() {
        return isBackOffEnabled;
    }

    public long getBackoffMinDelay() {
        return backoffMinDelay;
    }

    public long getBackoffMaxDelay() {
        return backoffMaxDelay;
    }

    public int getBackoffMaxDelayAttempts() {
        return backoffMaxDelayAttempts;
    }

    public ActorMessageDesc enabledBackOff() {
        isBackOffEnabled = true;
        backoffMinDelay = DEFAULT_BACKOFF_MIN;
        backoffMaxDelay = DEFAULT_BACKOFF_MAX;
        backoffMaxDelayAttempts = DEFAULT_BACKOFF_ATTEMPTS;
        return this;
    }

    public ActorMessageDesc enabledBackOff(long backoffMinDelay, long backoffMaxDelay, int backoffMaxDelayAttempts) {
        this.isBackOffEnabled = true;
        this.backoffMinDelay = backoffMinDelay;
        this.backoffMaxDelay = backoffMaxDelay;
        this.backoffMaxDelayAttempts = backoffMaxDelayAttempts;
        return this;
    }

    public ActorMessageDesc disableBackOff() {
        this.isBackOffEnabled = false;
        return this;
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
