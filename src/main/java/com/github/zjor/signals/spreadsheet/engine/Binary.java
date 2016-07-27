package com.github.zjor.signals.spreadsheet.engine;

import java.util.Optional;

public class Binary implements Expr {

    private Expr left;
    private Expr right;
    private char operator;

    public Binary(Expr left, Expr right, char operator) {
        this.left = left;
        this.right = right;
        this.operator = operator;
    }

    @Override
    public Optional<Double> eval() {
        return left.eval().flatMap(l -> right.eval().map(r -> {
            switch (operator) {
                case '+': return l + r;
                case '-': return l - r;
                case '*': return l * r;
                case '/': return l / r;
                default:
                    throw new IllegalArgumentException("Unknown operator: " + operator);
            }
        }));
    }
}
