package com.lld.simpleactors.actors;

import java.util.function.Consumer;
import java.util.function.Function;

public interface ActorRef<T> {

    ActorSystem system();

    void tell(Consumer<T> action);

    void later(Consumer<T> action, long ms);

    /**
     *
     * @param action
     * @param consumer
     * @param <R>
     */
    <R> void ask(Function<T, R> action, Consumer<R> consumer);
}
