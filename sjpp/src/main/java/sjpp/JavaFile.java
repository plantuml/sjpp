package sjpp;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.ListIterator;

public class JavaFile {

	private final Context context;
	private final List<String> lines;
	private final Path path;
	private String packageName;

	public JavaFile(Context context, Path path) throws IOException {
		this.path = path;
		this.context = context;
		this.lines = Files.readAllLines(path);

		for (String s : lines) {
			if (s.startsWith("package ")) {
				final int x = s.indexOf(';');
				if (x != -1) {
					this.packageName = s.substring("package ".length(), x).trim();
				}
				continue;
			}
			final Directive dir = Directive.directiveInLine(s);
			switch (dir) {
			case IMPORT:
				break;
			case REMOVE_FOLDER:
				context.removePackage(this.packageName);
				break;
			case REMOVE_FILE:
				context.removeFile(this);
				break;
			}
		}

		if (packageName == null)
			throw new IllegalArgumentException(path.toString());

		// System.err.println(lines.size());
	}

	@Override
	public String toString() {
		return path.toString();
	}

	public boolean belongsToPackage(String p) {
		return packageName.startsWith(p);
	}

	public void process() {

		Mode mode = Mode.NORMAL;

		for (ListIterator<String> it = lines.listIterator(); it.hasNext();) {
			final String line = it.next();
			final Directive dir = Directive.directiveInLine(line);
			if (dir == Directive.IMPORT) {
				final int x = line.indexOf(';');
				final String importName = line.substring("import ".length(), x).trim();
				final boolean removeImportLine = context.removeImportLine(importName);
				if (removeImportLine) {
					// System.err.println(line);
					it.set("// " + line);
				}
			} else if (dir == Directive.UNCOMMENT) {
				mode = Mode.UNCOMMENT;
			} else if (dir == Directive.COMMENT) {
				mode = Mode.COMMENT;
			} else if (dir == Directive.DONE) {
				mode = Mode.NORMAL;
			} else {
				if (mode == Mode.COMMENT)
					it.set("// " + line);
				else if (mode == Mode.UNCOMMENT)
					it.set(line.replaceFirst("//", ""));

			}
		}

	}

	public boolean isItMe(String name) {
		String signature = path.toAbsolutePath().toFile().getAbsolutePath().replaceAll("[^-\\w]", ".");
		signature = signature.replaceAll("\\.java$", "");
		return signature.endsWith(name);
	}

	public Path getNewPath(Path out) {
		final Path root = context.getRoot();
		if (path.toString().startsWith(root.toString()) == false)
			throw new IllegalStateException();
		final String relative = path.toString().substring(root.toString().length());

		final String newPath = out.toString() + relative;
		return Paths.get(newPath);

	}

	public void save(Path newPath) throws IOException {
		Files.createDirectories(newPath.getParent());
		Files.write(newPath, lines);
	}

	static enum Mode {
		NORMAL, COMMENT, UNCOMMENT
	}
}
