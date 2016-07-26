package com.github.zjor.rx;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

public class Observable<T> {

    private static final ScheduledExecutorService SCHEDULER = Executors.newScheduledThreadPool(5);

    private LinkedList<T> unprocessed = new LinkedList<>();
    private Collection<Subscription<T>> subscriptions = new LinkedHashSet<>();
    private boolean completed = false;

    public Subscription<T> subscribe(Consumer<T> onNext) {
        Subscription<T> subscription = new Subscription<>(this, onNext);
        subscriptions.add(subscription);

        while (!unprocessed.isEmpty()) {
            T value = unprocessed.poll();
            subscriptions.forEach(s -> s.onNext(value));
        }
        return subscription;
    }

    protected void unsubscribe(Subscription<T> subscription) {
        subscriptions.remove(subscription);
    }

    public void next(T value) {
        if (completed) {
            throw new IllegalStateException("Completed observable doesn't accept values");
        }

        if (subscriptions.isEmpty()) {
            unprocessed.add(value);
        } else {
            subscriptions.forEach(s -> s.onNext(value));
        }
    }

    public <S> Observable<S> map(Function<T, S> func) {
        Observable<S> mapped = new Observable<>();
        subscribe(value -> mapped.next(func.apply(value)));
        return mapped;
    }

    public <S> Observable<S> flatMap(Function<T, Observable<S>> func) {
        Observable<S> mapped = new Observable<>();
        subscribe(t -> func.apply(t).subscribe(s -> mapped.next(s)));
        return mapped;
    }

    public Observable<T> filter(Predicate<T> p) {
        Observable<T> filtered = new Observable<>();
        subscribe(v -> {
            if (p.test(v)) {
                filtered.next(v);
            }
        });
        return filtered;
    }

    public Observable<T> throttle(long dueTime) {
        Observable<T> throttled = new Observable<>();
        AtomicBoolean shouldIgnore = new AtomicBoolean(false);
        subscribe(value -> {
            if (!shouldIgnore.get()) {
                throttled.next(value);
                SCHEDULER.submit(() -> {
                    shouldIgnore.set(true);
                    Thread.sleep(dueTime);
                    shouldIgnore.set(false);
                    return null;
                });
            }
        });
        return throttled;
    }

    public Observable<T> delay(long delay) {
        Observable<T> delayed = new Observable<>();
        subscribe(value -> SCHEDULER.schedule(() -> delayed.next(value), delay, TimeUnit.MILLISECONDS));
        return delayed;
    }

    public <S> Observable<S> fold(S zero, BiFunction<S, T, S> combinator) {
        Observable<S> folded = new Observable<>();
        ValueHolder<S> accumulator = new ValueHolder<>(zero);

        subscribe(value -> {
            accumulator.setValue(combinator.apply(accumulator.getValue(), value));
            folded.next(accumulator.getValue());
        });

        return folded;
    }

    public static <S> Observable<S> just(S event) {
        Observable<S> o = new Observable<>();
        o.next(event);
        return o;
    }

    public static <S> Observable<S> from(Collection<S> events) {
        Observable<S> o = new Observable<>();
        events.forEach(e -> o.next(e));
        return o;
    }

    public static <S> Observable<S> of(Consumer<Observable<S>> consumer) {
        Observable<S> o = new Observable<>();
        consumer.accept(o);
        return o;
    }

}
