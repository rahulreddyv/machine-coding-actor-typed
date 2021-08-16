package com.lld.simpleactors.actors;

public interface ActorScheduler extends AutoCloseable {
    void actorCreated(Object actorId);

    void actorDisposed(Object actorId);

    void schedule(Runnable task, Object actorId);

    @Override
    default void close() {
    };
}
