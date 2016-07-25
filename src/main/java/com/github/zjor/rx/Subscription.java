package com.github.zjor.rx;

import java.util.function.Consumer;

public class Subscription<T> {

    private Observable<T> observable;

    private Consumer<T> onNext;

    public Subscription(Observable<T> observable, Consumer<T> onNext) {
        this.observable = observable;
        this.onNext = onNext;
    }

    public void unsubscribe() {
        observable.unsubscribe(this);
    }

    protected void onNext(T value) {
        onNext.accept(value);
    }


}
