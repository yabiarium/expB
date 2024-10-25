package lang.c;

import org.junit.Test;

import lang.c.testhelpter.CTokenizerTestHelper;

public class T01_21CTokenizerTest {

    CTokenizerTestHelper helper = new CTokenizerTestHelper();

    @Test
    public void minusNumber() {
        String testString = "-100";
        CToken[] exceptedTokenList = {
            new CToken(CToken.TK_MINUS, 1, 1, "-"), // それぞれ「字句(Token)の種類」「行番号」「先頭からの開始位置」「認識される文字列」
            new CToken(CToken.TK_NUM, 1, 2, "100"),
            new CToken(CToken.TK_EOF, 1, 5, "end_of_file")
        };
        helper.acceptList(testString, exceptedTokenList);
    }

    @Test
    public void expression1() {
        String testString = "7 - 2";
        CToken[] exceptedTokenList = {
            new CToken(CToken.TK_NUM, 1, 1, "7"),
            new CToken(CToken.TK_MINUS, 1, 3, "-"),
            new CToken(CToken.TK_NUM, 1, 5, "2"),
            new CToken(CToken.TK_EOF, 1, 6, "end_of_file")
        };
        helper.acceptList(testString, exceptedTokenList);
    }

    @Test
    public void expression2() {
        String testString = "13 + 7 + 2";
        CToken[] exceptedTokenList = {
            new CToken(CToken.TK_NUM, 1, 1, "13"),
            new CToken(CToken.TK_PLUS, 1, 4, "+"),
            new CToken(CToken.TK_NUM, 1, 6, "7"),
            new CToken(CToken.TK_PLUS, 1, 8, "+"),
            new CToken(CToken.TK_NUM, 1, 10, "2"),
            new CToken(CToken.TK_EOF, 1, 11, "end_of_file")
        };
        helper.acceptList(testString, exceptedTokenList);
    }

    @Test
    public void inlineCommnetWithFactor() {
        String testString = "+ - // plus minus 記号";
        CToken[] exceptedTokenList = {
            new CToken(CToken.TK_PLUS, 1, 1, "+"),
            new CToken(CToken.TK_MINUS, 1, 3, "-"),
            new CToken(CToken.TK_EOF, 1, 21, "end_of_file")
        };
        helper.acceptList(testString, exceptedTokenList);
    }

    @Test
    public void lineCommentPlusExpression() {
        String testString = "// LINE_COMMENT\n" +
                            "13 + 7 + 2";
        CToken[] exceptedTokenList = {
            new CToken(CToken.TK_NUM, 2, 1, "13"),
            new CToken(CToken.TK_PLUS, 2, 4, "+"),
            new CToken(CToken.TK_NUM, 2, 6, "7"),
            new CToken(CToken.TK_PLUS, 2, 8, "+"),
            new CToken(CToken.TK_NUM, 2, 10, "2"),
            new CToken(CToken.TK_EOF, 2, 11, "end_of_file")
        };
        helper.acceptList(testString, exceptedTokenList);
    }

    @Test
    public void blockCommentWithFactor() {
        String testString = "/***/123/*/12/*/34/*/56/*/78    // 123 34 78 が出てくるはず";
        CToken[] exceptedTokenList = {
            new CToken(CToken.TK_NUM, 1, 6, "123"),
            new CToken(CToken.TK_NUM, 1, 17, "34"),
            new CToken(CToken.TK_NUM, 1, 27, "78"),
            // ↓colNoの54については，実装によってはもう少し小さくなるかもしれない．
            //通常は影響がない(行末まで読み飛ばすから)ので54にならなくてもOKとする
            new CToken(CToken.TK_EOF, 1, 53, "end_of_file") 
        };
        helper.acceptList(testString, exceptedTokenList);
    }

    @Test
    public void blockCommentPlusExpression() {
        String testString = "/* COMMENT_START AND COMMENT_LINE1\n" +
                            "   COMMENT_LINE2\n" +
                            "   COMMENT_LINE3\n" +
                            "   COMMENT_LINE4 AND COMMENT_END */\n" +
                            "13 + 7 + 2";
        CToken[] exceptedTokenList = {
            new CToken(CToken.TK_NUM, 5, 1, "13"),
            new CToken(CToken.TK_PLUS, 5, 4, "+"),
            new CToken(CToken.TK_NUM, 5, 6, "7"),
            new CToken(CToken.TK_PLUS, 5, 8, "+"),
            new CToken(CToken.TK_NUM, 5, 10, "2"),
            new CToken(CToken.TK_EOF, 5, 11, "end_of_file")
        };
        helper.acceptList(testString, exceptedTokenList);
    }

    @Test
    public void slashAndNumber() {
        String testString = "/100";
        CToken[] exceptedTokenList = {
            new CToken(CToken.TK_DIV, 1, 1, "/"),
            new CToken(CToken.TK_NUM, 1, 2, "100"),
            new CToken(CToken.TK_EOF, 1, 5, "end_of_file")
        };
        helper.acceptList(testString, exceptedTokenList);
    }

}
