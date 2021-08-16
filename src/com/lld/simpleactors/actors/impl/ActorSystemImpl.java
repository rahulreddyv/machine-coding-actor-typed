package com.lld.simpleactors.actors.impl;

import com.lld.simpleactors.actors.ActorBuilder;
import com.lld.simpleactors.actors.ActorRef;
import com.lld.simpleactors.actors.ActorScheduler;
import com.lld.simpleactors.actors.ActorSystem;

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class ActorSystemImpl implements ActorSystem {

    private static final int DEFAULT_FORKJOINSCHEDULER_THROUGHPUT = 10;

    private final ActorScheduler defaultScheduler;
    private final String name;

    private final RegSet<ActorImpl<?>> actors = new MapRegSet<>();
    private final ScheduledExecutorService timer;

    private final CompletableFuture<String> terminator = new CompletableFuture<>();
    private final AtomicBoolean isShuttingDown = new AtomicBoolean();


    private volatile boolean isShutDown;

    public ActorSystemImpl(String name) {
        this(name, Schedulers.newForkJoinPoolScheduler(DEFAULT_FORKJOINSCHEDULER_THROUGHPUT));
    }

    public ActorSystemImpl(String name, ActorScheduler defaultScheduler) {
        this.name = name;
        this.defaultScheduler = defaultScheduler;
        this.timer = Executors.newSingleThreadScheduledExecutor(runnable -> {
            Thread thread = new Thread(runnable, "actr:" + name + ":timer");
            thread.setPriority(8);
            return thread;
        });
    }

    public boolean checkShutdown() {
        return isShutDown;
    }

    public void add(ActorImpl<?> actorRef) {
        //CheckShutdown
        actorRef.reg(actors.add(actorRef));
    }

    @Override
    public CompletableFuture<String> shutdown() {
        return null;
    }

    @Override
    public CompletableFuture<String> shutdownCompletable() {
        return null;
    }

    @Override
    public <T> ActorBuilder<T> actorBuilder() {
        return new ActorBuilderImpl<T>(this);
    }

    @Override
    public <T> ActorRef<T> actorOf(Supplier<T> constructor, String name) {
        return this.<T> actorBuilder().constructor(constructor).name(name).build();
    }

    public <T> ActorRef<T> actorOf(Supplier<T> constructor) {
        return actorOf(constructor, Long.toHexString(new Random().nextLong()));
    }

    public static class ActorBuilderImpl<T> implements ActorBuilder<T> {
        private ActorSystemImpl actorSystem;
        private T object;
        private Supplier<T> constructor;
        private Consumer<T> destructor;
        private ActorScheduler scheduler;
        private String name;
        private BiConsumer<T, Exception> exceptionHandler;

        private ActorBuilderImpl(ActorSystemImpl actorSystem) {
            actorSystem.checkShutdown();
            this.actorSystem = actorSystem;
            this.scheduler = actorSystem.defaultScheduler;
            this.exceptionHandler = (obj, ex) -> ex.printStackTrace();
        }

        /**
         * Adds an existing actor POJO class instance to be used with the actor being constructed.
         *
         * @param object actor POJO class instance
         * @return this builder
         */
        @Override
        public ActorBuilder<T> object(T object) {
            this.object = object;
            return this;
        }

        /**
         * Adds a factory for POJO class instance creation to be used with the actor being constructed.
         *
         * Constructor will be called during {@link #build()} call in a synchronous manner
         *
         * @param constructor POJO class instance factory
         * @return this builder
         */
        @Override
        public ActorBuilder<T> constructor(Supplier<T> constructor) {
            this.constructor = constructor;
            return this;
        }

        /**
         * Adds a destructor to be called in actor thread context when the actor is being destroyed.
         *
         * @param destructor action to be called on actor destruction
         * @return this builder
         */
        @Override
        public ActorBuilder<T> destructor(Consumer<T> destructor) {
            this.destructor = destructor;
            return this;
        }

        /**
         * Sets a name for the actor being constructed.
         *
         * @param name actor name
         * @return this builder
         */
        @Override
        public ActorBuilder<T> name(String name) {
            this.name = name;
            return this;
        }

        /**
         * Sets a scheduler for the actor being constructed.
         *
         * @param scheduler scheduler to be used for the actor being constructed
         * @return this builder
         */
        @Override
        public ActorBuilder<T> scheduler(ActorScheduler scheduler) {
            this.scheduler = scheduler;
            return this;
        }

        /**
         * Sets an exception handler for the actor being constructed.
         *
         * Exception handler is triggered in actor's thread context whenever an exception occurs in actor's <i>ask</i>, <i>tell</i> or <i>later</i> call. Note that the exception handler is ignored when calling methods returning CallableFuture as in that case the exception is passed directly to the future.
         *
         * @param exceptionHandler exception handler to be triggered
         * @return this builder
         */
        @Override
        public ActorBuilder<T> exceptionHandler(BiConsumer<T, Exception> exceptionHandler) {
            this.exceptionHandler = exceptionHandler;
            return this;
        }

        /**
         * Creates an actor using this builder.
         *
         * @return newly create ActorRef instance
         */
        @Override
        public ActorRef<T> build() {
            if (constructor != null && object != null)
                throw new IllegalArgumentException("Not allowed to provide both object and constructor");
            if (constructor == null && object == null)
                throw new IllegalArgumentException("Provide either object or constructor");

            ActorRef<T> actorRef = new ActorImpl<T>(object, constructor, scheduler, actorSystem, name, exceptionHandler, destructor);
            return actorRef;
        }

    }
}
