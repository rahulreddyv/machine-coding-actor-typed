package com.lld.simpleactors.actors;

import com.lld.simpleactors.actors.impl.ActorSystemImpl;

/**
 * Helper class to work with the actor contexts
 */
public class Actor {

    private static ThreadLocal<ActorRef<?>> currentActor = new ThreadLocal<>();
    private static ThreadLocal<ActorRef<?>> senderActor = new ThreadLocal<>();

    public static ActorSystem newSystem(String name) {return new ActorSystemImpl(name);}

    @SuppressWarnings("unchecked")
    public static <T> ActorRef<T> current() { return (ActorRef<T>) currentActor.get();}

    @SuppressWarnings("unchecked")
    public static <T> ActorRef<T> sender() { return (ActorRef<T>) senderActor.get();}

    public static ActorSystem system() {
        ActorRef<?> actor = current();
        return actor == null ? null : current().system();
    }

    /* Not a part of public API */
    public static void setCurrent(ActorRef<?> actor) {
        currentActor.set(actor);
    }

    /* Not a part of public API */
    public static void setSender(ActorRef<?> actor) {
        senderActor.set(actor);
    }
}
