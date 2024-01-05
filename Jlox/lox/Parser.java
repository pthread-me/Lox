package lox;

import java.util.List;
import static lox.TokenType.*;

class Parser {

    private final List<Token> tokens;
    private int current;

    public Parser(List<Token> tokens){
        this.tokens = tokens;
        current = 0;
    }

    public Expr parse(){
        try {
            return expression();
        }catch (ParseError error){
            return null;
        }
    }

    private ParseError error(Token token, String err_message){
        Lox.error(token, err_message);
        return new ParseError();
    }

    private void synchronize(){

        while(peak().type != EOF){
            switch (peak().type){
                case CLASS, FUN, VAR, FOR, WHILE, IF, PRINT, RETURN, SEMICOLON -> {return;}
            }
            advance();
        }
    }

    /**
     * @return return current token
     */
    private Token peak(){
        return tokens.get(current);
    }

    /**
     * @return return the current token, and consumes it.
     */
    private Token advance(){
        Token temp = tokens.get(current);
        current++;
        return temp;
    }

    /**
     * consumes a targeted token, used to check for ) , } and ]
     * @param expected the expected next token
     * @param err_message message if token does not match
     * @return the targeted token if found, else exit
     */
    private Token consume(TokenType expected, String err_message){
        if(peak().type == expected){
            return advance();
        }
        throw error(peak(), err_message);
    }

    /**
     * checks if we are at the end of the tokens list
     * @return return true if we're at the end
     */
    private boolean isEnd(){
        return peak().type == TokenType.EOF;
    }

    /**
     * checks if the cur token is of a given type
     * @param type the type to check against
     * @return true if it matches
     */
    private boolean check(TokenType type){
        if(!isEnd()){
            return peak().type == type;
        }
        return false;
    }

    /**
     * look current token and return true if it matches any of the operators
     * @param operators a list of operators to test on
     * @return true if there's a hit
     */
    private boolean match(TokenType... operators){
        TokenType cur_type = tokens.get(current).type;

        for (TokenType operator : operators) {
            if (check(operator)){
                return true;
            }
        }
        return false;
    }


    private Expr expression(){
        return listing();
    }

    /**
     * Supports the comma operator as in a list of expressions : a,b,c
     * @return the total expression left associative so (, (, a b) c)
     */
    private Expr listing(){
        Expr expr = ternary();
        while (match(COMMA)){
            Token operator = advance();
            Expr right = equation();

            expr = new Expr.Binary(expr, operator, right);
        }
        return expr;
    }

    private Expr ternary(){
        Expr expr = equation();

        if(match(QUESTION_MARK)){
            Token first = advance();
            Expr left = expression();
            Token second = consume(COLON, "a ternary operation has the format a?b:c\t the " +
                    "inputted string is missing a :");
            Expr right = expression();

            expr = new Expr.Ternary(expr, first, left, second, right );
        }
        return expr;
    }

    private Expr equation(){
        //left recursion
        Expr expr = comparison();

        while(match(BANG_EQUAL, EQUAL_EQUAL)){
            Token operator = advance();
            Expr right = comparison();

            expr = new Expr.Binary(expr, operator, right);
        }
        return expr;
    }

    private Expr comparison(){
        Expr expr = term();

        while(match(LESS, GREATER, LESS_EQUAL, GREATER_EQUAL)){
            Token operator = advance();
            Expr right = term();

            expr = new Expr.Binary(expr, operator, right);
        }
        return expr;
    }

    private Expr term(){
        Expr expr = factor();

        while(match(PLUS, MINUS)){
            Token operator = advance();
            Expr right = factor();

            expr = new Expr.Binary(expr, operator, right);
        }
        return expr;
    }

    private Expr factor(){
        Expr expr = unary();

        while(match(SLASH, STAR)){
            Token operator = advance();
            Expr right = unary();

            expr = new Expr.Binary(expr, operator, right);
        }
        return expr;
    }

    private Expr unary(){
        if(match(BANG, MINUS)){
            Token operator = advance();
            Expr right = unary();

            return new Expr.Unary(operator, right);
        }
        return primary();
   }

   private Expr primary(){
        Token cur = peak();

        switch (cur.type){
            case FALSE -> {advance(); return new Expr.Literal(false);}
            case TRUE -> {advance(); return new Expr.Literal(true);}
            case NIL -> {advance(); return new Expr.Literal(null);}
            case NUMBER, STRING -> {advance(); return new Expr.Literal(cur.literal);}
            case LEFT_PAREN -> {
                advance();
                Expr expr = expression();
                Token right = consume(RIGHT_PAREN, "Missing closing parentheses");
                return new Expr.Grouping(expr);
            }
            case EQUAL_EQUAL, BANG_EQUAL, GREATER, GREATER_EQUAL, LESS, LESS_EQUAL -> {
                advance();
                Expr discard = equation();
                throw error(cur, "Binary operator does not have a left-hand operand");
            }
        }
        throw error(cur, "Invalid Symbol");
   }

   private static class ParseError extends RuntimeException{}
}

