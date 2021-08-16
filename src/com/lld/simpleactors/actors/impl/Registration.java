package com.lld.simpleactors.actors.impl;

import java.util.Collection;

interface RegSet<T> {
    interface Registration {
        void remove();
    }

    Registration add(T element);

    Collection<T> copy();
}
