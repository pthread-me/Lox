
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class Lox {
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

		InputStreamReader in = new InputStreamReader(System.in);
		BufferedReader reader = new BufferedReader(in);

		while(true){
			System.out.println("> ");
			String line = reader.readLine();
			if(line == null){
				break;
			}
			run(line);
		}
	}

	public static void run(String source){
		Scanner scanner = new Scanner(source);
		List<Token> tokens = scanner.scanTokens();
	}
}
