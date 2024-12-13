package lang.c.parse;

import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

import lang.FatalErrorException;
import lang.c.testhelpter.IsFirstTestHelper;

@RunWith(Enclosed.class)
public class T07_31IsFirstTest {

    public static class StatementIfTest {
        IsFirstTestHelper<StatementIf> StatementIfHelper = new IsFirstTestHelper<StatementIf>(StatementIf.class);

        @Test
        public void trueTest() throws FatalErrorException {
            String[] testDataArr = {"if",};        // StatementIf.isFirst() のテスト
            StatementIfHelper.trueListTest(testDataArr);
        }
    }

    public static class StatementWhileTest {
        IsFirstTestHelper<StatementWhile> StatementWhileHelper = new IsFirstTestHelper<StatementWhile>(StatementWhile.class);

        @Test
        public void trueTest() throws FatalErrorException {
            String[] testDataArr = {"while",};
            StatementWhileHelper.trueListTest(testDataArr);
        }
    }

    public static class StatementTest {
        IsFirstTestHelper<Statement> StatementHelper = new IsFirstTestHelper<Statement>(Statement.class);

        @Test
        public void trueTest() throws FatalErrorException {
            String[] testDataArr = {"if",
                                    "while",
                                    "{ input i_variable; }"};
            StatementHelper.trueListTest(testDataArr);
        }
    }

    public static class StatementBlockTest {
        IsFirstTestHelper<StatementBlock> statementBlockHelper = new IsFirstTestHelper<StatementBlock>(StatementBlock.class);

        @Test
        public void trueTest() throws FatalErrorException {
            String[] testDataArr = { 
                "{ input i_variable; }",
            };
            statementBlockHelper.trueListTest(testDataArr);
        }
    }

    public static class ConditionBlockTest {
        IsFirstTestHelper<ConditionBlock> conditionBlockHelper = new IsFirstTestHelper<ConditionBlock>(ConditionBlock.class);

        @Test
        public void trueTest() throws FatalErrorException {
            String[] testDataArr = { 
                "( i_a==1 )",
            };
            conditionBlockHelper.trueListTest(testDataArr);
        }
    }

}
