package sjpp;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class App {

	public static void main(String[] args) throws IOException, InterruptedException {
		if (args.length != 3) {
			System.err.println("Usage: java -jar sjpp.jar <source_tree> <destination_tree> <flag define>");
			System.err.println();
			System.err.println("Example:");
			System.err.println("Usage: java -jar sjpp.jar src src-core DEMO");
			System.err.println("In this example, the flag DEMO is defined.");
			System.err.println(
					"Then all Java files in 'src' folder (and subfolders) will be pre-processed and the result is saved in 'src-core' folder.");
			System.err.println();
			System.err.println("More info at https://github.com/plantuml/sjpp");
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

}
