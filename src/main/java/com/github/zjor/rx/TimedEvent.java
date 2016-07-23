package com.github.zjor.rx;

/**
 * Holds event which should be emitted after specified dueTime
 * @param <T>
 */
public class TimedEvent<T> {

    private T event;

    private long dueTime;

    public TimedEvent(T event, long dueTime) {
        this.event = event;
        this.dueTime = dueTime;
    }

    public static <S> TimedEvent<S> of(S event, long dueTime) {
        return new TimedEvent<>(event, dueTime);
    }

    public T getEvent() {
        return event;
    }

    public long getDueTime() {
        return dueTime;
    }
}
