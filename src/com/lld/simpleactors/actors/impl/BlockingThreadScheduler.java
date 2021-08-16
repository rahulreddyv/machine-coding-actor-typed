package com.lld.simpleactors.actors.impl;

import com.lld.simpleactors.actors.ActorScheduler;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class BlockingThreadScheduler implements ActorScheduler {

    private final BlockingQueue<Runnable> queue = new LinkedBlockingQueue<>();
    private Thread thread;

    @Override
    public void actorCreated(Object actorId) {

    }

    @Override
    public void actorDisposed(Object actorId) {

    }

    @Override
    public void schedule(Runnable task, Object actorId) {
        queue.add(task);
    }

    @Override
    public void close() {
        thread.interrupt();
    }


    /**
     * Starts message processing loop in the current thread. This method does not return until the scheduler is disposed by calling {@link #close()}. If {@link #schedule(Runnable, Object)} is called before {@link #start()}, the message will be kept
     * in the queue.
     */
    public void start() {
        this.thread = Thread.currentThread();
        try {
            while (!thread.isInterrupted()) {
                Runnable job = queue.take();
                job.run();
            }
        } catch (InterruptedException e) {
        }
    }

}
