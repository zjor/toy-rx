package com.github.zjor.rx;

public class ValueHolder<T>{

    private T value;

    public ValueHolder(T value) {
        this.value = value;
    }

    public void setValue(T value) {
        this.value = value;
    }

    public T getValue() {
        return value;
    }
}
