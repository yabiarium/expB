package lang.c;

import org.junit.Test;

import lang.c.testhelpter.CTokenizerTestHelper;

public class T04_21CTokenizerTest {

    CTokenizerTestHelper helper = new CTokenizerTestHelper();

    @Test
    public void ident1() {
        String testString = "*vAriable_1";
        CToken[] exceptedTokenList = {
            new CToken(CToken.TK_MULT, 1, 1, "*"),
            new CToken(CToken.TK_IDENT, 1, 2, "vAriable_1"),
            new CToken(CToken.TK_EOF, 1, 12, "end_of_file")
        };
        helper.acceptList(testString, exceptedTokenList);
    }

    @Test
    public void array() {
        String testString = "[100]";
        CToken[] exceptedTokenList = {
            new CToken(CToken.TK_LBRA, 1, 1, "["),
            new CToken(CToken.TK_NUM, 1, 2, "100"),
            new CToken(CToken.TK_RBRA, 1, 5, "]"),
            new CToken(CToken.TK_EOF, 1, 6, "end_of_file")
        };
        helper.acceptList(testString, exceptedTokenList);
    }

    @Test
    public void expression100() {
        String testString = "abc+-def[ghij+*k]lmn";
        CToken[] exceptedTokenList = {
            new CToken(CToken.TK_IDENT, 1, 1, "abc"),
            new CToken(CToken.TK_PLUS, 1, 4, "+"),
            new CToken(CToken.TK_MINUS, 1, 5, "-"),
            new CToken(CToken.TK_IDENT, 1, 6, "def"),
            new CToken(CToken.TK_LBRA, 1, 9, "["),
            new CToken(CToken.TK_IDENT, 1, 10, "ghij"),
            new CToken(CToken.TK_PLUS, 1, 14, "+"),
            new CToken(CToken.TK_MULT, 1, 15, "*"),
            new CToken(CToken.TK_IDENT, 1, 16, "k"),
            new CToken(CToken.TK_RBRA, 1, 17, "]"),
            new CToken(CToken.TK_IDENT, 1, 18, "lmn"),
            new CToken(CToken.TK_EOF, 1, 21, "end_of_file")
        };
        helper.acceptList(testString, exceptedTokenList);
    }

    @Test
    public void expression101() {
        String testString = "ab0c+-1def[gh2ij+*k3]4lm5n";
        CToken[] exceptedTokenList = {
            new CToken(CToken.TK_IDENT, 1, 1, "ab0c"),
            new CToken(CToken.TK_PLUS, 1, 5, "+"),
            new CToken(CToken.TK_MINUS, 1, 6, "-"),
            new CToken(CToken.TK_NUM, 1, 7, "1"),
            new CToken(CToken.TK_IDENT, 1, 8, "def"),
            new CToken(CToken.TK_LBRA, 1, 11, "["),
            new CToken(CToken.TK_IDENT, 1, 12, "gh2ij"),
            new CToken(CToken.TK_PLUS, 1, 17, "+"),
            new CToken(CToken.TK_MULT, 1, 18, "*"),
            new CToken(CToken.TK_IDENT, 1, 19, "k3"),
            new CToken(CToken.TK_RBRA, 1, 21, "]"),
            new CToken(CToken.TK_NUM, 1, 22, "4"),
            new CToken(CToken.TK_IDENT, 1, 23, "lm5n"),
            new CToken(CToken.TK_EOF, 1, 27, "end_of_file")
        };
        helper.acceptList(testString, exceptedTokenList);
    }
    @Test
    public void variable1() {
        String testString = "_ia\n"+
                            "_0a_1_aaa";

        CToken[] exceptedTokenList = {
            new CToken(CToken.TK_IDENT, 1, 1, "_ia"),
            new CToken(CToken.TK_IDENT, 2, 1, "_0a_1_aaa"),
            new CToken(CToken.TK_EOF, 2, 10, "end_of_file")
        };
        helper.acceptList(testString, exceptedTokenList);
    }

    @Test
    public void variable2() {
        String testString = "i_a\n" +
                            "&i_b\n" +
                            "ip_d\n" +
                            "*ip_e\n" +
                            "ia_f[i_a]\n" +
                            "&ia_f[20]\n" +
                            "ipa_g[*ip_d]\n" +
                            "*ipa_g[ia_f[i_a]]\n" +
                            "c_h";
        CToken[] exceptedTokenList = {
            new CToken(CToken.TK_IDENT, 1, 1, "i_a"),

            new CToken(CToken.TK_AMP, 2, 1, "&"),
            new CToken(CToken.TK_IDENT, 2, 2, "i_b"),

            new CToken(CToken.TK_IDENT, 3, 1, "ip_d"),

            new CToken(CToken.TK_MULT, 4, 1, "*"),
            new CToken(CToken.TK_IDENT, 4, 2, "ip_e"),

            new CToken(CToken.TK_IDENT, 5, 1, "ia_f"),
            new CToken(CToken.TK_LBRA, 5, 5, "["),
            new CToken(CToken.TK_IDENT, 5, 6, "i_a"),
            new CToken(CToken.TK_RBRA, 5, 9, "]"),

            new CToken(CToken.TK_AMP, 6, 1, "&"),
            new CToken(CToken.TK_IDENT, 6, 2, "ia_f"),
            new CToken(CToken.TK_LBRA, 6, 6, "["),
            new CToken(CToken.TK_NUM, 6, 7, "20"),
            new CToken(CToken.TK_RBRA, 6, 9, "]"),

            new CToken(CToken.TK_IDENT, 7, 1, "ipa_g"),
            new CToken(CToken.TK_LBRA, 7, 6, "["),
            new CToken(CToken.TK_MULT, 7, 7, "*"),
            new CToken(CToken.TK_IDENT, 7, 8, "ip_d"),
            new CToken(CToken.TK_RBRA, 7, 12, "]"),

            new CToken(CToken.TK_MULT, 8, 1, "*"),
            new CToken(CToken.TK_IDENT, 8, 2, "ipa_g"),
            new CToken(CToken.TK_LBRA, 8, 7, "["),
            new CToken(CToken.TK_IDENT, 8, 8, "ia_f"),
            new CToken(CToken.TK_LBRA, 8, 12, "["),
            new CToken(CToken.TK_IDENT, 8, 13, "i_a"),
            new CToken(CToken.TK_RBRA, 8, 16, "]"),
            new CToken(CToken.TK_RBRA, 8, 17, "]"),

            new CToken(CToken.TK_IDENT, 9, 1, "c_h"),

            new CToken(CToken.TK_EOF, 9, 4, "end_of_file")
        };
        helper.acceptList(testString, exceptedTokenList);
    }
}