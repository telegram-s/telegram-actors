package org.telegram.actors.dispatch;

import static org.telegram.actors.ActorTime.currentTime;

/**
 * MessageDispatcher is used for dispatching messages on it's own thread.
 * Automatically starts new thread for dispatching.
 * Class is completely thread-safe and it could collect actions before real thread start.
 * <p/>
 * Author: Stepan Ex3NDR Korshakov (me@ex3ndr.com, telegram: +7-931-342-12-48)
 */
public abstract class MessageDispatcher<T> {

    private final Thread thread;
    final private DispatchQueue<T> queue;

    private boolean isClosed = false;

    public MessageDispatcher() {
        this(Thread.NORM_PRIORITY);
    }

    public MessageDispatcher(int priority) {
        this(priority, new SimpleDispatchQueue<T>());
    }

    public MessageDispatcher(DispatchQueue<T> queue) {
        this(Thread.NORM_PRIORITY, queue);
    }

    public MessageDispatcher(int priority, final DispatchQueue<T> queue) {
        this.queue = queue;
        this.thread = new Thread() {
            @Override
            public void run() {
                while (!isClosed) {
                    T action = queue.dispatch(currentTime());
                    if (action == null) {
                        synchronized (this) {
                            try {
                                long delay = queue.waitDelay(currentTime());
                                if (delay > 0) {
                                    wait(delay);
                                }
                                continue;
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                                return;
                            }
                        }
                    }

                    try {
                        dispatchAction(action);
                    } catch (Throwable t) {
                        // Possibly danger situation, but i hope this will not corrupt JVM
                        // For example: on Android we could always continue execution after OutOfMemoryError
                        // Anyway, better to catch all errors manually in dispatchAction
                        // t.printStackTrace();
                    }
                }
            }
        };
        this.queue.setListener(new QueueListener() {
            @Override
            public void onQueueChanged() {
                synchronized (thread) {
                    thread.notifyAll();
                }
            }
        });
        thread.setPriority(priority);
        thread.start();
    }

    public void close() {
        isClosed = true;
    }

    public DispatchQueue<T> getQueue() {
        return queue;
    }

    public void postAction(T action) {
        postAction(action, 0);
    }

    public void postAction(T action, long delay) {
        queue.putToQueue(action, currentTime() + delay);
    }

    protected abstract void dispatchAction(T object);
}
