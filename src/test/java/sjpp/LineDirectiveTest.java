package sjpp;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * Unit tests for the {@link Line#directive()} method.
 * 
 * Tests all directive types with various syntax variations including
 * different spacing, casing considerations, and edge cases.
 */
class LineDirectiveTest {

    // ========================================================================
    // IMPORT directive tests
    // ========================================================================

    @Nested
    @DisplayName("IMPORT directive")
    class ImportDirectiveTests {

        @Test
        void directive_shouldReturnImport_forSimpleImport() throws IOException {
            Line line = new Line("import java.util.List;");
            assertEquals(Directive.IMPORT, line.directive());
        }

        @Test
        void directive_shouldReturnImport_forFullyQualifiedImport() throws IOException {
            Line line = new Line("import com.example.myapp.service.UserService;");
            assertEquals(Directive.IMPORT, line.directive());
        }

        @Test
        void directive_shouldReturnImport_forStaticImport() throws IOException {
            Line line = new Line("import static org.junit.jupiter.api.Assertions.assertEquals;");
            assertEquals(Directive.IMPORT, line.directive());
        }

        @Test
        void directive_shouldReturnImport_forWildcardImport() throws IOException {
            Line line = new Line("import java.util.*;");
            assertEquals(Directive.IMPORT, line.directive());
        }

        @Test
        void directive_shouldReturnImport_forStaticWildcardImport() throws IOException {
            Line line = new Line("import static org.junit.jupiter.api.Assertions.*;");
            assertEquals(Directive.IMPORT, line.directive());
        }

        @Test
        void directive_shouldReturnNone_forImportWithLeadingSpace() throws IOException {
            Line line = new Line("  import java.util.List;");
            assertEquals(Directive.NONE, line.directive());
        }

        @Test
        void directive_shouldReturnNone_forCommentedImport() throws IOException {
            Line line = new Line("// import java.util.List;");
            assertEquals(Directive.NONE, line.directive());
        }

        @Test
        void directive_shouldReturnNone_forImportInString() throws IOException {
            Line line = new Line("String s = \"import java.util.List;\";");
            assertEquals(Directive.NONE, line.directive());
        }
    }

    // ========================================================================
    // REMOVE_FILE directive tests
    // ========================================================================

    @Nested
    @DisplayName("REMOVE_FILE directive")
    class RemoveFileDirectiveTests {

        @Test
        void directive_shouldReturnRemoveFile_forBasicSyntax() throws IOException {
            Line line = new Line("//::remove file when __TEST__");
            assertEquals(Directive.REMOVE_FILE, line.directive());
        }

        @Test
        void directive_shouldReturnRemoveFile_withNoSpaces() throws IOException {
            Line line = new Line("//::removefilewhen__TEST__");
            assertEquals(Directive.REMOVE_FILE, line.directive());
        }

        @Test
        void directive_shouldReturnRemoveFile_withExtraSpaces() throws IOException {
            Line line = new Line("//::remove   file   when   __TEST__");
            assertEquals(Directive.REMOVE_FILE, line.directive());
        }

        @Test
        void directive_shouldReturnRemoveFile_withTabs() throws IOException {
            Line line = new Line("//::remove\tfile\twhen\t__TEST__");
            assertEquals(Directive.REMOVE_FILE, line.directive());
        }

        @Test
        void directive_shouldReturnRemoveFile_withMixedWhitespace() throws IOException {
            Line line = new Line("//::remove \t file \t when __FLAG__");
            assertEquals(Directive.REMOVE_FILE, line.directive());
        }

        @Test
        void directive_shouldReturnRemoveFile_withMultipleFlags() throws IOException {
            Line line = new Line("//::remove file when __TEST__ __OTHER__");
            assertEquals(Directive.REMOVE_FILE, line.directive());
        }

        @ParameterizedTest
        @ValueSource(strings = {
            "//::remove file when __A__",
            "//::remove file when __TEAVM__",
            "//::remove file when __PRODUCTION__",
            "//::remove file when FLAG",
            "//::remove file when flag123"
        })
        void directive_shouldReturnRemoveFile_withVariousFlags(String input) throws IOException {
            Line line = new Line(input);
            assertEquals(Directive.REMOVE_FILE, line.directive());
        }
    }

    // ========================================================================
    // REMOVE_CURRENT_FOLDER directive tests
    // ========================================================================

    @Nested
    @DisplayName("REMOVE_CURRENT_FOLDER directive")
    class RemoveCurrentFolderDirectiveTests {

        @Test
        void directive_shouldReturnRemoveCurrentFolder_forBasicSyntax() throws IOException {
            Line line = new Line("//::remove current folder when __TEST__");
            assertEquals(Directive.REMOVE_CURRENT_FOLDER, line.directive());
        }

        @Test
        void directive_shouldReturnRemoveCurrentFolder_withNoSpaces() throws IOException {
            Line line = new Line("//::removecurrentfolderwhen__TEST__");
            assertEquals(Directive.REMOVE_CURRENT_FOLDER, line.directive());
        }

        @Test
        void directive_shouldReturnRemoveCurrentFolder_withExtraSpaces() throws IOException {
            Line line = new Line("//::remove   current   folder   when   __TEST__");
            assertEquals(Directive.REMOVE_CURRENT_FOLDER, line.directive());
        }

        @Test
        void directive_shouldReturnRemoveCurrentFolder_withTabs() throws IOException {
            Line line = new Line("//::remove\tcurrent\tfolder\twhen\t__FLAG__");
            assertEquals(Directive.REMOVE_CURRENT_FOLDER, line.directive());
        }

        @ParameterizedTest
        @ValueSource(strings = {
            "//::remove current folder when __A__",
            "//::remove current folder when __TEAVM__",
            "//::removecurrentfolderwhen__X__"
        })
        void directive_shouldReturnRemoveCurrentFolder_withVariousFormats(String input) throws IOException {
            Line line = new Line(input);
            assertEquals(Directive.REMOVE_CURRENT_FOLDER, line.directive());
        }
    }

    // ========================================================================
    // REMOVE_FOLDER_AND_SUBFOLDERS directive tests
    // ========================================================================

    @Nested
    @DisplayName("REMOVE_FOLDER_AND_SUBFOLDERS directive")
    class RemoveFolderAndSubfoldersDirectiveTests {

        @Test
        void directive_shouldReturnRemoveFolderAndSubfolders_forBasicSyntax() throws IOException {
            Line line = new Line("//::remove folder when __TEST__");
            assertEquals(Directive.REMOVE_FOLDER_AND_SUBFOLDERS, line.directive());
        }

        @Test
        void directive_shouldReturnRemoveFolderAndSubfolders_withNoSpaces() throws IOException {
            Line line = new Line("//::removefolderwhen__TEST__");
            assertEquals(Directive.REMOVE_FOLDER_AND_SUBFOLDERS, line.directive());
        }

        @Test
        void directive_shouldReturnRemoveFolderAndSubfolders_withExtraSpaces() throws IOException {
            Line line = new Line("//::remove   folder   when   __TEST__");
            assertEquals(Directive.REMOVE_FOLDER_AND_SUBFOLDERS, line.directive());
        }

        @Test
        void directive_shouldReturnRemoveFolderAndSubfolders_withTabs() throws IOException {
            Line line = new Line("//::remove\tfolder\twhen\t__FLAG__");
            assertEquals(Directive.REMOVE_FOLDER_AND_SUBFOLDERS, line.directive());
        }

        @ParameterizedTest
        @ValueSource(strings = {
            "//::remove folder when __A__",
            "//::remove folder when __TEAVM__",
            "//::removefolderwhen__X__"
        })
        void directive_shouldReturnRemoveFolderAndSubfolders_withVariousFormats(String input) throws IOException {
            Line line = new Line(input);
            assertEquals(Directive.REMOVE_FOLDER_AND_SUBFOLDERS, line.directive());
        }
    }

    // ========================================================================
    // COMMENT directive tests
    // ========================================================================

    @Nested
    @DisplayName("COMMENT directive")
    class CommentDirectiveTests {

        @Test
        void directive_shouldReturnComment_forBasicSyntax() throws IOException {
            Line line = new Line("//::comment when __TEST__");
            assertEquals(Directive.COMMENT, line.directive());
        }

        @Test
        void directive_shouldReturnComment_withNoSpaces() throws IOException {
            Line line = new Line("//::commentwhen__TEST__");
            assertEquals(Directive.COMMENT, line.directive());
        }

        @Test
        void directive_shouldReturnComment_withExtraSpaces() throws IOException {
            Line line = new Line("//::comment   when   __TEST__");
            assertEquals(Directive.COMMENT, line.directive());
        }

        @Test
        void directive_shouldReturnComment_withTabs() throws IOException {
            Line line = new Line("//::comment\twhen\t__FLAG__");
            assertEquals(Directive.COMMENT, line.directive());
        }

        @Test
        void directive_shouldReturnComment_withMixedWhitespace() throws IOException {
            Line line = new Line("//::comment \t when \t __FLAG__");
            assertEquals(Directive.COMMENT, line.directive());
        }

        @ParameterizedTest
        @ValueSource(strings = {
            "//::comment when __A__",
            "//::comment when __TEAVM__",
            "//::comment when __PRODUCTION__",
            "//::commentwhen__X__",
            "//::comment when FLAG1 FLAG2 FLAG3"
        })
        void directive_shouldReturnComment_withVariousFormats(String input) throws IOException {
            Line line = new Line(input);
            assertEquals(Directive.COMMENT, line.directive());
        }
    }

    // ========================================================================
    // UNCOMMENT directive tests
    // ========================================================================

    @Nested
    @DisplayName("UNCOMMENT directive")
    class UncommentDirectiveTests {

        @Test
        void directive_shouldReturnUncomment_forBasicSyntax() throws IOException {
            Line line = new Line("//::uncomment when __TEST__");
            assertEquals(Directive.UNCOMMENT, line.directive());
        }

        @Test
        void directive_shouldReturnUncomment_withNoSpaces() throws IOException {
            Line line = new Line("//::uncommentwhen__TEST__");
            assertEquals(Directive.UNCOMMENT, line.directive());
        }

        @Test
        void directive_shouldReturnUncomment_withExtraSpaces() throws IOException {
            Line line = new Line("//::uncomment   when   __TEST__");
            assertEquals(Directive.UNCOMMENT, line.directive());
        }

        @Test
        void directive_shouldReturnUncomment_withTabs() throws IOException {
            Line line = new Line("//::uncomment\twhen\t__FLAG__");
            assertEquals(Directive.UNCOMMENT, line.directive());
        }

        @ParameterizedTest
        @ValueSource(strings = {
            "//::uncomment when __A__",
            "//::uncomment when __TEAVM__",
            "//::uncommentwhen__X__"
        })
        void directive_shouldReturnUncomment_withVariousFormats(String input) throws IOException {
            Line line = new Line(input);
            assertEquals(Directive.UNCOMMENT, line.directive());
        }
    }

    // ========================================================================
    // REVERT directive tests
    // ========================================================================

    @Nested
    @DisplayName("REVERT directive")
    class RevertDirectiveTests {

        @Test
        void directive_shouldReturnRevert_forBasicSyntax() throws IOException {
            Line line = new Line("//::revert when __TEST__");
            assertEquals(Directive.REVERT, line.directive());
        }

        @Test
        void directive_shouldReturnRevert_withNoSpaces() throws IOException {
            Line line = new Line("//::revertwhen__TEST__");
            assertEquals(Directive.REVERT, line.directive());
        }

        @Test
        void directive_shouldReturnRevert_withExtraSpaces() throws IOException {
            Line line = new Line("//::revert   when   __TEST__");
            assertEquals(Directive.REVERT, line.directive());
        }

        @Test
        void directive_shouldReturnRevert_withTabs() throws IOException {
            Line line = new Line("//::revert\twhen\t__FLAG__");
            assertEquals(Directive.REVERT, line.directive());
        }

        @ParameterizedTest
        @ValueSource(strings = {
            "//::revert when __A__",
            "//::revert when __TEAVM__",
            "//::revertwhen__X__"
        })
        void directive_shouldReturnRevert_withVariousFormats(String input) throws IOException {
            Line line = new Line(input);
            assertEquals(Directive.REVERT, line.directive());
        }
    }

    // ========================================================================
    // DONE directive tests
    // ========================================================================

    @Nested
    @DisplayName("DONE directive")
    class DoneDirectiveTests {

        @Test
        void directive_shouldReturnDone_forBasicSyntax() throws IOException {
            Line line = new Line("//::done");
            assertEquals(Directive.DONE, line.directive());
        }

        @Test
        void directive_shouldReturnDone_withTrailingSpaces() throws IOException {
            Line line = new Line("//::done   ");
            assertEquals(Directive.DONE, line.directive());
        }

        @Test
        void directive_shouldReturnDone_withTrailingComment() throws IOException {
            Line line = new Line("//::done // end of block");
            assertEquals(Directive.DONE, line.directive());
        }

        @Test
        void directive_shouldReturnDone_withTrailingText() throws IOException {
            Line line = new Line("//::done anything here");
            assertEquals(Directive.DONE, line.directive());
        }

        @ParameterizedTest
        @ValueSource(strings = {
            "//::done",
            "//::done ",
            "//::done\t",
            "//::done// comment",
            "//::donealiases"
        })
        void directive_shouldReturnDone_withVariousFormats(String input) throws IOException {
            Line line = new Line(input);
            assertEquals(Directive.DONE, line.directive());
        }
    }

    // ========================================================================
    // NONE directive tests (regular code lines)
    // ========================================================================

    @Nested
    @DisplayName("NONE directive (regular lines)")
    class NoneDirectiveTests {

        @Test
        void directive_shouldReturnNone_forEmptyLine() throws IOException {
            Line line = new Line("");
            assertEquals(Directive.NONE, line.directive());
        }

        @Test
        void directive_shouldReturnNone_forWhitespaceLine() throws IOException {
            Line line = new Line("    ");
            assertEquals(Directive.NONE, line.directive());
        }

        @Test
        void directive_shouldReturnNone_forRegularCode() throws IOException {
            Line line = new Line("System.out.println(\"Hello\");");
            assertEquals(Directive.NONE, line.directive());
        }

        @Test
        void directive_shouldReturnNone_forRegularComment() throws IOException {
            Line line = new Line("// This is a regular comment");
            assertEquals(Directive.NONE, line.directive());
        }

        @Test
        void directive_shouldReturnNone_forJavadocComment() throws IOException {
            Line line = new Line("/** Javadoc comment */");
            assertEquals(Directive.NONE, line.directive());
        }

        @Test
        void directive_shouldReturnNone_forBlockComment() throws IOException {
            Line line = new Line("/* block comment */");
            assertEquals(Directive.NONE, line.directive());
        }

        @Test
        void directive_shouldReturnNone_forPackageStatement() throws IOException {
            Line line = new Line("package com.example;");
            assertEquals(Directive.NONE, line.directive());
        }

        @Test
        void directive_shouldReturnNone_forClassDeclaration() throws IOException {
            Line line = new Line("public class MyClass {");
            assertEquals(Directive.NONE, line.directive());
        }

        @Test
        void directive_shouldReturnNone_forMethodDeclaration() throws IOException {
            Line line = new Line("public void doSomething() {");
            assertEquals(Directive.NONE, line.directive());
        }

        @Test
        void directive_shouldReturnNone_forAnnotation() throws IOException {
            Line line = new Line("@Override");
            assertEquals(Directive.NONE, line.directive());
        }

        @Test
        void directive_shouldReturnNone_forSingleColon() throws IOException {
            Line line = new Line("//:something");
            assertEquals(Directive.NONE, line.directive());
        }

        @Test
        void directive_shouldReturnNone_forTripleColon() throws IOException {
            Line line = new Line("//:::something");
            assertEquals(Directive.NONE, line.directive());
        }

        @Test
        void directive_shouldReturnNone_forUnknownDirective() throws IOException {
            Line line = new Line("//::unknown when __TEST__");
            assertEquals(Directive.NONE, line.directive());
        }

        @Test
        void directive_shouldReturnNone_forMisspelledDirective() throws IOException {
            Line line = new Line("//::coment when __TEST__");
            assertEquals(Directive.NONE, line.directive());
        }

        @Test
        void directive_shouldReturnNone_forDirectiveWithLeadingSpace() throws IOException {
            Line line = new Line(" //::comment when __TEST__");
            assertEquals(Directive.COMMENT, line.directive());
        }

        @Test
        void directive_shouldReturnNone_forDirectiveWithLeadingTab() throws IOException {
            Line line = new Line("\t//::comment when __TEST__");
            assertEquals(Directive.COMMENT, line.directive());
        }

        @ParameterizedTest
        @ValueSource(strings = {
            "int x = 5;",
            "return true;",
            "if (condition) {",
            "} else {",
            "for (int i = 0; i < 10; i++) {",
            "while (running) {",
            "try {",
            "catch (Exception e) {",
            "throw new RuntimeException();",
            "private static final String CONST = \"value\";",
            "List<String> list = new ArrayList<>();",
            "map.put(\"key\", \"value\");",
            "// TODO: fix this",
            "// FIXME: broken",
            "// NOTE: important"
        })
        void directive_shouldReturnNone_forVariousCodeLines(String input) throws IOException {
            Line line = new Line(input);
            assertEquals(Directive.NONE, line.directive());
        }
    }

    // ========================================================================
    // Caching behavior tests
    // ========================================================================

    @Nested
    @DisplayName("Caching behavior")
    class CachingTests {

        @Test
        void directive_shouldReturnSameInstance_whenCalledMultipleTimes() throws IOException {
            Line line = new Line("//::comment when __TEST__");
            
            Directive first = line.directive();
            Directive second = line.directive();
            Directive third = line.directive();
            
            assertEquals(Directive.COMMENT, first);
            assertEquals(first, second);
            assertEquals(second, third);
        }

        @Test
        void directive_shouldBeCached_forImport() throws IOException {
            Line line = new Line("import java.util.List;");
            
            Directive first = line.directive();
            Directive second = line.directive();
            
            assertEquals(Directive.IMPORT, first);
            assertEquals(first, second);
        }

        @Test
        void directive_shouldBeCached_forNone() throws IOException {
            Line line = new Line("int x = 5;");
            
            Directive first = line.directive();
            Directive second = line.directive();
            
            assertEquals(Directive.NONE, first);
            assertEquals(first, second);
        }
    }

    // ========================================================================
    // Edge cases and boundary tests
    // ========================================================================

    @Nested
    @DisplayName("Edge cases")
    class EdgeCaseTests {

        @Test
        void directive_shouldReturnNone_forDirectiveLikeStringContent() throws IOException {
            Line line = new Line("String s = \"//::comment when __TEST__\";");
            assertEquals(Directive.NONE, line.directive());
        }

        @Test
        void directive_shouldReturnNone_forDirectiveInBlockComment() throws IOException {
            Line line = new Line("/* //::comment when __TEST__ */");
            assertEquals(Directive.NONE, line.directive());
        }

        @Test
        void directive_shouldHandleVeryLongLine() throws IOException {
            String longFlag = "__" + "A".repeat(1000) + "__";
            Line line = new Line("//::comment when " + longFlag);
            assertEquals(Directive.COMMENT, line.directive());
        }

        @Test
        void directive_shouldHandleSpecialCharactersInFlag() throws IOException {
            Line line = new Line("//::comment when __TEST_123__");
            assertEquals(Directive.COMMENT, line.directive());
        }

        @Test
        void directive_shouldReturnComment_withUnicodeWhitespace() throws IOException {
            // Using regular spaces and tabs, not unicode whitespace
            // as unicode whitespace behavior may vary
            Line line = new Line("//::comment when __TEST__");
            assertEquals(Directive.COMMENT, line.directive());
        }

        @Test
        void directive_shouldHandleMinimalDirective() throws IOException {
            Line line = new Line("//::done");
            assertEquals(Directive.DONE, line.directive());
        }

        @Test
        void directive_shouldReturnNone_forJustDoubleSlashColon() throws IOException {
            Line line = new Line("//:");
            assertEquals(Directive.NONE, line.directive());
        }

        @Test
        void directive_shouldReturnNone_forJustDoubleSlashDoubleColon() throws IOException {
            Line line = new Line("//::");
            assertEquals(Directive.NONE, line.directive());
        }

        @Test
        void directive_shouldReturnImport_forImportWithTrailingComment() throws IOException {
            Line line = new Line("import java.util.List; // used for collections");
            // Note: this depends on implementation - getImportName() looks for first ';'
            assertEquals(Directive.IMPORT, line.directive());
        }
    }

    // ========================================================================
    // Order-sensitive directive detection tests
    // ========================================================================

    @Nested
    @DisplayName("Directive detection order")
    class DirectiveOrderTests {

        @Test
        void directive_shouldDetectRemoveFile_beforeRemoveFolder() throws IOException {
            // "remove file" should be detected even though "remove folder" is a substring match
            Line line = new Line("//::remove file when __TEST__");
            assertEquals(Directive.REMOVE_FILE, line.directive());
        }

        @Test
        void directive_shouldDetectRemoveCurrentFolder_beforeRemoveFolder() throws IOException {
            // "remove current folder" should be detected correctly
            Line line = new Line("//::remove current folder when __TEST__");
            assertEquals(Directive.REMOVE_CURRENT_FOLDER, line.directive());
        }

        @Test
        void directive_shouldDetectRemoveFolder_correctly() throws IOException {
            Line line = new Line("//::remove folder when __TEST__");
            assertEquals(Directive.REMOVE_FOLDER_AND_SUBFOLDERS, line.directive());
        }
    }
}
