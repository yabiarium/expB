package lang.c;

import org.junit.Test;

import lang.c.testhelpter.CTokenizerTestHelper;

public class T06_21CTokenizerTest {

    CTokenizerTestHelper helper = new CTokenizerTestHelper();

    @Test
    public void statement() {
        String testString = """
            =100
            true
            false
            <<=>>=!===
            !<="""; 
            // これまでの codeGen() での扱い同様，各行の先頭に空白は入っておらず，各行は colNol=1 から始まる
            // また，  =100 の行は lineNo は 2 ではなく 1 である点も注意

        CToken[] exceptedTokenList = {
            new CToken(CToken.TK_ASSIGN, 1, 1, "="),
            new CToken(CToken.TK_NUM, 1, 2, "100"),

            new CToken(CToken.TK_TRUE, 2, 1, "true"),

            new CToken(CToken.TK_FALSE, 3, 1, "false"),

            new CToken(CToken.TK_LT, 4, 1, "<"),
            new CToken(CToken.TK_LE, 4, 2, "<="),
            new CToken(CToken.TK_GT, 4, 4, ">"),
            new CToken(CToken.TK_GE, 4, 5, ">="),
            new CToken(CToken.TK_NE, 4, 7, "!="),
            new CToken(CToken.TK_EQ, 4, 9, "=="),

            new CToken(CToken.TK_ILL, 5, 1, "!"),
            new CToken(CToken.TK_LE, 5, 2, "<="),

            new CToken(CToken.TK_EOF, 5, 4, "end_of_file")
        };
        helper.acceptList(testString, exceptedTokenList);
    }
}