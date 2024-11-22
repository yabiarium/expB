package lang.c.parse;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

import lang.FatalErrorException;
import lang.c.testhelpter.ParseTestHelper;
import lang.c.testhelpter.ParseTestHelper2;
import lang.c.testhelpter.TestDataAndErrMessage;

@RunWith(Enclosed.class)
public class T01_32ParseTest {

    public static class ExpressionSubTest {
        ParseTestHelper2<Term,ExpressionSub> expressionSubHelper = new ParseTestHelper2<Term,ExpressionSub>(Term.class,ExpressionSub.class);
    
        @Test
        public void trueESTest() throws FatalErrorException {
            // 自分で実装しよう
            String[] testDataArr = {
                "1-3"
        };
            expressionSubHelper.parseAcceptTestList(testDataArr);
        }
    
        @Test
        public void falseESTest() throws FatalErrorException {
            TestDataAndErrMessage[] arr = {
                new TestDataAndErrMessage("1-#", "-の後ろはtermです"),
            };
            expressionSubHelper.parseRejectTestList(arr);
        }
    }

    public static class ExpressionTest {
        ParseTestHelper<Expression> expressionHelper = new ParseTestHelper<Expression>(Expression.class);

        // Expression 正当例
        // parse() は後ろに余計なTokenがあっても acceptされる（余計な Token の前までのToken が使われる) 点に注意
        @Test
        public void trueETest() throws FatalErrorException {
            String[] testDataArr = {
                    "1+2-3+4-5", // 全部解釈されて accept
            };
            expressionHelper.parseAcceptTestList(testDataArr);
        }
    
        @Ignore
        @Test
        public void falseETest() throws FatalErrorException {
            // 自分で実装しよう
            TestDataAndErrMessage[] arr = {
                new TestDataAndErrMessage("1+2--3", "-の後ろはtermです"),
            };
            expressionHelper.parseRejectTestList(arr);
        }

    }

    public static class ProgramTest {
        ParseTestHelper<Program> programHelper = new ParseTestHelper<Program>(Program.class);

        // Program 正当例
        @Ignore //CV05
        @Test
        public void accept() throws FatalErrorException {
            String[] testDataArr = {
                    "2", // 全部解釈されて accept
                    "1+3+434+3", // 全部解釈されて accept
            };
            programHelper.parseAcceptTestList(testDataArr);
        }

        // Program 不当例
        // parse() の Reject は，BNF 定義における「2つ目以降，必ず必要な項目」がない場合をチェックすること
        // CV00 では， program ::= expression EOF の EOFのチェックと，expressionAdd ::= PLUS terms
        // の term のチェックしかできない．
        // expression ::= term { expressionAdd | expressionSub } だから，expressionAdd や expressionSub
        // がないことのテストをしたくなるかもしれないが
        // { } は繰り返し回数0を許すため，exppressionAdd が0個でも acceptされるため意味がない
        @Ignore //CV05
        @Test
        public void reject() throws FatalErrorException {
            TestDataAndErrMessage[] arr = {
                    // new TestDataAndErrMessage("1", "これは正当例なのでこのデータのテストは失敗します"),
                    new TestDataAndErrMessage("1+", "+の後ろはtermです"),
                    //new TestDataAndErrMessage("1+2+3+4+5-", "プログラムの最後にゴミがあります"),
                    new TestDataAndErrMessage("2030###", "プログラムの最後にゴミがあります"),
            };
            programHelper.parseRejectTestList(arr);
        }
    }
}
