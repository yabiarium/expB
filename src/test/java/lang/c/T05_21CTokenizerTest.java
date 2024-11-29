package lang.c;

import org.junit.Test;

import lang.c.testhelpter.CTokenizerTestHelper;

public class T05_21CTokenizerTest {

    CTokenizerTestHelper helper = new CTokenizerTestHelper();

    @Test
    public void statement() {
        String testString = "a=/*comment*/b;\n"+
                            "input a;\n"+
                            "output &b;";
        CToken[] exceptedTokenList = {
            new CToken(CToken.TK_IDENT, 1, 1, "a"),
            new CToken(CToken.TK_ASSIGN, 1, 2, "="),
            new CToken(CToken.TK_IDENT, 1, 14, "b"),
            new CToken(CToken.TK_SEMI, 1, 15, ";"),

            new CToken(CToken.TK_INPUT, 2, 1, "input"),
            new CToken(CToken.TK_IDENT, 2, 7, "a"),
            new CToken(CToken.TK_SEMI, 2, 8, ";"),

            new CToken(CToken.TK_OUTPUT, 3, 1, "output"),
            new CToken(CToken.TK_AMP, 3, 8, "&"),
            new CToken(CToken.TK_IDENT, 3, 9, "b"),
            new CToken(CToken.TK_SEMI, 3, 10, ";"),

            new CToken(CToken.TK_EOF, 3, 11, "end_of_file")
        };
        helper.acceptList(testString, exceptedTokenList);
    }
}