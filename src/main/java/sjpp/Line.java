package sjpp;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Line {

    private final String line;
    private Directive cachedDirective;
    private Boolean cachedDoesApplyOn;

    private Line(String line) {
        this.line = line;
    }

    public String getText() {
        return line;
    }

    @Override
    public String toString() {
        return line;
    }

    public String getLine() {
        return line;
    }

    public synchronized Directive directive() {
        if (cachedDirective == null)
            cachedDirective = Directive.directiveInLine(line);

        return cachedDirective;
    }

    public static List<Line> readAllLines(Path path) throws IOException {
        return Files.readAllLines(path).stream().map(Line::new).collect(Collectors.toList());
    }

    public synchronized boolean doesApplyOn(Define define) {
        if (cachedDoesApplyOn == null) {
            List<String> words = Arrays.asList(line.replaceAll("[:/]", "").split("\\s+"));
            final int idx = words.indexOf("when");
            if (idx == -1)
                throw new RuntimeException("Syntax error on " + line);
            words = words.subList(idx + 1, words.size());
            cachedDoesApplyOn = words.contains(define.getId());
        }

        return cachedDoesApplyOn;
    }

    public boolean startsWith(String s) {
        return line.startsWith(s);
    }

    public String getImportName() {
        final int x = line.indexOf(';');
        return line.substring("import ".length(), x).trim();
    }

    public String getPackageName() {
        if (line.startsWith("package ")) {
            final int x = line.indexOf(';');
            if (x != -1)
                return line.substring("package ".length(), x).trim();
        }
        return null;

    }

}
