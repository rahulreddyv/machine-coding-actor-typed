package com.lld.simpleactors.actors;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

public interface ActorBuilder<T> {
    /**
     * Adds an existing POJO to be used with the actor being constructed
     * @param object actor POJO instance
     * @return this builder
     */
    ActorBuilder<T> object(T object);

    /**
     * Adds a factory for POJO class instance creation to be used with the actor being created
     * @param constructor POJO class instance factory
     * @return this builder
     */
    ActorBuilder<T> constructor(Supplier<T> constructor);

    /**
     * Adds a destructor for POJO class instance creation to be used with the actor being created
     * @param destructor POJO class instance factory
     * @return this builder
     */
    ActorBuilder<T> destructor(Consumer<T> destructor);

    /**
     * Sets a name of the actor being constructed
     * @param name actor name
     * @return this builder
     */
    ActorBuilder<T> name(String name);

    /**
     * Sets a scheduler for the actor being constructed
     *
     * @param scheduler scheduler to be used for the actor being constructed
     * @return this builder
     */
    ActorBuilder<T> scheduler(ActorScheduler scheduler);

    /**
     * Sets an exception handler for the actor being constructed
     *
     *
     */
    ActorBuilder<T> exceptionHandler(BiConsumer<T, Exception> exceptionHandler);

    /**
     * Creates an actor using this builder.
     *
     * @return newly create ActorRef instance
     */
    ActorRef<T> build();
}
