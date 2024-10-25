package lang.c;

import org.junit.Test;

import lang.c.testhelpter.CTokenizerTestHelper;

public class T03_21CTokenizerTest {

    CTokenizerTestHelper helper = new CTokenizerTestHelper();

    @Test
    public void asterSlashNumber() {
        String testString = "*100\n"
                            + "/100";
        CToken[] exceptedTokenList = {
            new CToken(CToken.TK_MULT, 1, 1, "*"),
            new CToken(CToken.TK_NUM, 1, 2, "100"),
            new CToken(CToken.TK_DIV, 2, 1, "/"),
            new CToken(CToken.TK_NUM, 2, 2, "100"),
            new CToken(CToken.TK_EOF, 2, 5, "end_of_file")
        };
        helper.resetEnvironment();
        helper.acceptList(testString, exceptedTokenList);
    }

    @Test
    public void expression1() {
        String testString = "(1+2)*3/-(4-5)";
        CToken[] exceptedTokenList = {
            new CToken(CToken.TK_LPAR, 1, 1, "("),
            new CToken(CToken.TK_NUM, 1, 2, "1"),
            new CToken(CToken.TK_PLUS, 1, 3, "+"),
            new CToken(CToken.TK_NUM, 1, 4, "2"),
            new CToken(CToken.TK_RPAR, 1, 5, ")"),
            new CToken(CToken.TK_MULT, 1, 6, "*"),
            new CToken(CToken.TK_NUM, 1, 7, "3"),
            new CToken(CToken.TK_DIV, 1, 8, "/"),
            new CToken(CToken.TK_MINUS, 1, 9, "-"),
            new CToken(CToken.TK_LPAR, 1, 10, "("),
            new CToken(CToken.TK_NUM, 1, 11, "4"),
            new CToken(CToken.TK_MINUS, 1, 12, "-"),
            new CToken(CToken.TK_NUM, 1, 13, "5"),
            new CToken(CToken.TK_RPAR, 1, 14, ")"),
            new CToken(CToken.TK_EOF, 1, 15, "end_of_file")
        };
        helper.acceptList(testString, exceptedTokenList);
    }

    @Test
    public void expression2() {
        String testString = "+4--5++2";
        CToken[] exceptedTokenList = {
            new CToken(CToken.TK_PLUS, 1, 1, "+"),
            new CToken(CToken.TK_NUM, 1, 2, "4"),
            new CToken(CToken.TK_MINUS, 1, 3, "-"),
            new CToken(CToken.TK_MINUS, 1, 4, "-"),
            new CToken(CToken.TK_NUM, 1, 5, "5"),
            new CToken(CToken.TK_PLUS, 1, 6, "+"),
            new CToken(CToken.TK_PLUS, 1, 7, "+"),
            new CToken(CToken.TK_NUM, 1, 8, "2"),
            new CToken(CToken.TK_EOF, 1, 9, "end_of_file")
        };
        helper.acceptList(testString, exceptedTokenList);
    }

    @Test
    public void expressionBlockCommentEOF() {
        String testString = "(1+2)/*3+4";
        CToken[] exceptedTokenList = {
            new CToken(CToken.TK_LPAR, 1, 1, "("),
            new CToken(CToken.TK_NUM, 1, 2, "1"),
            new CToken(CToken.TK_PLUS, 1, 3, "+"),
            new CToken(CToken.TK_NUM, 1, 4, "2"),
            new CToken(CToken.TK_RPAR, 1, 5, ")"),
            new CToken(CToken.TK_EOF, 1, 11, "end_of_file")
        };
        helper.acceptList(testString, exceptedTokenList);
    }
}
