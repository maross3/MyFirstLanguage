package myfirstlang;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class Lox {

	static boolean hadError = false;
	
	public static void main(String[] args) throws IOException {
			// runPrompt();
			if (args.length < 1) {
				System.out.println("Usage: jlox [script]");
				System.exit(64);
			} else if (args.length == 1) {
				runFile(args[0]);
			} else {
				runPrompt();
			}
		}

	
	private static void runFile(String path) throws IOException {
			byte[] bytes = Files.readAllBytes(Paths.get(path));
			run(new String(bytes, Charset.defaultCharset()));
			if(hadError) System.exit(65);
		}
	
	
	private static void runPrompt() throws IOException {
		InputStreamReader input = new InputStreamReader(System.in);
		BufferedReader reader = new BufferedReader(input);
		
		for(;;) {
			System.out.print("\n> ");
			String line = reader.readLine();
			 // control-d means end of file, so line == null
			if(line == null) break;
			run(line);
			// prevents exit on error on repl
			hadError = false;
		}
	}
	
	private static void run(String source) {
		Scanner scanner = new Scanner(source);
		List<Token> tokens = scanner.scanTokens();
		
		for(Token token : tokens) {
			System.out.print(token);
		}
	}
	
	static void error(int line, String message) {
		report(line, "", message);
	}
	
	// should print more detailed errors in port
	private static void report(int line, String where, String message) {
		System.err.println(" [line " + line + "] Error" + where + ": " + message);
		hadError = true;
	}

}
