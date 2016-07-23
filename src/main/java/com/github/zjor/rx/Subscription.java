package com.github.zjor.rx;

import java.util.Optional;
import java.util.function.Consumer;

public class Subscription<T> {

    private Observable<T> observable;

    private Consumer<T> onNext;

    private Optional<Runnable> onComplete;

    public Subscription(Observable<T> observable, Consumer<T> onNext, Runnable onComplete) {
        this.observable = observable;
        this.onNext = onNext;
        this.onComplete = Optional.ofNullable(onComplete);
    }

    public void unsubscribe() {
        observable.unsubscribe(this);
    }

    protected void onNext(T value) {
        onNext.accept(value);
    }

    protected void onComplete() {
        onComplete.ifPresent(Runnable::run);
    }



}
