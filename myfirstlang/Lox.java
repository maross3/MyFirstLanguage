package myfirstlang;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class Lox {
	private static final Interpreter interpreter = new Interpreter();
	static boolean hadError = false;
	static boolean hadRuntimeError = false;
	public static void main(String[] args) throws IOException {
		
			String dir = "./tests/ScopeReassignment.txt";
			runFile(dir);
			// runPrompt();				
					
//			if (args.length < 1) {
//				System.out.println("Usage: jlox [script]");
//				System.exit(64);
//			} else if (args.length == 1) {
//				runFile(args[0]);
//			} else {
//				runPrompt();
//			}
		}

	
	private static void runFile(String path) throws IOException {
			byte[] bytes = Files.readAllBytes(Paths.get(path));
			run(new String(bytes, Charset.defaultCharset()));
			if(hadError) System.exit(65);
			if(hadRuntimeError) System.exit(70);
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

		Parser parser = new Parser(tokens);
		List<Stmt> statements = parser.parse();
		
		if(hadError) return;
		
		interpreter.interpret(statements);
		//System.out.println(new AstPrinter().print(expression));
//		for(Token token : tokens) {
//			System.out.print(token);
//		}
	}
	
	static void error(int line, String message) {
		report(line, "", message);
	}
	
	// should print more detailed errors in port
	private static void report(int line, String where, String message) {
		System.err.println(" [line " + line + "] Error" + where + ": " + message);
		hadError = true;
	}
	
	static void error(Token token, String message) {
	    if (token.type == TokenType.EOF) {
	        report(token.line, " at end", message);
	      } else {
	        report(token.line, " at '" + token.lexeme + "'", message);
	      }
	}
	
	static void runtimeError(RuntimeError error) {
		System.err.println("\n" + error.getMessage() +
				" [line " + error.token.line + "]\n");
		hadRuntimeError = true;
	}

}
