function Signal(expr) {
	this.observers = new Set([]);
	this.expr = expr;
	this.value = this.eval();
}

Signal.callers = [];

Signal.prototype.getValue = function() {
	var callers = Signal.callers;
	if (callers.length > 0) {
		var lastCaller = callers[callers.length - 1];
		this.observers.add(lastCaller);
		if (lastCaller.observers.has(this)) {
			throw "Circular reference detected";
		}
	}
	return this.value;
}

Signal.prototype.eval = function() {
	Signal.callers.push(this);

	var newValue = this.expr();
	if (newValue != this.value) {
		this.value = newValue;
		var oldObservers = new Set(this.observers);
		this.observers.clear();
		oldObservers.forEach(function(observer) {
			observer.eval();
		});
	}

	Signal.callers.pop();
	return this.value;
}

function Var(expr) {
	Signal.call(this, expr);
}

Var.prototype = Object.create(Signal.prototype);
Var.prototype.constructor = Var;

Var.prototype.update = function(expr) {
	this.expr = expr;
	this.eval();
}