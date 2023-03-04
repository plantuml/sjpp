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
			case REMOVE_CURRENT_FOLDER:
				if (context.doesApplyOn(s))
					context.removeCurrentFolder(this.packageName);
				break;
			case REMOVE_FOLDER_AND_SUBFOLDERS:
				if (context.doesApplyOn(s))
					context.removeFolderAndSubfolders(this.packageName);
				break;
			case REMOVE_FILE:
				if (context.doesApplyOn(s))
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

//	public boolean belongsToPackage(String p) {
//		return getPackageName().startsWith(p);
//	}
//
	public void process() {

		if (context.getMode() == ContextMode.REGULAR)
			removeFirstHeader();

		ProcessMode mode = ProcessMode.NORMAL;

		for (ListIterator<String> it = lines.listIterator(); it.hasNext();) {
			final String line = it.next();
			final Directive dir = Directive.directiveInLine(line);
			if (dir == Directive.IMPORT) {
				final int x = line.indexOf(';');
				final String importName = line.substring("import ".length(), x).trim();
				final boolean removeImportLine = context.removeImportLine(importName);
				if (removeImportLine)
					shadowThisLine(it, line);

			} else if (dir == Directive.UNCOMMENT && context.doesApplyOn(line)) {
				mode = ProcessMode.UNCOMMENT;
				removeLineIfRegularMode(it);
			} else if (dir == Directive.COMMENT && context.doesApplyOn(line)) {
				mode = ProcessMode.COMMENT;
				removeLineIfRegularMode(it);
			} else if (dir == Directive.DONE) {
				mode = ProcessMode.NORMAL;
				removeLineIfRegularMode(it);
			} else {
				if (mode == ProcessMode.COMMENT)
					shadowThisLine(it, line);
				else if (mode == ProcessMode.UNCOMMENT)
					it.set(line.replaceFirst("//", ""));

			}
		}

	}

	private void removeFirstHeader() {
		if (lines.get(0).startsWith("/*") == false)
			return;
		final ListIterator<String> it = lines.listIterator();
		while (it.hasNext()) {
			final String line = it.next();
			it.remove();
			if (line.endsWith("*/"))
				return;
		}

	}

	protected void removeLineIfRegularMode(ListIterator<String> it) {
		if (context.getMode() == ContextMode.REGULAR)
			it.remove();
	}

	protected void shadowThisLine(ListIterator<String> it, String line) {
		if (context.getMode() == ContextMode.DEBUG)
			it.set("// " + line);
		else
			it.remove();
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
		lines.add(0, Context.GENERATED);
		Files.createDirectories(newPath.getParent());
		Files.write(newPath, lines);
	}

	static enum ProcessMode {
		NORMAL, COMMENT, UNCOMMENT
	}

	public final String getPackageName() {
		return packageName;
	}
}
