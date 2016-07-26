package com.github.zjor.signals.spreadsheet;

import com.github.zjor.rx.Observable;
import com.github.zjor.signals.Var;

import java.util.function.Function;

public class Cell {

    private Observable<String> exprStream = new Observable<>();
    private Var<Double> signal = new Var<>(() -> 0.0);
    private Observable<String> valueStream = new Observable<>();

    private Function<String, Double> calculator;

    public Cell(Function<String, Double> calculator) {
        this.calculator = calculator;
    }

    {
        exprStream.subscribe(expr -> signal.update(() -> {
            double value = calculator.apply(expr);
            valueStream.next(String.valueOf(value));
            return value;
        }));

    }

    public void update(String expr) {
        exprStream.next(expr);
    }

    public Var<Double> getSignal() {
        return signal;
    }

    public Observable<String> getValueStream() {
        return valueStream;
    }

    public Observable<String> getExprStream() {
        return exprStream;
    }
}
