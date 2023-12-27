package src;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static src.TokenType.*;



public class Scanner {

	private final String source;
	private final List<Token> tokens = new ArrayList<>();

	private int start = 0;
	private int current = 0;
	private int line = 1;

	static {
		Map<String, TokenType> keywords = new HashMap<>();

		keywords.put("and", AND);
		keywords.put("class", CLASS);
		keywords.put("else", ELSE);
		keywords.put("false", FALSE);
		keywords.put("for", FOR);
		keywords.put("fun", FUN);
		keywords.put("if", IF);
		keywords.put("nil", NIL);
		keywords.put("or", OR);
		keywords.put("print", PRINT);
		keywords.put("return", RETURN);
		keywords.put("super", SUPER);
		keywords.put("this", THIS);
		keywords.put("true", TRUE);
		keywords.put("var", VAR);
		keywords.put("while", WHILE);
	}

	public Scanner(String source){
		this.source = source;
	}

	/**
	 * scans characters calling the scanToken func on each until we reach the last in which we add a final EOF token
	 * @return a List of all the tokens read + an EOF token (think of this as a \0 in Strings
	 */
	List<Token> scanTokens(){
		while(!isAtEnd()){
			start = current;
			scanToken();
		}

		tokens.add(new Token(EOF, "", null, line));
		return tokens;
	}

	private void scanToken(){
		char c = advance();

		//switching on single character lexemes
		switch (c){
			case '{' -> addToken(LEFT_BRACE);
			case '}' -> addToken(RIGHT_BRACE);
			case '(' -> addToken(LEFT_PAREN);
			case ')' -> addToken(RIGHT_PAREN);

			case ',' -> addToken(COMMA);
			case '.' -> addToken(DOT);
			case '-' -> addToken(MINUS);
			case '+' -> addToken(PLUS);
			case '*' -> addToken(STAR);
			case ';' -> addToken(SEMICOLON);

			case '!' -> addToken(match('=') ? BANG_EQUAL: BANG);
			case '<' -> addToken(match('=') ? LESS_EQUAL : LESS);
			case '>' -> addToken(match('=') ? GREATER_EQUAL : GREATER);
			case '=' -> addToken(match('=') ? EQUAL_EQUAL : EQUAL);

			case '/' -> {
				if(match('/')){
					while(peak() != '\n' && !isAtEnd()) advance();
				}else{
					addToken(SLASH);
				}
			}

			case '\n' -> line++;
			case '\t', '\r', ' ' -> {} //empty statement, equivalent to just a break;

			case '"' -> string();

			default -> {
				if(isDigit(c)){
					number();
				}else if(isAlpha(c)){
					identifier();
				}else {
					Lox.error(line, "Unexpected character.");
				}
			}
		}
	}

	private void identifier(){
		while(isAlphanumeric(peak())){
			advance();
		}

		String text = source.substring(start, current);
		TokenType type = keywords.get(text);
		if(type==null){
			type = IDENTIFIER;
		}
		addToken(type);
	}

	private boolean isAlpha(char c){
		return (c>='a' && c<='z') || (c>='A' && c<='Z') || c=='_';
	}

	private boolean isAlphanumeric(char c){
		return isAlpha(c) || isDigit(c);
	}
	private boolean isDigit(char c){
		return (c >= '0' && c <= '9');
	}

	/**
	 * Scans numbers and stores their literal in a java double
	 */
	private void number(){
		//scan until we find a. or not a number
		while(isDigit(peak())){
			advance();
		}

		if(peak() == '.' && isDigit(peakNext())){
			advance();
			while(isDigit(peak())){
				advance();
			}
		}
		addToken(NUMBER, Double.parseDouble(source.substring(start, current)));
	}
	/**
	 * Scans a String
	 */
	private void string(){
		//only stop if we reach the second ", or if we reach the end of the source file
		while(peak() != '"' && !isAtEnd()){
			if(peak() == '\n'){
				line++;
			}
			advance();
		}

		//if we reach the end of the source file before " then there is an error
		if(isAtEnd()){
			Lox.error(line, "Unterminated String");
		}

		//extracting the string without the ""
		String value = source.substring(start+1, current-1);
		addToken(STRING, value);
	}

	/**
	 * @return current char, Then increments the current pointer
	 */
	private char advance(){
		return source.charAt(current ++);
	}

	/**
	 * acts as a conditional advance
	 * @param expected the character we expect
	 * @return increment current pointer if expect matches the character that current points to and returns true,
	 * 			false otherwise
	 */
	private boolean match(char expected){
		if(isAtEnd() || source.charAt(current)!=expected){
			return false;
		}
		//increment current
		current++;
		return true;
	}

	/**
	 * @return the character pointed at by current without incrementing the pointer
	 */
	private char peak(){
		if(isAtEnd()){
			return '\0';
		}
		return source.charAt(current);
	}

	private char peakNext(){
		if(current+1 >= source.length()){
			return '\0';
		}
		return source.charAt(current+1);
	}

	/**
	 * adds the current token to the token list
	 */
	private void addToken(TokenType type, Object literal){
		String text = source.substring(start, current);
		tokens.add(new Token(type, text, literal, line));
	}
	private void addToken(TokenType type){
		addToken(type, null);
	}


	private boolean isAtEnd(){
		return current >= source.length();
	}
}
