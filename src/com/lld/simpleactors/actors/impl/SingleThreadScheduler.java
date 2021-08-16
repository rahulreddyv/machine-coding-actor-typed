package com.lld.simpleactors.actors.impl;

import com.lld.simpleactors.actors.ActorScheduler;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SingleThreadScheduler implements ActorScheduler {
    private final ExecutorService executor;

    public SingleThreadScheduler() {
        this.executor = Executors.newSingleThreadExecutor(runnable -> new Thread(runnable, "actor:single"));
    }

    @Override
    public void actorCreated(Object actorId) {

    }

    @Override
    public void actorDisposed(Object actorId) {

    }

    @Override
    public void schedule(Runnable task, Object actorId) {
        if (!executor.isShutdown() && !executor.isTerminated()) {
            executor.submit(task);
        }
    }

    @Override
    public void close() {
        executor.shutdown();
    }
}
