package lang.c.parse;

import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

import lang.FatalErrorException;
import lang.c.testhelpter.IsFirstTestHelper;

@RunWith(Enclosed.class)
public class T04_31IsFirstTest {

    public static class UnsignedFactorTest {
        IsFirstTestHelper<UnsignedFactor> UnsignedFactorHelper = new IsFirstTestHelper<UnsignedFactor>(UnsignedFactor.class);

        @Test
        public void trueTest() throws FatalErrorException {
            String[] testDataArr = { "&100", "200", "(100+200)", "i_a", "*200", "*ip_d", "&i_a" };
            UnsignedFactorHelper.trueListTest(testDataArr);
        }
    }

    public static class PrimaryMultTest {
        IsFirstTestHelper<PrimaryMult> primaryMultHelper = new IsFirstTestHelper<PrimaryMult>(PrimaryMult.class);

        @Test
        public void trueTest() throws FatalErrorException {
            String[] testDataArr = { "*ip_e" };
            primaryMultHelper.trueListTest(testDataArr);
        }

        @Test
        public void falseTest() throws FatalErrorException {
            String[] testDataArr = { "+100" };
            primaryMultHelper.falseListTest(testDataArr);
        }
    }

    public static class VariableTest {
        IsFirstTestHelper<Variable> variableHelper = new IsFirstTestHelper<Variable>(Variable.class);

        @Test
        public void trueTest() throws FatalErrorException {
            String[] testDataArr = { "i_a", "ip_d", "ia_f[100]", "ipa_g[200]" };
            variableHelper.trueListTest(testDataArr);
        }

        @Test
        public void falseTest() throws FatalErrorException {
            String[] testDataArr = { "*100", "&i_a" };
            variableHelper.falseListTest(testDataArr);
        }
    }

    public static class ArrayTest {
        IsFirstTestHelper<Array> arrayHelper = new IsFirstTestHelper<Array>(Array.class);

        @Test
        public void trueTest() throws FatalErrorException {
            String[] testDataArr = { "[100]", "[*ip_d]" };
            arrayHelper.trueListTest(testDataArr);
        }

        @Test
        public void falseTest() throws FatalErrorException {
            String[] testDataArr = { "100" };
            arrayHelper.falseListTest(testDataArr);
        }
    }

    public static class IdentTest {
        IsFirstTestHelper<Ident> identHelper = new IsFirstTestHelper<Ident>(Ident.class);

        @Test
        public void trueTest() throws FatalErrorException {
            String[] testDataArr = { "i_b", "ip_e", "c_h", "ia_f[30]", "ipa_g[9]" };
            identHelper.trueListTest(testDataArr);
        }

        @Test
        public void falseTest() throws FatalErrorException {
            String[] testDataArr = { "100", "+", "[]", "()" };
            identHelper.falseListTest(testDataArr);
        }
    }
}