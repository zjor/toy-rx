package com.github.zjor.signals.spreadsheet.engine;

public class LiteralExpr implements Expr {
    private double value;

    public LiteralExpr(double value) {
        this.value = value;
    }

    @Override
    public Double eval() {
        return value;
    }
}
