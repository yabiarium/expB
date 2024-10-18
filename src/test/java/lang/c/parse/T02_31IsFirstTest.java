package lang.c.parse;

import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

import lang.FatalErrorException;
import lang.c.testhelpter.IsFirstTestHelper;

@RunWith(Enclosed.class)
public class T02_31IsFirstTest {

    public static class FactorAmpTest {
        // Test that each class's isFirst() is valid
        // Distant future, you should add necessary test cases to each Test code.

        IsFirstTestHelper<FactorAmp> numberHelper = new IsFirstTestHelper<FactorAmp>(FactorAmp.class);

        // 個別正当例
        @Test
        public void accept() throws FatalErrorException {
            numberHelper.trueTest("&100");
            numberHelper.trueTest("&");
        }

        // リスト不当例
        @Test
        public void rejectList() throws FatalErrorException {
            String[] testDataArr = { "-100", "20" };
            numberHelper.falseListTest(testDataArr);
        }
    }
}
