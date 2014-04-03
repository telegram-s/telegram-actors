package org.telegram.actors;

/**
 * Created by ex3ndr on 03.04.14.
 */
public class ActorTime {
    public static long currentTime() {
        return System.nanoTime() / 1000000;
    }
}
