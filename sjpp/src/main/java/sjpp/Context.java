package sjpp;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

public class Context {

	private Define define;
	private final List<JavaFile> files = new ArrayList<JavaFile>();

	private final List<String> removedPackage = new ArrayList<String>();
	private final List<JavaFile> removedFiles = new ArrayList<JavaFile>();
	private final Path root;

	public Context(Path root) throws IOException {
		this.root = root;
		Files.walk(root).filter(Files::isRegularFile).filter(path -> path.toString().endsWith(".java"))
				.forEach(this::addPath);
	}

	public void addDefine(String id) {
		this.define = new Define(id);
	}

	public boolean doesApplyOn(String s) {
		return define.doesApplyOn(s);
	}

	private void addPath(Path path) {
		try {
			files.add(new JavaFile(this, path));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void process() {
		for (ListIterator<JavaFile> it = files.listIterator(); it.hasNext();) {
			final JavaFile file = it.next();
			if (isRemoved(file)) {
				if (removedFiles.contains(file) == false)
					removedFiles.add(file);
				it.remove();
			}
		}

		for (JavaFile file : files) {
			file.process();
		}
	}

	public void removePackage(String packageName) {
		this.removedPackage.add(packageName);
	}

	public void removeFile(JavaFile javaFile) {
		this.removedFiles.add(javaFile);
	}

	private boolean isRemoved(JavaFile javaFile) {
		for (String p : removedPackage)
			if (javaFile.belongsToPackage(p))
				return true;

		return removedFiles.contains(javaFile);
	}

	public boolean removeImportLine(String importName) {
		for (JavaFile file : removedFiles)
			if (file.isItMe(importName))
				return true;

		return false;
	}

	public static void deleteJavaFiles(Path out) throws IOException {
		Files.walk(out).filter(Files::isRegularFile).filter(path -> path.toString().endsWith(".java")).forEach(path -> {
			try {
				Files.delete(path);
			} catch (IOException e) {
				e.printStackTrace();
			}
		});

	}

	public void save(Path out) throws IOException {
		System.err.println("root=" + root);
		System.err.println("out =" + out);
		for (JavaFile file : files) {
			final Path newPath = file.getNewPath(out);
			// System.err.println("Writing " + newPath);
			file.save(newPath);
		}
	}

	public final Path getRoot() {
		return root;
	}

}
