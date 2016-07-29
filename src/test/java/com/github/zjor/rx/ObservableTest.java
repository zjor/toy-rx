package com.github.zjor.rx;

import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class ObservableTest {

    @Test
    public void justShouldEmitEventAfterSubscription() {
        List<String> events = new LinkedList<>();
        Observable.just("hello").subscribe(event -> {
            events.add(event);
        });
        Assert.assertEquals(1, events.size());
    }

    @Test
    public void mapBeforeSubscriptionShouldPropagateEvent() {
        List<Integer> results = new LinkedList<>();
        Observable.just("hello").map(s -> s.length()).subscribe(e -> {
            results.add(e);
        });
        Assert.assertEquals(1, results.size());
    }

    @Test
    public void shouldExecuteAfterDelay() throws InterruptedException, ExecutionException, TimeoutException {
        CompletableFuture<String> future = new CompletableFuture<>();
        Observable.just("hello").delay(500).subscribe(s -> future.complete(s));
        Assert.assertNull(future.getNow(null));
        Assert.assertNotNull(future.get(600, TimeUnit.MILLISECONDS));
    }

    @Test
    public void shouldThrottle() throws Exception {
        TimedEvent<String>[] events = new TimedEvent[] {
                TimedEvent.of("one", 10),
                TimedEvent.of("two", 50),
                TimedEvent.of("three", 200)
        };
        List<String> results = new LinkedList<>();

        Observable
                .from(Arrays.asList(events))
                .flatMap(e -> Observable.just(e.getEvent()).delay(e.getDueTime()))
                .throttle(100)
                .subscribe(e -> results.add(e));
        Thread.sleep(300);
        Assert.assertArrayEquals(new String[]{"one", "three"}, results.toArray(new String[2]));
    }

    @Test
    public void shouldMerge() {
        Observable<Integer> o1 = new Observable<>();
        Observable<Integer> o2 = new Observable<>();
        Observable<Integer> merged = Observable.merge(o1, o2);
        o1.next(1); o2.next(2); o1.next(3);
        List<Integer> results = new LinkedList<>();
        merged.subscribe(results::add);

        Assert.assertArrayEquals(new Integer[]{1, 2, 3}, results.toArray(new Integer[0]));
    }

    @Test
    public void shouldZip() {
        Observable<String> o1 = new Observable<>();
        Observable<Integer> o2 = new Observable<>();
        Observable<Pair<String, Integer>> zipped = Observable.zip(o1, o2);
        o1.next("a");
        o2.next(1);
        o2.next(2);
        o1.next("b");
        o1.next("c");
        o2.next(3);

        List<Pair<String, Integer>> results = new LinkedList<>();
        zipped.subscribe(results::add);

        Assert.assertEquals(Pair.of("a", 1), results.get(0));
        Assert.assertEquals(Pair.of("b", 2), results.get(1));
        Assert.assertEquals(Pair.of("c", 3), results.get(2));
    }

    @Test
    public void shouldDebounce() throws Exception {
        TimedEvent<String>[] events = new TimedEvent[] {
                TimedEvent.of("one", 10),
                TimedEvent.of("two", 20),
                TimedEvent.of("three", 100)
        };
        List<String> results = new LinkedList<>();

        Observable
                .from(Arrays.asList(events))
                .flatMap(e -> Observable.just(e.getEvent()).delay(e.getDueTime()))
                .debounce(50)
                .subscribe(e -> results.add(e));
        Thread.sleep(250);

        Assert.assertArrayEquals(new String[]{"two", "three"}, results.toArray(new String[2]));
    }


}
