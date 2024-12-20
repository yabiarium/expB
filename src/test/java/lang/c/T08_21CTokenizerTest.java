package lang.c;

import org.junit.Test;

import lang.c.testhelpter.CTokenizerTestHelper;

public class T08_21CTokenizerTest {

    CTokenizerTestHelper helper = new CTokenizerTestHelper();

    @Test
    public void chapter8test() {
        String testString = "||&&!|;"; 
        CToken[] exceptedTokenList = {
            new CToken(CToken.TK_OR, 1, 1, "||"),
            new CToken(CToken.TK_AND, 1, 3, "&&"),
            new CToken(CToken.TK_NOT, 1, 5, "!"),
            new CToken(CToken.TK_ILL, 1, 6, "|"),
            new CToken(CToken.TK_SEMI, 1, 7, ";"),
            new CToken(CToken.TK_EOF, 1, 8, "end_of_file")
        };
        helper.acceptList(testString, exceptedTokenList);
    }
}