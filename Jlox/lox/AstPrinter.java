package lox;

public class AstPrinter implements Expr.Visitor<String> {

    /**
     * Fun being called by the AST tree
     * @param expr The expression to print / convert tp RNP
     * @return a String representation of the Expression
     */
    String print(Expr expr){
        return expr.accept(this);
    }
    String RPN(Expr expr){
        return expr.RPN(this);
    }

    /**
     * fun set deals with printing gets called by the expression's accept() fun
     * @param expr the expression calling the fun
     * @return a string to print
     */
    @Override
    public String visitBinaryExpr(Expr.Binary expr){
        return parenthesize(expr.operator.lexeme, expr.left, expr.right);
    }
    @Override
    public String visitTernaryExpr(Expr.Ternary expr){
        return "("+expr.condition.accept(this) +") "+ expr.first.lexeme +" ("+ expr.left.accept(this)+ ") "+
                expr.second.lexeme +" ("+ expr.right.accept(this)+")";
    }
    @Override
    public String visitGroupingExpr(Expr.Grouping expr){
        return parenthesize("group", expr.expression);
    }
    @Override
    public String visitUnaryExpr(Expr.Unary expr){
        return parenthesize(expr.operator.lexeme, expr.right);
    }
    @Override
    public String visitLiteralExpr(Expr.Literal expr){
        return expr==null? "nil": expr.value.toString();
    }
    private String parenthesize(String operator, Expr... exprs){
        StringBuilder builder = new StringBuilder();

        builder.append("( ");
        builder.append(operator);

        for(Expr expr : exprs){
            builder.append(" ");
            //this allows for recursion, so we append the node's children as well
            builder.append(expr.accept(this));
        }

        builder.append(" )");

        return builder.toString();
    }


    /**
     * set of fun convert expression into RPN called by the RNP() fun in the Expr class
     * @param expr the calling expression
     * @return a string to print
     */
    public String BinaryRPN(Expr.Binary expr){
        return RPN_reformat(expr.operator.lexeme, expr.left, expr.right);
    }
    public String UnaryRPN(Expr.Unary expr){
        return RPN_reformat(expr.operator.lexeme, expr.right);
    }
    public String LiteralRPN(Expr.Literal expr){
        return expr.value.toString();
    }
    public String TernaryRPN(Expr.Ternary expr){
        return "("+expr.condition.RPN(this) +") "+ expr.first.lexeme +" ("+ expr.left.RPN(this)+ ") "+
                expr.second.lexeme +" ("+ expr.right.RPN(this)+")";
    }
    public String GroupingRPN(Expr.Grouping expr){
        return RPN_reformat("", expr.expression);
    }
    private String RPN_reformat(String operator, Expr... exprs){

        StringBuilder builder = new StringBuilder();

        for(Expr expr : exprs){
            builder.append(expr.RPN(this)).append(" ");
        }

        builder.append(operator);
        return builder.toString();
    }


    //tester
    public static void main(String[] args){

        Expr expr = new Expr.Binary(
                new Expr.Grouping(
                        new Expr.Binary(new Expr.Literal(1), new Token(TokenType.PLUS, "+", null,1), new Expr.Literal(2))
                ),
                new Token(TokenType.STAR, "*", null,1),
                new Expr.Grouping(
                        new Expr.Binary(new Expr.Literal(4), new Token(TokenType.MINUS, "-", null, 1), new Expr.Literal(3))
                ));

        System.out.println(new AstPrinter().RPN(expr));
    }
}
