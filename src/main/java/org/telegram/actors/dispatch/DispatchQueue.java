package org.telegram.actors.dispatch;

/**
 * Queue for dispatching objects for MessageDispatcher.
 * Implementation MIGHT BE thread-safe.
 * Thread-safe requirement is used for implementing custom queues
 * (for example with reordering or deleting)
 * <p/>
 * Author: Stepan Ex3NDR Korshakov (me@ex3ndr.com, telegram: +7-931-342-12-48)
 */
public abstract class DispatchQueue<T> {

    protected static final long FOREVER = Long.MAX_VALUE;

    private QueueListener listener;

    /**
     * Fetch message for dispatching and removing it from dispatch queue
     *
     * @return message or null if there is no message for processing
     */
    public abstract T dispatch(long time);

    /**
     * Expected delay for nearest message.
     * You might provide most accurate value as you can,
     * this will minimize unnecessary thread work.
     * For example, if you will return zero here then thread will
     * loop continuously and consume processor time.
     *
     * @return delay in ms
     */
    public abstract long waitDelay(long time);

    protected abstract void putToQueueImpl(T message, long atTime);

    /**
     * Adding message to queue
     *
     * @param message message
     * @param atTime  time (use {@link org.telegram.actors.ActorTime#currentTime()} for currentTime)
     */
    public final void putToQueue(T message, long atTime) {
        putToQueueImpl(message, atTime);
        notifyQueueChanged();
    }

    /**
     * Notification about queue change.
     * During this call methods {@link #putToQueue(T, long)},
     * {@link #waitDelay(long)}, {@link #dispatch(long)} may be called
     */
    protected void notifyQueueChanged() {
        QueueListener lListener = listener;
        if (lListener != null) {
            lListener.onQueueChanged();
        }
    }

    public QueueListener getListener() {
        return listener;
    }

    public void setListener(QueueListener listener) {
        this.listener = listener;
    }
}
