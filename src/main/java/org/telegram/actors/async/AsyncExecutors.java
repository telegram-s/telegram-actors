package org.telegram.actors.async;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by ex3ndr on 18.04.14.
 */
public class AsyncExecutors {

    private ExecutorService service = Executors.newCachedThreadPool();

    public void execute(Runnable runnable) {
        service.execute(runnable);
    }
}
