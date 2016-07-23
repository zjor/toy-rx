package com.github.zjor.signals;

import java.util.function.Supplier;

public class Var<T> extends Signal<T> {

    public Var(Supplier<T> expression) {
        super(expression);
    }

    @Override
    public void update(Supplier<T> newExpression) {
        super.update(newExpression);
    }
}
