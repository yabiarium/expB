package lang.c;

import org.junit.Test;

import lang.c.testhelpter.CTokenizerTestHelper;

public class T02_21CTokenizerTest {

    CTokenizerTestHelper helper = new CTokenizerTestHelper();

    //16進数のテスト
    @Test
    public void hexOK1() {
        String testString = "&0xffff"; // NUM で認識されるはず
        CToken[] exceptedTokenList = {
            new CToken(CToken.TK_AMP, 1, 1, "&"),
            new CToken(CToken.TK_NUM, 1, 2, "0xffff"),
            new CToken(CToken.TK_EOF, 1, 8, "end_of_file")
        };
        helper.acceptList(testString, exceptedTokenList);
    }

    @Test
    public void hexNG1() {
        String testString = "0x10000";   // ILL で認識されるはず
        CToken[] exceptedTokenList = {
            new CToken(CToken.TK_ILL, 1, 1, "0x10000"),
            new CToken(CToken.TK_EOF, 1, 8, "end_of_file")
        };
        helper.acceptList(testString, exceptedTokenList);
    }

    @Test
    public void hexAndIll() {
        String testString = "0xffgf";  // NUM(255) ILL ILL で認識されるはず
        CToken[] exceptedTokenList = {
            new CToken(CToken.TK_NUM, 1, 1, "0xff"),
            new CToken(CToken.TK_IDENT, 1, 5, "gf"), //CV04で変更
            // new CToken(CToken.TK_ILL, 1, 5, "g"),
            // new CToken(CToken.TK_ILL, 1, 6, "f"),
            new CToken(CToken.TK_EOF, 1, 7, "end_of_file")
        };
        helper.acceptList(testString, exceptedTokenList);
    }

    //ここから8進数のテスト
    @Test
    public void octOK1() {
        String testString = "0177777";   // NUM で認識されるはず(8進数16bitの最大値)
        CToken[] exceptedTokenList = {
            new CToken(CToken.TK_NUM, 1, 1, "0177777"),
            new CToken(CToken.TK_EOF, 1, 8, "end_of_file")
        };
        helper.acceptList(testString, exceptedTokenList);
    }

    @Test
    public void octNG1() {
        String testString = "0200000"; // ILL で認識されるはず(上の数値+1で丁度オーバーフロー)
        CToken[] exceptedTokenList = {
            new CToken(CToken.TK_ILL, 1, 1, "0200000"),
            new CToken(CToken.TK_EOF, 1, 8, "end_of_file")
        };
        helper.acceptList(testString, exceptedTokenList);
    }

    @Test
    public void octAndNum() {
        String testString = "01786"; // NUM(15) NUM(86) で認識されるはず (NUM(15)は8進数で8+7)
        CToken[] exceptedTokenList = {
            new CToken(CToken.TK_NUM, 1, 1, "017"),
            new CToken(CToken.TK_NUM, 1, 4, "86"),
            new CToken(CToken.TK_EOF, 1, 6, "end_of_file")
        };
        helper.acceptList(testString, exceptedTokenList);
    }

    @Test
    public void numOK1() {
        String testString = "32767";   // NUM で認識されるはず
        CToken[] exceptedTokenList = {
            new CToken(CToken.TK_NUM, 1, 1, "32767"),
            new CToken(CToken.TK_EOF, 1, 6, "end_of_file")
        };
        helper.acceptList(testString, exceptedTokenList);
    }

    @Test
    public void numOK2() {
        String testString = "32768";   // NUM で認識されるはず
        CToken[] exceptedTokenList = {
            new CToken(CToken.TK_NUM, 1, 1, "32768"),
            new CToken(CToken.TK_EOF, 1, 6, "end_of_file")
        };
        helper.acceptList(testString, exceptedTokenList);
    }

    @Test
    public void numNG1() {
        String testString = "&32769";  // AMP ILL で認識されるはず
        CToken[] exceptedTokenList = {
            new CToken(CToken.TK_AMP, 1, 1, "&"),
            new CToken(CToken.TK_ILL, 1, 2, "32769"),
            new CToken(CToken.TK_EOF, 1, 7, "end_of_file")
        };
        helper.acceptList(testString, exceptedTokenList);
    }

}
