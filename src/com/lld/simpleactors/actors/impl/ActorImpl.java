package com.lld.simpleactors.actors.impl;

import com.lld.simpleactors.actors.Actor;
import com.lld.simpleactors.actors.ActorRef;
import com.lld.simpleactors.actors.ActorScheduler;
import com.lld.simpleactors.actors.ActorSystem;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class ActorImpl<T> implements ActorRef<T> {
    private volatile T object;
    private final ActorSystemImpl actorSystem;
    private final ActorScheduler scheduler;
    private final String name;
    private final BiConsumer<T, Exception> exceptionHandler;
    private final Consumer<T> destructor;
    private volatile Object mailbox;
    private volatile RegSet.Registration reg;

    ActorImpl(T object, Supplier<T> constructor, ActorScheduler scheduler, ActorSystemImpl actorSystem, String name, BiConsumer<T, Exception> exceptionHandler, Consumer<T> destructor) {
        this.actorSystem = actorSystem;
        this.exceptionHandler = exceptionHandler;
        this.name = name;
        this.destructor = destructor;
        if (object != null) {
            this.object = object;
        }
        this.scheduler = scheduler;
        scheduler.actorCreated(this);
        if (constructor != null) {
            this.object = constructor.get();
        }
        actorSystem.add(this);
    }


    @Override
    public ActorSystem system() {
        return null;
    }

    @Override
    public void tell(Consumer<T> action) {
        ActorRef<?> caller = Actor.current();
        scheduleCall(action, caller);
    }

    private void scheduleCall(Consumer<T> action, ActorRef<?> caller) {
        scheduleCallErrorAware(action, caller, e -> exceptionHandler.accept(object, e));
    }

    private void scheduleCallErrorAware(Consumer<T> action, ActorRef<?> caller, Consumer<Exception> exceptionCallback) {
        scheduler.schedule(() -> {
            Actor.setCurrent(this);
            Actor.setSender(caller);
            try {
                if (object == null)
                    return;
                action.accept(object);
            } catch (Exception e) {
                exceptionCallback.accept(e);
            } finally {
                Actor.setCurrent(null);
                Actor.setSender(null);
            }
        }, this);
    }

    @Override
    public void later(Consumer<T> action, long ms) {

    }

    @Override
    public <R> void ask(Function<T, R> action, Consumer<R> consumer) {

    }

    void reg(RegSet.Registration reg) {
        this.reg = reg;
    }

    void mailbox(Object mailbox) {
        this.mailbox = mailbox;
    }

    Object mailbox() {
        return mailbox;
    }
}
