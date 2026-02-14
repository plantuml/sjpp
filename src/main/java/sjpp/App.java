package sjpp;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;

public class App {

	public static void main(String[] args) throws IOException, InterruptedException {
		if (args.length == 1 && args[0].equals("--version")) {
			printVersion();
			return;
		}
		if (args.length != 3) {
			printUsage();
			return;
		}
		final String source = args[0];
		final String destination = args[1];

		final Path root = Paths.get(source);

		final Context context = new Context(ContextMode.REGULAR, root);
		context.addDefine(args[2]);

		final Path out = Paths.get(destination);
		context.process(out);

	}

    public static void printVersion() {
        System.err.println("SJPP - Simple Java PreProcessor");
        System.err.println();
        System.err.println("Version: " + CompilationInfo.VERSION);
        System.err.println("Commit:  " + CompilationInfo.COMMIT);
        System.err.println("Build :  " + new Date(CompilationInfo.COMPILE_TIMESTAMP));
    }

	private static void printUsage() {
		printVersion();
		System.err.println();
		System.err.println("DESCRIPTION:");
		System.err.println("  Preprocesses Java source files using conditional compilation directives.");
		System.err.println("  Reads from <source>, processes files based on <flag>, writes to <destination>.");
		System.err.println();
		System.err.println("USAGE:");
		System.err.println("  java -jar sjpp.jar <source> <destination> <flag>");
		System.err.println();
		System.err.println("ARGUMENTS:");
		System.err.println("  <source>       Input directory containing Java source files");
		System.err.println("  <destination>  Output directory for processed files");
		System.err.println("  <flag>         Preprocessing flag to define (e.g., __CORE__, __MIT__)");
		System.err.println();
		System.err.println("EXAMPLE:");
		System.err.println("  java -jar sjpp.jar src src-core __CORE__");
		System.err.println();
		System.err.println("  This defines the flag __CORE__, then processes all Java files in 'src'");
		System.err.println("  (recursively) and writes the result to 'src-core'.");
		System.err.println();
		System.err.println("DIRECTIVES (in source files):");
		System.err.println("  // ::remove file when FLAG              Remove entire file");
		System.err.println("  // ::remove current folder when FLAG    Remove all files in current folder");
		System.err.println("  // ::remove folder when FLAG            Remove folder and subfolders");
        System.err.println("  // ::comment when FLAG                  Comment out code block");
        System.err.println("  // ::done");
		System.err.println("  // ::uncomment when FLAG                Uncomment code block");
        System.err.println("  // ::done");
		System.err.println("  // ::revert when FLAG                   Toggle comment state of code block");
        System.err.println("  // ::done");
		System.err.println("More info: https://github.com/plantuml/sjpp");
	}

}
