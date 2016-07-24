package com.github.zjor;

import com.github.zjor.signals.Var;

public class App {
    public static void main(String[] args) {
//        Observable<Integer> ints = new Observable<>();
//        Observable<Integer> squares = ints.filter(i -> i > 3).map(i -> i * i);
//        squares.subscribe(i -> {
//            System.out.println(i);
//        });
//        ints.next(3);
//        ints.next(4);
//        ints.next(5);
        Var<Integer> a = new Var(() -> 5);
        Var<Integer> b = new Var(() -> {
            int val = a.getValue() + 1;
            System.out.println("new value: " + val);
            return val;
        });
        a.update(() -> 6);

    }
}
