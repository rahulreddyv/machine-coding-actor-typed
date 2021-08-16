package com.lld.simpleactors.actors.impl;

import com.lld.simpleactors.actors.ActorScheduler;

import java.util.Map;
import java.util.concurrent.*;

public class ThreadPerActorScheduler implements ActorScheduler {

    private Map<Object, ExecutorService> executors = new ConcurrentHashMap<>();
    private ThreadFactory threadFactory;

    public ThreadPerActorScheduler(String name) {
        this(runnable -> new Thread(runnable, "actor:" + name));
    }

    public ThreadPerActorScheduler() {this(Thread::new);}

    public ThreadPerActorScheduler(ThreadFactory threadFactory) {
        this.threadFactory = threadFactory;
    }

    @Override
    public void actorCreated(Object actorId) {
        executors.put(actorId, Executors.newSingleThreadExecutor(threadFactory));
    }

    @Override
    public void actorDisposed(Object actorId) {
        ExecutorService service = executors.remove(actorId);
        service.shutdown();
    }

    @Override
    public void schedule(Runnable task, Object actorId) {
        ExecutorService executor = executors.get(actorId);
        if (!executor.isShutdown()) {
            executor.execute(task);
        }
    }

    @Override
    public void close() {
        executors.values().forEach(ExecutorService::shutdown);
    }
}
