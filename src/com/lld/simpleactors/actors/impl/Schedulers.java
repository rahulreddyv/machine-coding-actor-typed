package com.lld.simpleactors.actors.impl;

import com.lld.simpleactors.actors.ActorScheduler;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ThreadFactory;

public class Schedulers {
    /**
     * Creates a scheduler based on the shared ForkJoinPool.
     * @param throughput maximum number of pending actor messages to be processed at once
     * @return scheduler
     */
    public static ActorScheduler newForkJoinPoolScheduler(int throughput) {
        return new ExecutorBasedScheduler(ForkJoinPool.commonPool(), throughput);
    }

    /**
     * Creates a scheduler based on a user-provided ExecuterService.
     * @param executorService executor service for scheduling the tasks
     * @param throughput maximum number of pending actor messages to be processed at once
     * @return scheduler
     */
    public static ActorScheduler newExecutorBasedScheduler(ExecutorService executorService, int throughput) {
        return new ExecutorBasedScheduler(executorService, throughput);
    }

    /**
     * Creates a scheduler based on a thread pool with a fixed number of threads.
     * @param threads number of threads in the thread pool
     * @param throughput maximum number of pending actor messages to be processed at once
     * @return scheduler
     */
    public static ActorScheduler newFixedThreadPoolScheduler(int threads, int throughput) {
        return new ExecutorBasedScheduler(Executors.newFixedThreadPool(threads, runnable -> new Thread(runnable, "actor:fixed")), throughput);
    }

    /**
     * Creates a scheduler that processed all the actors messages sequentially in a single
     * user-supplied thread. See {@link BlockingThreadScheduler} for details.
     * @return scheduler
     */
    public static BlockingThreadScheduler newBlockingThreadScheduler() {
        return new BlockingThreadScheduler();
    }

    /**
     * Creates a scheduler using a single thread for all actor calls.
     * @return scheduler
     */
    public static ActorScheduler newSingleThreadScheduler() {
        return new SingleThreadScheduler();
    }

    /**
     * Creates a scheduler that creates a single-thread executor for each actor.
     * @return scheduler
     */
    public static ActorScheduler newThreadPerActorScheduler() {
        return new ThreadPerActorScheduler("actor");
    }

    /**
     * Creates a scheduler that creates a single-thread executor for each actor.
     * @param threadFactory thread factory to be used for thread creation
     * @return scheduler
     */
    public static ActorScheduler newThreadPerActorScheduler(ThreadFactory threadFactory) {
        return new ThreadPerActorScheduler(threadFactory);
    }

    /**
     * Creates a scheduler that creates a single-thread executor for each actor.
     * @param name thread name
     * @return scheduler
     */
    public static ActorScheduler newThreadPerActorScheduler(String name) {
        return new ThreadPerActorScheduler(name);
    }
}
