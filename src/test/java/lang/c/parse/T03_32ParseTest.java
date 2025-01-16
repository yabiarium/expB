package lang.c.parse;

import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

import lang.FatalErrorException;
import lang.c.testhelpter.ParseTestHelper;
// import lang.c.testhelpter.ParseTestHelper2;
import lang.c.testhelpter.TestDataAndErrMessage;

@RunWith(Enclosed.class)
public class T03_32ParseTest {

    public static class FactorTest {
        ParseTestHelper<Factor> factorHelper = new ParseTestHelper<Factor>(Factor.class);
    
        @Test
        public void trueTest() throws FatalErrorException {
            String[] testDataArr = {
                "300", "-100", "+200"
            };
            factorHelper.parseAcceptTestList(testDataArr);
        }
    }

    public static class TermTest {
        ParseTestHelper<Term> termHelper = new ParseTestHelper<Term>(Term.class);

        @Test
        public void trueTest() throws FatalErrorException {
            String[] testDataArr = {
                "1*2",
                "4/3",
                "1*2*3",
                "1/2/3",
                "1*2/3*4/5"
            };
            termHelper.parseAcceptTestList(testDataArr);
        }
    }

    public static class UnsignedFactorTest {
        ParseTestHelper<UnsignedFactor> uFactorHelper = new ParseTestHelper<UnsignedFactor>(UnsignedFactor.class);

        @Test
        public void trueTest() throws FatalErrorException {
            String[] testDataArr = {
                "(1+2)",
                "&100",
                "400",
            };
            uFactorHelper.parseAcceptTestList(testDataArr);
        }
    }

    public static class ExpressionTest {
        ParseTestHelper<Expression> expressionHelper = new ParseTestHelper<Expression>(Expression.class);

        // Expression 正当例
        // parse() は後ろに余計なTokenがあっても acceptされる（余計な Token の前までのToken が使われる) 点に注意
        @Test
        public void trueTest() throws FatalErrorException {
            String[] testDataArr = {
                    "&100*&100",         // parse() は通るはず (semanticCheck() でエラーを出すので)
                    "(1+3)*(2-(3+4))",
                    "100--200",
                    "100-200)"           // parse() は通る (currentToken が ')' になった状態で終了)
            };
            expressionHelper.parseAcceptTestList(testDataArr);
        }
    
        @Test
        public void falseETest() throws FatalErrorException {
            TestDataAndErrMessage[] arr = {
                //new TestDataAndErrMessage("1**1", "*の後ろはfactorです"),
                new TestDataAndErrMessage("200*/100", "*の後ろはfactorです"),
                new TestDataAndErrMessage("100---200", "-の後ろはunsignedFactorです"),
                new TestDataAndErrMessage("(100-200", ")がありません"),
            };
            expressionHelper.parseRejectTestList(arr);
        }

    }
}
