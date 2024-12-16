package lang.c.parse;

import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

import lang.FatalErrorException;
import lang.c.testhelpter.ParseTestHelper;
import lang.c.testhelpter.ParseTestHelper2;
import lang.c.testhelpter.TestDataAndErrMessage;

@RunWith(Enclosed.class)
public class T08_32ParseTest {

    public static class ConditionBlockTest {
        ParseTestHelper<ConditionBlock> ConditionBlockrHelper = new ParseTestHelper<ConditionBlock>(ConditionBlock.class);

        // conditionBlock  ::= LPAR conditionExpression RPAR
        @Test
        public void trueTest() throws FatalErrorException {
            String[] testDataArr = {
                //"(10<i_c)",
                "([i_a==(10+1)*2 || true] && false)",
                "(i_a==10+1*2 || true)"
            };
            ConditionBlockrHelper.parseAcceptTestList(testDataArr);
        }

        @Test
        public void falseTest() throws FatalErrorException {
            TestDataAndErrMessage[] arr = {
                new TestDataAndErrMessage("()", "(の後ろはconditionExpressionです"), //中身が無い
                new TestDataAndErrMessage("(10<=i_a", ")がありません"), //]が無い
            };
            ConditionBlockrHelper.parseRejectTestList(arr);
        }
    }

    //省略 conditionExpression ::= conditionTerm { ExpressionOr }

    public static class ExpressionOrTest {
        ParseTestHelper2<ConditionTerm,ExpressionOr> ExpressionOrHelper = new ParseTestHelper2<ConditionTerm,ExpressionOr>(ConditionTerm.class,ExpressionOr.class);

        //expressionOr ::= OR conditionTerm
        @Test
        public void trueTest() throws FatalErrorException {
            String[] testDataArr = {
                    "true || true",
            };
            ExpressionOrHelper.parseAcceptTestList(testDataArr);
        }

        @Test
        public void falseTest() throws FatalErrorException {
            TestDataAndErrMessage[] arr = {
                new TestDataAndErrMessage("true ||","||の後ろはconditionTermです"),
            };
            ExpressionOrHelper.parseRejectTestList(arr);
        }
    }

    //省略 conditionTerm   ::= conditionFactor { termAnd }

    public static class TermAndTest {
        ParseTestHelper2<ConditionFactor,TermAnd> TermAndHelper = new ParseTestHelper2<ConditionFactor,TermAnd>(ConditionFactor.class,TermAnd.class);
        
        //termAnd         ::= AND conditionFactor
        @Test
        public void trueTest() throws FatalErrorException {
            String[] testDataArr = {
                    "true && true",
            };
            TermAndHelper.parseAcceptTestList(testDataArr);
        }

        @Test
        public void falseTest() throws FatalErrorException {
            TestDataAndErrMessage[] arr = {
                new TestDataAndErrMessage("true &&","&&の後ろはconditionFactorです"),
            };
            TermAndHelper.parseRejectTestList(arr);
        }
    }

    //省略 conditionFactor ::= notFactor | conditionUnsignedFactor

    public static class NotFactorTest {
        ParseTestHelper<NotFactor> NotFactorHelper = new ParseTestHelper<NotFactor>(NotFactor.class);

        //notFactor       ::= NOT conditionUnsignedFactor
        @Test
        public void trueTest() throws FatalErrorException {
            String[] testDataArr = {
                    "! true",
                    "![i_a<=10]"
            };
            NotFactorHelper.parseAcceptTestList(testDataArr);
        }

        @Test
        public void falseTest() throws FatalErrorException {
            TestDataAndErrMessage[] arr = {
                new TestDataAndErrMessage("!###","!の後ろはConditionUnsignedFactorです"),
            };
            NotFactorHelper.parseRejectTestList(arr);
        }
    }

    public static class ConditionUnsignedFactorTest {
        ParseTestHelper<ConditionUnsignedFactor> ConditionUnsignedFactorHelper = new ParseTestHelper<ConditionUnsignedFactor>(ConditionUnsignedFactor.class);

        // condition | LBRA conditionExpression RBRA
        @Test
        public void trueTest() throws FatalErrorException {
            String[] testDataArr = {
                "10<i_c",    
                "[10<i_c]",
                "10<i_c]", // ProgramTest(プログラムにゴミ有)
            };
            ConditionUnsignedFactorHelper.parseAcceptTestList(testDataArr);
        }

        @Test
        public void falseTest() throws FatalErrorException {
            TestDataAndErrMessage[] arr = {
                new TestDataAndErrMessage("[", "[の後ろはconditionExpressionです"), //中身が無い
                new TestDataAndErrMessage("[10<=i_a", "]がありません"), //]が無い
            };
            ConditionUnsignedFactorHelper.parseRejectTestList(arr);
        }
    }

}