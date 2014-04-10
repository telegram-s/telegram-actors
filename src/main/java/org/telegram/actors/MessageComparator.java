package org.telegram.actors;

/**
 * Created by ex3ndr on 10.04.14.
 */
public interface MessageComparator {
    public boolean checkMessage(String name, Object[] args);
}
