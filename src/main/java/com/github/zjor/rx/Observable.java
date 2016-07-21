package com.github.zjor.rx;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

public class Observable<T> {

    private static final ScheduledExecutorService SCHEDULER = Executors.newScheduledThreadPool(5);

    private LinkedList<T> unprocessed = new LinkedList<>();
    private Collection<Subscription<T>> subscriptions = new LinkedHashSet<>();

    public Subscription<T> subscribe(Consumer<T> onNext) {
        Subscription<T> subscription = new Subscription<>(this, onNext);
        subscriptions.add(subscription);

        while (!unprocessed.isEmpty()) {
            T next = unprocessed.poll();
            subscriptions.forEach(s -> s.onNext(next));
        }
        return subscription;
    }

    protected void unsubscribe(Subscription<T> subscription) {
        subscriptions.remove(subscription);
    }

    public void onNext(T value) {
        if (subscriptions.isEmpty()) {
            unprocessed.add(value);
        } else {
            subscriptions.forEach(s -> s.onNext(value));
        }
    }

    public <S> Observable<S> map(Function<T, S> func) {
        Observable<S> mapped = new Observable<>();
        subscribe(value -> mapped.onNext(func.apply(value)));
        return mapped;
    }

    public <S> Observable<S> flatMap(Function<T, Observable<S>> func) {
        Observable<S> mapped = new Observable<>();
        subscribe(t -> func.apply(t).subscribe(s -> mapped.onNext(s)));
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

    public Observable<T> delay(long delay) {
        Observable<T> delayed = new Observable<>();
        subscribe(value -> SCHEDULER.schedule(() -> delayed.onNext(value), delay, TimeUnit.MILLISECONDS));
        return delayed;
    }

    public static <S> Observable<S> just(S event) {
        Observable<S> o = new Observable<>();
        o.onNext(event);
        return o;
    }

}
