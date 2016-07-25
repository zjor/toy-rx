package com.github.zjor;

import com.github.zjor.rx.Observable;
import com.github.zjor.rx.TimedEvent;

import java.util.Arrays;

public class App {
    public static void main(String[] args) {

        TimedEvent<String>[] events = new TimedEvent[]{
                TimedEvent.of("one", 1000),
                TimedEvent.of("two", 500),
                TimedEvent.of("three", 200)
        };

        Observable.from(Arrays.asList(events))
                .flatMap(e ->
                        Observable.just(e.getEvent())
                                .delay(e.getDueTime()))
                .subscribe(s -> System.out.println(s));


//        Var<Integer> a = new Var(() -> 5);
//        Var<Integer> b = new Var(() -> {
//            int val = a.getValue() + 1;
//            System.out.println("new value: " + val);
//            return val;
//        });
//        a.update(() -> 6);

    }
}
