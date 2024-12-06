package lang.c;

import org.junit.Test;

import lang.c.testhelpter.CTokenizerTestHelper;

public class T07_21CTokenizerTest {

    CTokenizerTestHelper helper = new CTokenizerTestHelper();

    @Test
    public void chapter7test() {
        String testString = "if{else}endif;while(i_a<i_b)output i_c;"; 
        CToken[] exceptedTokenList = {
            new CToken(CToken.TK_IF, 1, 1, "if"),
            new CToken(CToken.TK_LCUR, 1, 3, "{"),
            new CToken(CToken.TK_ELSE, 1, 4, "else"),
            new CToken(CToken.TK_RCUR, 1, 8, "}"),
            new CToken(CToken.TK_IDENT, 1, 9, "endif"), //TK_ENDIFは作らない
            new CToken(CToken.TK_SEMI, 1, 14, ";"),
            new CToken(CToken.TK_WHILE,1, 15, "while"),
            new CToken(CToken.TK_LPAR, 1, 20, "("),
            new CToken(CToken.TK_IDENT, 1, 21, "i_a"),
            new CToken(CToken.TK_LT, 1, 24, "<"),
            new CToken(CToken.TK_IDENT, 1, 25, "i_b"),
            new CToken(CToken.TK_RPAR, 1, 28, ")"),
            new CToken(CToken.TK_OUTPUT, 1, 29, "output"),
            new CToken(CToken.TK_IDENT, 1, 36, "i_c"),
            new CToken(CToken.TK_SEMI, 1, 39, ";"),
            new CToken(CToken.TK_EOF, 1, 40, "end_of_file")
        };
        helper.acceptList(testString, exceptedTokenList);
    }
}