package lox;

import java.util.List;

abstract class Expr {

	interface Visitor<R> {
		R visitBinaryExpr(Binary expr);
		R BinaryRPN(Binary expr);

		R visitGroupingExpr(Grouping expr);
		R GroupingRPN(Grouping expr);

		R visitUnaryExpr(Unary expr);
		R UnaryRPN(Unary expr);

		R visitLiteralExpr(Literal expr);
		R LiteralRPN(Literal expr);

	}
	static class Binary   extends Expr {
		Binary  ( Expr left, Token operator, Expr right){
		this.left = left;
		this.operator = operator;
		this.right = right;
		}
		@Override
		<R> R accept(Visitor<R> visitor){
		return visitor.visitBinaryExpr(this);
}
		@Override
		<R> R RPN(Visitor<R> visitor){
		return visitor.BinaryRPN(this);
}
		Expr left;
		Token operator;
		Expr right;
	}
	static class Grouping extends Expr {
		Grouping( Expr expression){
		this.expression = expression;
		}
		@Override
		<R> R accept(Visitor<R> visitor){
		return visitor.visitGroupingExpr(this);
}
		@Override
		<R> R RPN(Visitor<R> visitor){
		return visitor.GroupingRPN(this);
}
		Expr expression;
	}
	static class Unary    extends Expr {
		Unary   ( Token operator, Expr right){
		this.operator = operator;
		this.right = right;
		}
		@Override
		<R> R accept(Visitor<R> visitor){
		return visitor.visitUnaryExpr(this);
}
		@Override
		<R> R RPN(Visitor<R> visitor){
		return visitor.UnaryRPN(this);
}
		Token operator;
		Expr right;
	}
	static class Literal  extends Expr {
		Literal ( Object value){
		this.value = value;
		}
		@Override
		<R> R accept(Visitor<R> visitor){
		return visitor.visitLiteralExpr(this);
}
		@Override
		<R> R RPN(Visitor<R> visitor){
		return visitor.LiteralRPN(this);
}
		Object value;
	}

	abstract <R> R accept(Visitor<R> visitor);
	abstract <R> R RPN(Visitor<R> visitor);

}
