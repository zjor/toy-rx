package com.github.zjor.rx;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

public class Observable<T> {

    private Collection<Subscription<T>> subscriptions = new LinkedHashSet<>();

    public Subscription<T> subscribe(Consumer<T> onNext) {
        Subscription<T> s = new Subscription<>(this, onNext);
        subscriptions.add(s);
        return s;
    }

    protected void unsubscribe(Subscription<T> subscription) {
        subscriptions.remove(subscription);
    }

    public void onNext(T value) {
        subscriptions.forEach(s -> s.onNext(value));
    }

    public <S> Observable<S> map(Function<T, S> func) {
        Observable<S> mapped = new Observable<>();
        subscribe(value -> mapped.onNext(func.apply(value)));
        return mapped;
    }

    public Observable<T> filter(Predicate<T> p) {
        Observable<T> filtered = new Observable<>();
        subscribe(v -> {
            if (p.test(v)) {
                filtered.onNext(v);
            }
        });
        return filtered;
    }
}
