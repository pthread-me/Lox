package src;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class Lox {
	static boolean hadError = false;

	public static void main(String[] args) throws IOException{


		if(args.length>1){
			System.out.println("Must input a path to the text file containing the Lox code");
			System.exit(64);
		}else if(args.length ==1){
			runFile(args[0]);
		}else{
			runPrompt();
		}

	}

	public static void runFile(String path) throws IOException{

		byte[] bytes = Files.readAllBytes(Paths.get(path));
		run(new String(bytes, Charset.defaultCharset()));
	}

	public static void runPrompt() throws IOException{

		hadError = false;

		InputStreamReader in = new InputStreamReader(System.in);
		BufferedReader reader = new BufferedReader(in);

		while(true){
			System.out.print("> ");
			String line = reader.readLine();
			if(line == null){
				break;
			}
			run(line);
		}
	}

	public static void run(String source){
		if(hadError){
			System.exit(65);
		}

		Scanner scanner = new Scanner(source);
		List<Token> tokens = scanner.scanTokens();

		for(Token token: tokens){
			System.out.println(token);
		}
	}

	public static void error(int line, String message){
		report(line, "", message);
	}

	public static void report(int line, String where, String message){
		System.err.println("[Line: " +line+ "] Error " +where+ ": "+message);
		hadError = true;
	}


}
