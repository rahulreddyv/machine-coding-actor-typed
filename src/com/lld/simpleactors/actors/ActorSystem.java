package com.lld.simpleactors.actors;

import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

public interface ActorSystem {
    /**
     * Initiate orderly shutdown of the actor system
     * @return Returns completableFuture of the shutdown. Result value would be shutdown reason
     */
    CompletableFuture<String> shutdown();

    /**
     *
     * @return Returns completableFuture of the shutdown. Result value would be shutdown reason
     */
    CompletableFuture<String> shutdownCompletable();

    /**
     * Gets an instance of {@link ActorBuilder} under this system
     * @param <T> actor POJO class
     * @return actor builder instance
     */
    <T> ActorBuilder<T> actorBuilder();

    /**
     * Creates a new actor under this system with a specified POJO instance factory and a name
     *
     * @param constructor factory to create
     * @param name name of the actor ref
     * @param <T> actor POJO class
     * @return ActorRef actor reference
     */
    <T> ActorRef<T> actorOf(Supplier<T> constructor, String name);
}
