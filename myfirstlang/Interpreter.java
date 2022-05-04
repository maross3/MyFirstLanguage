package myfirstlang;

import myfirstlang.Expr.Binary;
import myfirstlang.Expr.Grouping;
import myfirstlang.Expr.Literal;
import myfirstlang.Expr.Unary;

class Interpreter implements Expr.Visitor<Object> {

	void interpret(Expr expression) {
		try {
			Object value = evaluate(expression);
			System.out.println(stringify(value));
		} catch (RuntimeError error) {
			Lox.runtimeError(error);
		}
	}
	
	@Override
	public Object visitBinaryExpr(Expr.Binary expr) {
		Object left = evaluate(expr.left);
		Object right = evaluate(expr.right);
		
		switch(expr.operator.type) {
			case BANG_EQUAL:
				return !isEqual(left, right);
			case EQUAL_EQUAL:
				return isEqual(left, right);
			case GREATER:
				checkNumberOperands(expr.operator, left, right);
				return (double) left > (double) right;
			case GREATER_EQUAL:
				checkNumberOperands(expr.operator, left, right);
				return (double) left >= (double) right;
			case LESS:
				checkNumberOperands(expr.operator, left, right);
				return (double) left < (double) right;
			case LESS_EQUAL:
				checkNumberOperands(expr.operator, left, right);
				return (double) left <= (double) right;
			case MINUS:
				checkNumberOperands(expr.operator, left, right);
				return (double) left - (double) right;
			case PLUS:
				if (left instanceof Double && right instanceof Double) {
					return (double) left + (double) right;
				}
				if (left instanceof String && right instanceof String) {
					return (String) left + (String) right;
				}
				
				throw new RuntimeError(expr.operator,
						"Operands must be two numbers or two strings");
			case SLASH:
				checkNumberOperands(expr.operator, left, right);
				return (double) left / (double) right;
			case STAR:
				checkNumberOperands(expr.operator, left, right);
				return (double) left * (double) right;
		}
		// unreachable
		return null;
	}

	@Override
	public Object visitGroupingExpr(Expr.Grouping expr) {
		return evaluate(expr.expression);
	}

	@Override
	public Object visitLiteralExpr(Expr.Literal expr) {
		return expr.value;
	}

	@Override
	public Object visitUnaryExpr(Expr.Unary expr) {
		Object right = evaluate(expr.right);
		
		switch (expr.operator.type) {
		case MINUS:
			checkNumberOperand(expr.operator, right);
			return -(double) right; 
		case BANG:
			return !isTruthy(right);
		}
		// unreachable
		return null;
	}
	
	private Object evaluate(Expr expr) {
		return expr.accept(this);
	}
	
	private Boolean isEqual(Object left, Object right) {
		if(left == null && right == null) return true;
		if(left == null) return false;
		return left.equals(right);
	}
	
	private Boolean isTruthy(Object obj) {
		if (obj == null) return false;
		if (obj instanceof Boolean) return (boolean) obj;
		return true;
	}
	
	private void checkNumberOperand(Token operator, Object operand) {
		if (operand instanceof Double) return;
		throw new RuntimeError(operator, "Operand must be a number");
	}
	
	private void checkNumberOperands(Token operator, Object first, Object second) {
		if(first instanceof Double && second instanceof Double ) return;
		throw new RuntimeError(operator, "Operands must be numbers");
	}
	
	private String stringify(Object obj) {
		if (obj == null) return "nil";
		
		if(obj instanceof Double) {
			String text = obj.toString();
			// negate java type Double's explicit .0
			if(text.endsWith(".0")) {
				text = text.substring(0, text.length() - 2);
			}
			return text;
		}
		return obj.toString();
	}

}
