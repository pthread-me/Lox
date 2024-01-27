package lox;


public class Interpreter implements Expr.Visitor<Object>{

   private Object evaluate(Expr expr){
       return expr.accept(this);
   }

   private boolean isTruthy(Object obj){
      if(obj == null || obj instanceof String && ((String) obj).isEmpty()){
          return false;
      }else if (obj instanceof Boolean){
          return (boolean)obj;
      }else {
          return true;
      }
   }
    @Override
    public Object visitLiteralExpr(Expr.Literal expr) {
        return expr.value;
    }

    @Override
    public Object visitGroupingExpr(Expr.Grouping expr) {
       return evaluate(expr.expression);
    }

    @Override
    public Object visitUnaryExpr(Expr.Unary expr) {
       Object right = evaluate(expr.right);

       switch (expr.operator.type){
           case MINUS -> {return - (Double)right;}
           case BANG -> {return ! isTruthy(right)}
       }
    }

    @Override
    public Object visitBinaryExpr(Expr.Binary expr) {

       //having left first can affect side-effects, think of the if(l==NULL && l->a==NULL) check in C
       Object left = evaluate(expr.left);
       Object right = evaluate(expr.right);

       switch (expr.operator.type){

       }
    }
}
