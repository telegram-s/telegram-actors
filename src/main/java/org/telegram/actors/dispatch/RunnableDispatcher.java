package org.telegram.actors.dispatch;

/**
 * RunnableDispatcher is MessageDispatcher implementation for executing
 * various Runnable
 *
 * Author: Stepan Ex3NDR Korshakov (me@ex3ndr.com, telegram: +7-931-342-12-48)
 */
public class RunnableDispatcher extends MessageDispatcher<Runnable> {
    @Override
    protected void dispatchAction(Runnable object) {
        object.run();
    }
}
