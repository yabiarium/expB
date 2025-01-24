package lang.c;

import org.junit.Test;

import lang.c.testhelpter.CTokenizerTestHelper;

public class T10_21CTokenizerTest {

    CTokenizerTestHelper helper = new CTokenizerTestHelper();

    @Test
    public void chapter10test() {
        String testString = "int a,b,c,d;\r\n" + //
                        "const int *a=&45;"; 
        CToken[] exceptedTokenList = {
            new CToken(CToken.TK_INT, 1, 1, "int"),
            new CToken(CToken.TK_IDENT, 1, 5, "a"),
            new CToken(CToken.TK_COMMA, 1, 6, ","),
            new CToken(CToken.TK_IDENT, 1, 7, "b"),
            new CToken(CToken.TK_COMMA, 1, 8, ","),
            new CToken(CToken.TK_IDENT, 1, 9, "c"),
            new CToken(CToken.TK_COMMA, 1, 10, ","),
            new CToken(CToken.TK_IDENT, 1, 11, "d"),
            new CToken(CToken.TK_SEMI, 1, 12, ";"),
            new CToken(CToken.TK_CONST, 2, 1, "const"),
            new CToken(CToken.TK_INT, 2, 7, "int"),
            new CToken(CToken.TK_MULT, 2, 11, "*"),
            new CToken(CToken.TK_IDENT, 2, 12, "a"),
            new CToken(CToken.TK_ASSIGN, 2, 13, "="),
            new CToken(CToken.TK_AMP, 2, 14, "&"),
            new CToken(CToken.TK_NUM, 2, 15, "45"),
            new CToken(CToken.TK_SEMI, 2, 17, ";"),
            new CToken(CToken.TK_EOF, 2, 18, "end_of_file"),
        };
        helper.acceptList(testString, exceptedTokenList);
    }
}