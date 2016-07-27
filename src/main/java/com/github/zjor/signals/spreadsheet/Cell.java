package com.github.zjor.signals.spreadsheet;

import com.github.zjor.rx.Observable;
import com.github.zjor.signals.Var;

import java.util.Optional;
import java.util.function.Function;

public class Cell {

    private Observable<String> exprStream = new Observable<>();
    private Var<Optional<Double>> signal = new Var<>(() -> Optional.empty());
    private Observable<Optional<String>> valueStream = new Observable<>();

    private Function<String, Optional<Double>> calculator;

    public Cell(Function<String, Optional<Double>> calculator) {
        this.calculator = calculator;
    }

    {
        exprStream.subscribe(expr -> signal.update(() -> {
            Optional<Double> value = calculator.apply(expr);
            valueStream.next(value.map(String::valueOf));
            return value;
        }));

    }

    public void update(String expr) {
        exprStream.next(expr);
    }

    public Var<Optional<Double>> getSignal() {
        return signal;
    }

    public Observable<Optional<String>> getValueStream() {
        return valueStream;
    }

    public Observable<String> getExprStream() {
        return exprStream;
    }
}
