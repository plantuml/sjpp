package sjpp;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

public class SjppAntTask extends Task {

	private String src;
	private String dest;
	private String define;

	@Override
	public void execute() throws BuildException {
		this.log("Starting SimpleJava PreProcessor.");
		this.log("src: " + src);
		this.log("dest: " + dest);
		this.log("define: " + define);

		final Path root = Paths.get(src);

		final Context context = new Context(ContextMode.REGULAR, root);
		context.addDefine(define);

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

}
