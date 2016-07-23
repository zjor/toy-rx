package com.github.zjor.rx;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

public class Observable<T> {

    private static final ScheduledExecutorService SCHEDULER = Executors.newScheduledThreadPool(5);

    private LinkedList<Event<T>> unprocessed = new LinkedList<>();
    private Collection<Subscription<T>> subscriptions = new LinkedHashSet<>();
    private boolean completed = false;

    public Subscription<T> subscribe(Consumer<T> onNext, Runnable onComplete) {
        Subscription<T> subscription = new Subscription<>(this, onNext, onComplete);
        subscriptions.add(subscription);

        while (!unprocessed.isEmpty()) {
            Event<T> next = unprocessed.poll();
            next.handle(
                    value -> subscriptions.forEach(s -> s.onNext(value)),
                    () -> complete());
        }
        return subscription;
    }

    public Subscription<T> subscribe(Consumer<T> onNext) {
        return subscribe(onNext, null);
    }

    protected void unsubscribe(Subscription<T> subscription) {
        subscriptions.remove(subscription);
    }

    public void next(T value) {
        if (completed) {
            throw new IllegalStateException("Completed observable doesn't accept values");
        }

        if (subscriptions.isEmpty()) {
            unprocessed.add(Event.of(value));
        } else {
            subscriptions.forEach(s -> s.onNext(value));
        }
    }

    private void terminate() {
        if (!completed) {
            completed = true;
            unprocessed.add(Event.<T>terminal());
        }
    }

    public void complete() {
        completed = true;
        subscriptions.forEach(Subscription::onComplete);
    }

    public <S> Observable<S> map(Function<T, S> func) {
        Observable<S> mapped = new Observable<>();
        subscribe(value -> mapped.next(func.apply(value)), () -> mapped.terminate());
        return mapped;
    }

    public <S> Observable<S> flatMap(Function<T, Observable<S>> func) {
        Observable<S> mapped = new Observable<>();
        subscribe(t -> func.apply(t).subscribe(s -> mapped.next(s)), () -> mapped.terminate());
        return mapped;
    }

    public Observable<T> filter(Predicate<T> p) {
        Observable<T> filtered = new Observable<>();
        subscribe(v -> {
            if (p.test(v)) {
                filtered.next(v);
            }
        }, () -> filtered.terminate());
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
        }, () -> throttled.terminate());
        return throttled;
    }

    public Observable<T> delay(long delay) {
        Observable<T> delayed = new Observable<>();
        subscribe(value -> SCHEDULER.schedule(
                () -> {
                    delayed.next(value);
                    delayed.terminate();
                }
                , delay, TimeUnit.MILLISECONDS));
        return delayed;
    }

    public static <S> Observable<S> just(S event) {
        Observable<S> o = new Observable<>();
        o.next(event);
        o.terminate();
        return o;
    }

    public static <S> Observable<S> from(Collection<S> events) {
        Observable<S> o = new Observable<>();
        events.forEach(e -> o.next(e));
        o.terminate();
        return o;
    }

    static class Event<T> {

        enum Type {
            REGULAR, TERMINAL
        }

        private T value;

        private Type type;

        public Event(T value, Type type) {
            this.value = value;
            this.type = type;
        }

        static <T> Event<T> of(T data) {
            return new Event(data, Type.REGULAR);
        }

        static <T> Event<T> terminal() {
            return new Event<>(null, Type.TERMINAL);
        }

        void handle(Consumer<T> onNext, Runnable onComplete) {
            switch (type) {
                case REGULAR:
                    onNext.accept(value);
                    break;
                case TERMINAL:
                    onComplete.run();
                    break;
            }
        }

    }


}
