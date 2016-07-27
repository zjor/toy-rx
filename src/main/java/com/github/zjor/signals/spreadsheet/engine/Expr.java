package com.github.zjor.signals.spreadsheet.engine;

import java.util.Optional;

public interface Expr {

    Optional<Double> eval();

}
