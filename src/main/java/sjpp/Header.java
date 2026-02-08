package sjpp;

import java.io.IOException;
import java.util.List;

public class Header {

    private List<String> headerLines;

    public Header(List<String> headerLines) throws IOException {
        this.headerLines = headerLines;
    }

    public List<String> getLines() {
        return headerLines;
    }
}
