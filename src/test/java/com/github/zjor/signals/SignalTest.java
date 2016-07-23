package com.github.zjor.signals;

import org.junit.Assert;
import org.junit.Test;

public class SignalTest {

    @Test
    public void shouldComputeTransitiveReference() {
        Signal<Integer> a = Signal.of(1);
        Signal<Integer> b = new Signal<>(() -> a.getValue() + 1);
        Signal<Integer> c = new Signal<>(() -> b.getValue() + 1);
        Assert.assertTrue(3 == c.getValue());
    }

    @Test
    public void shouldHandleMultipleDependencies() {
        Signal<Integer> a = Signal.of(1);
        Signal<Integer> b = new Signal<>(() -> a.getValue() + 1);
        Signal<Integer> c = new Signal<>(() -> b.getValue() + a.getValue());
        Assert.assertTrue(3 == c.getValue());
    }
}
