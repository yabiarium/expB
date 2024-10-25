package lang.c.parse;

import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

import lang.FatalErrorException;
import lang.c.testhelpter.IsFirstTestHelper;

@RunWith(Enclosed.class)
public class T03_31IsFirstTest {

    public static class TermMultTest {
        IsFirstTestHelper<TermMult> termMultHelper = new IsFirstTestHelper<TermMult>(TermMult.class);

        @Test
        public void trueTest() throws FatalErrorException {
            String[] testDataArr = { "*100" };
            termMultHelper.trueListTest(testDataArr);
        }

        @Test
        public void falseTest() throws FatalErrorException {
            String[] testDataArr = { "/100" };
            termMultHelper.falseListTest(testDataArr);
        }
    }

    public static class TermDivTest {
        IsFirstTestHelper<TermDiv> termDivHelper = new IsFirstTestHelper<TermDiv>(TermDiv.class);

        @Test
        public void trueTest() throws FatalErrorException {
            String[] testDataArr = { "/100" };
            termDivHelper.trueListTest(testDataArr);
        }

        @Test
        public void falseTest() throws FatalErrorException {
            String[] testDataArr = { "+100" };
            termDivHelper.falseListTest(testDataArr);
        }
    }

    public static class PlusFactorTest {
        IsFirstTestHelper<PlusFactor> plusFactorHelper = new IsFirstTestHelper<PlusFactor>(PlusFactor.class);

        @Test
        public void trueTest() throws FatalErrorException {
            String[] testDataArr = { "+100" };
            plusFactorHelper.trueListTest(testDataArr);
        }

        @Test
        public void falseTest() throws FatalErrorException {
            String[] testDataArr = { "*100" };
            plusFactorHelper.falseListTest(testDataArr);
        }
    }

    public static class MinusFactorTest {
        IsFirstTestHelper<MinusFactor> minusFactorHelper = new IsFirstTestHelper<MinusFactor>(MinusFactor.class);

        @Test
        public void trueTest() throws FatalErrorException {
            String[] testDataArr = { "-100" };
            minusFactorHelper.trueListTest(testDataArr);
        }

        @Test
        public void falseTest() throws FatalErrorException {
            String[] testDataArr = { "/100" };
            minusFactorHelper.falseListTest(testDataArr);
        }
    }

    public static class UnsignedFactorTest {
        IsFirstTestHelper<UnsignedFactor> uFactorHelper = new IsFirstTestHelper<UnsignedFactor>(UnsignedFactor.class);

        @Test
        public void trueTest() throws FatalErrorException {
            String[] testDataArr = {    "&100", "200", "(100+200)"};
            uFactorHelper.trueListTest(testDataArr);
        }

        @Test
        public void falseTest() throws FatalErrorException {
            String[] testDataArr = { "-100", "*200" };
            uFactorHelper.falseListTest(testDataArr);
        }
    }
}
