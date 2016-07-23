package com.github.zjor;

import com.github.zjor.rx.Observable;

public class App {
    public static void main(String[] args) {
        Observable<Integer> ints = new Observable<>();
        Observable<Integer> squares = ints.filter(i -> i > 3).map(i -> i * i);
        squares.subscribe(i -> {
            System.out.println(i);
        });
        ints.next(3);
        ints.next(4);
        ints.next(5);
    }
}
