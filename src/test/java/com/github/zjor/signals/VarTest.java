package com.github.zjor.signals;

import org.junit.Assert;
import org.junit.Test;

public class VarTest {

    @Test
    public void shouldUpdateResult() {
        Var<Integer> a = new Var(() -> 1);
        Signal<Integer> b = new Signal<>(() -> a.getValue() + 1);
        Signal<Integer> c = new Signal<>(() -> b.getValue() + a.getValue());
        Assert.assertTrue(3 == c.getValue());

        a.update(() -> 3);
        Assert.assertTrue(7 == c.getValue());
    }

    @Test(expected = IllegalStateException.class)
    public void shouldDetectCircularReference() {
        Var<Integer> a = new Var(() -> 1);
        Signal<Integer> b = new Signal<>(() -> a.getValue() + 1);
        Signal<Integer> c = new Signal<>(() -> b.getValue() + a.getValue());
        Assert.assertTrue(3 == c.getValue());

        a.update(() -> b.getValue());
    }

}
