package com.github.zjor.rx;

import org.junit.Assert;
import org.junit.Test;

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

}
