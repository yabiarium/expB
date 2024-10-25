package lang.c.parse;

import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

import lang.FatalErrorException;
import lang.c.testhelpter.IsFirstTestHelper;

@RunWith(Enclosed.class)
public class T01_31IsFirstTest {

    public static class ExpressionSubTest {
        IsFirstTestHelper<ExpressionSub> expressionSubHelper = new IsFirstTestHelper<ExpressionSub>(
                ExpressionSub.class);
        
        // 個別正当例
        @Test
        public void accept() throws FatalErrorException {
            expressionSubHelper.trueTest("-4");
            expressionSubHelper.falseTest("+2");
            expressionSubHelper.falseTest("2");
        }

        @Test
        public void falseTest() throws FatalErrorException {
            String[] testDataArr = { "+1", "2" };
            expressionSubHelper.falseListTest(testDataArr);
        }
        
        @Test
        public void expressionSub() throws FatalErrorException {
            String[] testDataArr = { "-4" };
            expressionSubHelper.trueListTest(testDataArr);
        }
    }

}
