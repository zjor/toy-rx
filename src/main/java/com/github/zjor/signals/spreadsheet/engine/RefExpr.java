package com.github.zjor.signals.spreadsheet.engine;

import com.github.zjor.signals.Signal;

import java.util.Optional;

public class RefExpr implements Expr {

    private Signal<Optional<Double>> signal;

    public RefExpr(Signal<Optional<Double>> signal) {
        this.signal = signal;
    }

    @Override
    public Optional<Double> eval() {
        return signal.getValue();
    }
}
