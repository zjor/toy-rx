package com.github.zjor.signals.spreadsheet.engine;

import com.github.zjor.signals.Signal;

public class RefExpr implements Expr {

    private Signal<Double> signal;

    public RefExpr(Signal<Double> signal) {
        this.signal = signal;
    }

    @Override
    public Double eval() {
        return signal.getValue();
    }
}
