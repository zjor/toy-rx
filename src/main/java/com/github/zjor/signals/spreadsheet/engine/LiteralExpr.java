package com.github.zjor.signals.spreadsheet.engine;

import java.util.Optional;

public class LiteralExpr implements Expr {

    private Double value;

    public LiteralExpr(Double value) {
        this.value = value;
    }

    @Override
    public Optional<Double> eval() {
        return Optional.ofNullable(value);
    }
}
