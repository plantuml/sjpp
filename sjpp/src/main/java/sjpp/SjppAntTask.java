package sjpp;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class SjppAntTask extends Task {

	private String src;
	private String dest;
	private String define;
	private String header;

	@Override
	public void execute() throws BuildException {
		this.log("Starting SimpleJava PreProcessor.");
		this.log("src: " + src);
		this.log("dest: " + dest);
		this.log("define: " + define);
		this.log("header: " + header);

		final Path root = Paths.get(src);

		final Context context = new Context(ContextMode.REGULAR, root);
		context.addDefine(define);


		if (header != null) {
			try {
				context.addHeader(Files.readAllLines(Paths.get(header)));
			} catch (IOException e) {
				e.printStackTrace();
				this.log("Error " + e.toString());
			}
		}


		final Path out = Paths.get(dest);
		try {
			context.process(out);
		} catch (Exception e) {
			e.printStackTrace();
			this.log("Error " + e.toString());
		}

	}

	public final void setSrc(String src) {
		this.src = src;
	}

	public final void setDest(String dest) {
		this.dest = dest;
	}

	public final void setDefine(String define) {
		this.define = define;
	}

	public final void setHeader(String header) {
		this.header = header;
	}

}
