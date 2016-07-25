package com.github.zjor.signals.spreadsheet.engine;

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
    public Double eval() {
        switch (operator) {
            case '+': return left.eval() + right.eval();
            case '-': return left.eval() - right.eval();
            case '*': return left.eval() * right.eval();
            case '/': return left.eval() / right.eval();
            default:
                throw new IllegalArgumentException("Unknown operator: " + operator);
        }
    }
}
