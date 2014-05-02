package org.telegram.actors;

import org.telegram.actors.dispatch.RunnableDispatcher;
import org.telegram.actors.queue.ActorDispatcher;
import org.telegram.actors.queue.ActorMessage;
import org.telegram.actors.queue.ActorMessageQueue;

/**
 * Created by ex3ndr on 17.03.14.
 */
public class ActorSystem {

    private static final int HOLDERS_SIZE = 16;

    private static final String TAG = "ActorSystem";

    // High performance singleton
    private static class DispatcherHolder {
        public static final RunnableDispatcher HOLDER_INSTANCE = new RunnableDispatcher();
    }

    private static RunnableDispatcher getThreadDispatcher() {
        return DispatcherHolder.HOLDER_INSTANCE;
    }

    private ThreadHolder[] holders;
    private int holdersCount;

    public ActorSystem() {
        holders = new ThreadHolder[HOLDERS_SIZE];
        holdersCount = 0;
    }

    private void checkHolders() {
        if (holdersCount == holders.length) {
            ThreadHolder[] nHolders = new ThreadHolder[holders.length + HOLDERS_SIZE];
            for (int i = 0; i < holders.length; i++) {
                nHolders[i] = holders[i];
            }
            holders = nHolders;
        }
    }

    private void checkThread(final int id) {
        if (!holders[id].isCreated) {
            holders[id].isCreated = true;
            getThreadDispatcher().postAction(new Runnable() {
                @Override
                public void run() {
                    holders[id].actorThread = new ActorDispatcher(holders[id].threadPriority, holders[id].queue);
                }
            });
        }
    }

    public void addThread(String name) {
        addThread(name, Thread.NORM_PRIORITY);
    }

    public void addThread(String name, int priority) {
        checkHolders();
        holders[holdersCount++] = new ThreadHolder(name, priority);
    }

    public int getThreadId(String name) {
        for (int i = 0; i < holdersCount; i++) {
            if (holders[i].name.equals(name)) {
                return i;
            }
        }
        // Automatically add thread
        addThread(name);
        return getThreadId(name);
    }

    public void sendMessage(final int threadId, ActorMessage message) {
        sendMessage(threadId, message, 0);
    }

    public void sendMessage(final int threadId, ActorMessage message, long delay) {
        if (threadId < 0 || threadId >= holdersCount) {
            return;
        }
        // Logger.d(TAG, "Sending message " + message.getMessage() + " to " + message.getActor().getName());
        ActorMessageDesc desc = message.getActor().findDesc(message.getMessage());
        if (desc != null && desc.isSingleShot()) {
            holders[threadId].queue.postToQueueUniq(message, ActorTime.currentTime() + delay);
        } else {
            holders[threadId].queue.putToQueue(message, ActorTime.currentTime() + delay);
        }
        checkThread(threadId);
    }

    public void removeMessage(final int threadId, MessageComparator messageFilter) {
        if (threadId < 0 || threadId >= holdersCount) {
            return;
        }
        holders[threadId].queue.removeMessage(messageFilter);
    }

    public void close() {
        for (int i = 0; i < holdersCount; i++) {
            if (holders[i].actorThread != null) {
                holders[i].actorThread.close();
            }
        }
    }

    private class ThreadHolder {
        public String name;
        public int threadPriority;
        public ActorMessageQueue queue;

        public ActorDispatcher actorThread;
        public boolean isCreated = false;

        private ThreadHolder(String name, int threadPriority) {
            this.name = name;
            this.threadPriority = threadPriority;
            this.queue = new ActorMessageQueue();
        }
    }
}