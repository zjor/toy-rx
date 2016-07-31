function Literal(value) {
	this.value = value;
}

Literal.prototype.getValue = function() {
	return this.value;
}

function Reference(signalName, refs) {
	if (!(signalName in refs)) {
		throw "Undefined variable: " + signalName;
	}
	this.signal = refs[signalName];
}

Reference.prototype.getValue = function() {
	return this.signal.getValue();
}

function Binary(left, right, operand) {
	this.left = left;
	this.right = right;
	this.operand = operand;
}

Binary.prototype.getValue = function() {
	var l = this.left.getValue();
	var r = this.right.getValue();

	switch (this.operand) {
		case '+': return l + r;
		case '-': return l - r;
		case '*': return l * r;
		case '/': return l / r;
		default: throw "Unsupported operand: " + this.operand;
	}
}

function parse(expr, refs) {
	var doubleRe = /^[-+]?[0-9]*\.?[0-9]*$/;
	var refRe = /^[A-Za-z][0-9]$/;
	var exprRe = /^([A-Za-z][0-9])([+-\\*])([A-Za-z][0-9])$/;

	if (doubleRe.test(expr)) {
		return new Literal(parseFloat(expr));
	} else if (refRe.test(expr)) {
		return new Reference(expr.toLowerCase(), refs);
	} else if (exprRe.test(expr)) {
		var m = exprRe.exec(expr);
		return new Binary(parse(m[1], refs), parse(m[3], refs), m[2]);
	} else {
		throw "Unable to parse expression: " + expr;
	}
}