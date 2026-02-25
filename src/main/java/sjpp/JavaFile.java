package sjpp;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class JavaFile {

    private final Context context;
    private final Define define;
    private final List<Line> input;
    private final List<String> output = new ArrayList<String>();
    private final Path path;
    private String packageName;
    private final String signature;

    public boolean matchesImportStatement(String importName) {
        String normalizedName = importName;

        // Handle static imports: "static com.example.Class.member" ->
        // "com.example.Class"
        if (importName.startsWith("static ")) {
            normalizedName = importName.substring("static ".length());
            // Remove the member part (last segment after the last dot)
            final int lastDot = normalizedName.lastIndexOf('.');
            if (lastDot != -1) {
                normalizedName = normalizedName.substring(0, lastDot);
            }
        }

        return signature.endsWith(normalizedName);
    }

    public JavaFile(Context context, Path path) throws IOException {
        this.path = path;
        this.context = context;
        this.input = Collections.unmodifiableList(Line.readAllLines(path));
        this.define = context.getDefine();
        this.signature = path.toAbsolutePath().normalize().toString().replaceAll("[^-\\w]", ".").replaceAll("\\.java$",
                "");

        for (Line line : input) {
            final String tmp = line.getPackageName();
            if (tmp != null)
                this.packageName = tmp;

            final Directive dir = line.directive();
            switch (dir) {
            case REMOVE_CURRENT_FOLDER:
                if (line.doesApplyOn(define))
                    context.removeCurrentFolder(this.packageName);
                break;
            case REMOVE_FOLDER_AND_SUBFOLDERS:
                if (line.doesApplyOn(define))
                    context.removeFolderAndSubfolders(this.packageName);
                break;
            case REMOVE_FILE:
                if (line.doesApplyOn(define))
                    context.removeFile(this);
                break;
            }
        }

        if (packageName == null)
            throw new IllegalArgumentException(path.toString());

    }

    @Override
    public String toString() {
        return path.toString();
    }

    public String getSignature() {
        return signature;
    }

    public void process() {

//        if (context.getMode() == ContextMode.REGULAR)
//            removeFirstHeader();

        if (context.getHeader() != null)
            output.addAll(0, context.getHeader().getLines());

        ProcessMode mode = ProcessMode.NORMAL;

        for (Line in : input) {
            final Directive dir = in.directive();
            String line = in.getLine();
            if (dir == Directive.IMPORT) {
                if (mode == ProcessMode.COMMENT)
                    line = commentThisLine(line);
                else {
                    final String importName = in.getImportName();
                    if (context.removeImportLine(importName))
                        line = commentThisLine(line);
                }
            } else if (dir == Directive.UNCOMMENT && in.doesApplyOn(define)) {
                mode = ProcessMode.UNCOMMENT;
                line = removeLine(line);
            } else if (dir == Directive.COMMENT && in.doesApplyOn(define)) {
                mode = ProcessMode.COMMENT;
                line = removeLine(line);
            } else if (dir == Directive.REVERT && in.doesApplyOn(define)) {
                mode = ProcessMode.REVERT;
                line = removeLine(line);
            } else if (dir == Directive.DONE) {
                mode = ProcessMode.NORMAL;
                line = removeLine(line);
            } else {
                if (mode == ProcessMode.COMMENT)
                    line = commentThisLine(line);
                else if (mode == ProcessMode.UNCOMMENT)
                    line = uncommentThisLine(line);
                else if (mode == ProcessMode.REVERT)
                    line = revertThisLine(line);
            }

            if (line != null)
                output.add(line);
        }

    }

    private String revertThisLine(String line) {
        if (isCommented(line))
            return uncommentThisLine(line);
        else
            return commentThisLine(line);
    }

    private boolean isCommented(String line) {
        return line.replaceAll("\\s+", "").startsWith("//");
    }

    private String uncommentThisLine(String line) {
        if (isCommented(line))
            return line.replaceFirst("//", "");
        return line;
    }

    private String commentThisLine(String line) {
        if (context.getMode() == ContextMode.DEBUG)
            return "// " + line;
        return null;
    }

    private String removeLine(String s) {
        if (context.getMode() == ContextMode.DEBUG)
            return s;
        return null;
    }

//    private void removeFirstHeader() {
//        if (input.get(0).startsWith("/*") == false)
//            return;
//        final ListIterator<String> it = input.listIterator();
//        while (it.hasNext()) {
//            final String line = it.next();
//            it.remove();
//            if (line.endsWith("*/"))
//                return;
//        }
//
//    }

    public Path getNewPath(Path out) {
        final Path root = context.getRoot();
        if (path.toString().startsWith(root.toString()) == false)
            throw new IllegalStateException();
        final String relative = path.toString().substring(root.toString().length());

        final String newPath = out.toString() + relative;
        return Paths.get(newPath);

    }

    public void save(Path newPath) throws IOException {
        output.add(0, Context.GENERATED);
        Files.createDirectories(newPath.getParent());
        Files.write(newPath, output);
    }

    static enum ProcessMode {
        NORMAL, COMMENT, UNCOMMENT, REVERT
    }

    public final String getPackageName() {
        return packageName;
    }
}
