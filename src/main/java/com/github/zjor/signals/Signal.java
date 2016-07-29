package com.github.zjor.signals;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Set;
import java.util.function.Supplier;

public class Signal<T> {

    private static LinkedList<Signal<?>> caller = new LinkedList<>();

    private T value;

    private Supplier<T> expression;

    protected Set<Signal<?>> observers = new LinkedHashSet<>();

    public Signal(Supplier<T> expression) {
        this.expression = expression;
        value = evaluate();
    }

    public static <S> Signal of(S value) {
        return new Signal(() -> value);
    }

    public T getValue() {
        if (!caller.isEmpty()) {
            observers.add(caller.getLast());
            if (caller.getLast().observers.contains(this)) {
                throw new IllegalStateException("Circular reference detected");
            }
        }
        return value;
    }

    protected void update(Supplier<T> newExpression) {
        expression = newExpression;
        evaluate();
    }

    private T evaluate() {
        caller.offer(this);
        T newValue = expression.get();

        if (newValue != value) {
            value = newValue;

            Collection<Signal<?>> oldObservers = new LinkedList<>(observers);
            observers.clear();
            oldObservers.forEach(caller -> caller.evaluate());

        }
        caller.removeLast();
        return value;
    }


}
