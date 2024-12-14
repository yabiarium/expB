package lang.c.parse;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

import lang.FatalErrorException;
import lang.c.testhelpter.IsFirstTestHelper;

@RunWith(Enclosed.class)
public class T08_31IsFirstTest {

    // Conditionか、[Condition]だけがisFirstがtrueとなる
    public static class ConditionUnsignedFactorTest {
        IsFirstTestHelper<ConditionUnsignedFactor> ConditionUnsignedFactor = new IsFirstTestHelper<ConditionUnsignedFactor>(ConditionUnsignedFactor.class);

        @Test
        public void accept() throws FatalErrorException {
            String[] testDataArr = { "[", "i_a==1", "()" };
            ConditionUnsignedFactor.trueListTest(testDataArr);
        }

        @Test
        public void reject() throws FatalErrorException {
            String[] testDataArr = { "!", "&&", "||", "@" };
            ConditionUnsignedFactor.falseListTest(testDataArr);
        }
    }

    public static class NotFactorTest {
        IsFirstTestHelper<NotFactor> NotFactorHelper = new IsFirstTestHelper<NotFactor>(NotFactor.class);

        // !Condition のみ
        @Test
        public void accept() throws FatalErrorException {
            String[] testDataArr = { "!", "!i_a", "![]" };
            NotFactorHelper.trueListTest(testDataArr);
        }

        @Test
        public void reject() throws FatalErrorException {
            String[] testDataArr = { "&&", "||", "@", "[", "i_a==1", "()" };
            NotFactorHelper.falseListTest(testDataArr);
        }
    }

    public static class ConditionFactorTest {
        IsFirstTestHelper<ConditionFactor> ConditionFactorHelper = new IsFirstTestHelper<ConditionFactor>(ConditionFactor.class);

        // !Condition, [Condition], [!Condition], Condition
        @Test
        public void accept() throws FatalErrorException {
            String[] testDataArr = { "!", "[!i_a", "!&&" };
            ConditionFactorHelper.trueListTest(testDataArr);
        }

        // &&, ||, その他
        @Test
        public void reject() throws FatalErrorException {
            String[] testDataArr = { "&&", "||", "@" };
            ConditionFactorHelper.falseListTest(testDataArr);
        }
    }

    public static class TermAndTest {
        IsFirstTestHelper<TermAnd> TermAndHelper = new IsFirstTestHelper<TermAnd>(TermAnd.class);

        // &&
        @Test
        public void accept() throws FatalErrorException {
            String[] testDataArr = { "&&" };
            TermAndHelper.trueListTest(testDataArr);
        }

        // !, [], ||, Condition, その他
        @Test
        public void reject() throws FatalErrorException {
            String[] testDataArr = { "!", "[]", "||", "()", "@" };
            TermAndHelper.falseListTest(testDataArr);
        }
    }

    public static class ConditionTermTest {
        IsFirstTestHelper<ConditionTerm> ConditionTermHelper = new IsFirstTestHelper<ConditionTerm>(ConditionTerm.class);

        // !Condition, [Condition], [!Condition], Condition
        @Test
        public void accept() throws FatalErrorException {
            String[] testDataArr = { "!", "[" };
            ConditionTermHelper.trueListTest(testDataArr);
        }

        // &&, ||, その他
        @Test
        public void reject() throws FatalErrorException {
            String[] testDataArr = { "&&", "||", "@" };
            ConditionTermHelper.falseListTest(testDataArr);
        }
    }

    public static class ExpressionOrTest {
        IsFirstTestHelper<ExpressionOr> ExpressionOrHelper = new IsFirstTestHelper<ExpressionOr>(ExpressionOr.class);

        // ||
        @Test
        public void accept() throws FatalErrorException {
            String[] testDataArr = { "||" };
            ExpressionOrHelper.trueListTest(testDataArr);
        }

        @Test
        public void reject() throws FatalErrorException {
            String[] testDataArr = {  "!", "[]", "&&", "()", "@" };
            ExpressionOrHelper.falseListTest(testDataArr);
        }
    }
    
    public static class ConditionExpressionTest {
        IsFirstTestHelper<ConditionExpression> ConditionExpressionHelper = new IsFirstTestHelper<ConditionExpression>(ConditionExpression.class);

        // !Condition, [Condition], [!Condition], Condition
        @Test
        public void accept() throws FatalErrorException {
            String[] testDataArr = { "!", "[" };
            ConditionExpressionHelper.trueListTest(testDataArr);
        }

        // &&, ||, その他
        @Test
        public void reject() throws FatalErrorException {
            String[] testDataArr = { "&&", "||", "@" };
            ConditionExpressionHelper.falseListTest(testDataArr);
        }
    }

    public static class ConditionBlockTest {
        IsFirstTestHelper<ConditionBlock> ConditionBlockHelper = new IsFirstTestHelper<ConditionBlock>(ConditionBlock.class);

        // ()
        @Test
        public void accept() throws FatalErrorException {
            String[] testDataArr = { "()" };
            ConditionBlockHelper.trueListTest(testDataArr);
        }

        @Test
        public void reject() throws FatalErrorException {
            String[] testDataArr = { "!", "[", "&&", "||", "@" };
            ConditionBlockHelper.falseListTest(testDataArr);
        }
    }

}
