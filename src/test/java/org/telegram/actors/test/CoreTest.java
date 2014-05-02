package org.telegram.actors.test;

import org.jodah.concurrentunit.Waiter;
import org.junit.Assert;
import org.junit.Test;
import org.telegram.actors.*;

/**
 * Created by ex3ndr on 02.05.14.
 */
public class CoreTest {

    @Test(timeout = 1000)
    public void testThreadCreation() {
        ActorSystem actorSystem = new ActorSystem();
        for (int i = 0; i < 50; i++) {
            Assert.assertTrue(actorSystem.getThreadId("thread" + i) >= 0);
        }
    }

    @Test(timeout = 1000)
    public void testActorQueue() throws Throwable {
        final Waiter waiter = new Waiter();

        ActorSystem actorSystem = new ActorSystem();
        ActorReference reference = new Actor(actorSystem, "testActor", "actor_thread") {
            private int state = -1;

            @Override
            protected void onStart() {
                if (state == -1) {
                    state = 0;
                } else {
                    waiter.fail("Double onStart call");
                }
            }

            @Override
            protected void handleRaw(String name, Object[] args, ActorReference sender) {
                if (name.startsWith("counter_")) {
                    int count = Integer.parseInt(name.substring("counter_".length()));
                    if (count != state) {
                        waiter.fail("Incorrect execution order");
                        return;
                    }
                    state = count + 1;
                    if (count == 9) {
                        waiter.resume();
                    }
                } else if (name.equals("complete")) {
                    if (state == 10) {
                        state = 11;
                        waiter.resume();
                    } else if (state < 10) {
                        waiter.fail("Early 'complete'");
                    } else {
                        waiter.fail("Double 'complete' message");
                    }

                }
            }
        }.self();

        for (int i = 0; i < 10; i++) {
            reference.talk("counter_" + i, null);
        }
        reference.talk("complete", null);
        waiter.await();
    }

    @Test(timeout = 1000)
    public void testAnnotations() throws Throwable {
        final Waiter waiter = new Waiter();
        ActorSystem actorSystem = new ActorSystem();
        ActorReference reference = new Actor(actorSystem, "testActor", "actor_thread") {

            private int state = -1;

            @ActorMessage(message = "state0")
            public void onState0() {
                onState(0);
            }

            @ActorMessage(message = "state1")
            public void onState1() {
                onState(1);
            }

            @ActorMessage(message = "state2")
            public void onState2() {
                onState(2);
            }

            private void onState(int index) {
                if (index != state) {
                    waiter.fail("Incorrect execution order");
                    return;
                }
                state = index + 1;
                if (index == 2) {
                    waiter.resume();
                }
            }

            @Override
            protected void onStart() {
                if (state == -1) {
                    state = 0;
                } else {
                    waiter.fail("Double onStart call");
                }
            }
        }.self();

        reference.talk("state0", null);
        reference.talk("state1", null);
        reference.talk("state2", null);
        waiter.await();
    }

    @Test(timeout = 1000)
    public void testSingleShot() throws Throwable {
        final Waiter waiter = new Waiter();
        ActorSystem actorSystem = new ActorSystem();
        ActorReference reference = new Actor(actorSystem, "testActor", "actor_thread") {

            private boolean isCalled = false;

            @Override
            protected void onStart() {
                for (int i = 0; i < 10; i++) {
                    self().talk("mainMessage", self());
                }
                self().talk("complete", self());
            }

            @ActorMessage(message = "mainMessage", isSingleShot = true)
            public void onMainMessage() {
                if (isCalled) {
                    waiter.fail();
                } else {
                    isCalled = true;
                }
            }

            @ActorMessage(message = "complete")
            public void onComplete() {
                waiter.resume();
            }
        }.self();

        reference.ping(null);
        waiter.await();
    }

}
