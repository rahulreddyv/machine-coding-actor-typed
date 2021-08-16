package com.lld.simpleactors.actors.impl;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MapRegSet<T> implements RegSet<T> {

    private final Map<T, T> map = new ConcurrentHashMap<>();

    @Override
    public Registration add(T element) {
        map.put(element, element);
        return () -> map.remove(element);
    }

    @Override
    public Collection<T> copy() {
        return map.keySet();
    }

}
