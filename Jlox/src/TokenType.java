package src;

enum TokenType {

	//single char tokens
	LEFT_PAREN, RIGHT_PAREN, LEFT_BRACE, RIGHT_BRACE,
	COMMA, DOT, STAR, SEMICOLON, PLUS, MINUS, SLASH,

	//one or two char token
	BANG, BANG_EQUAL,
	EQUAL, EQUAL_EQUAL,
	GREATER, GREATER_EQUAL,
	LESS, LESS_EQUAL,

	//literals
	IDENTIFIER, STRING, NUMBER,

	//keywords
	AND, CLASS, ELSE, FALSE, IF, FOR, FUN, NIL, OR,
	PRINT, RETURN, SUPER, THIS, TRUE, VAR, WHILE,

	EOF
}
