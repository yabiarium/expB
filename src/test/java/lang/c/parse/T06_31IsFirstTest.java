package lang.c.parse;

import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

import lang.FatalErrorException;
import lang.c.testhelpter.IsFirstTestHelper;

@RunWith(Enclosed.class)
public class T06_31IsFirstTest {

    public static class ConditionLTTest {
        IsFirstTestHelper<ConditionLT> ConditionLTHelper = new IsFirstTestHelper<ConditionLT>(ConditionLT.class);

        @Test
        public void trueTest() throws FatalErrorException {
            String[] testDataArr = {"<i_a",};
            ConditionLTHelper.trueListTest(testDataArr);
        }

        @Test
        public void falseTest() throws FatalErrorException{
            String[] testDataArr = {"<=i_b",
                                    ">*ip_d",
                                    ">=ia_f[3]",
                                    "==*ipa_g[0]",
                                    "!=100" };
            ConditionLTHelper.falseListTest(testDataArr);
        }
    }

    public static class ConditionLETest {
        IsFirstTestHelper<ConditionLE> ConditionLEHelper = new IsFirstTestHelper<ConditionLE>(ConditionLE.class);

        @Test
        public void trueTest() throws FatalErrorException {
            String[] testDataArr = {"<=i_b",};
            ConditionLEHelper.trueListTest(testDataArr);
        }

        @Test
        public void falseTest() throws FatalErrorException{
            String[] testDataArr = {"<i_a",

                                    ">*ip_d",
                                    ">=ia_f[3]",
                                    "==*ipa_g[0]",
                                    "!=100" };
            ConditionLEHelper.falseListTest(testDataArr);
        }
    }

    public static class ConditionGTTest {
        IsFirstTestHelper<ConditionGT> ConditionGTHelper = new IsFirstTestHelper<ConditionGT>(ConditionGT.class);

        @Test
        public void trueTest() throws FatalErrorException {
            String[] testDataArr = {">*ip_d",};
            ConditionGTHelper.trueListTest(testDataArr);
        }

        @Test
        public void falseTest() throws FatalErrorException{
            String[] testDataArr = {"<i_a",
                                    "<=i_b",
                                    
                                    ">=ia_f[3]",
                                    "==*ipa_g[0]",
                                    "!=100" };
            ConditionGTHelper.falseListTest(testDataArr);
        }
    }

    public static class ConditionGETest {
        IsFirstTestHelper<ConditionGE> ConditionGEHelper = new IsFirstTestHelper<ConditionGE>(ConditionGE.class);

        @Test
        public void trueTest() throws FatalErrorException {
            String[] testDataArr = {">=ia_f[3]",};
            ConditionGEHelper.trueListTest(testDataArr);
        }

        @Test
        public void falseTest() throws FatalErrorException{
            String[] testDataArr = {"<i_a",
                                    "<=i_b",
                                    ">*ip_d",
                                    
                                    "==*ipa_g[0]",
                                    "!=100" };
            ConditionGEHelper.falseListTest(testDataArr);
        }
    }

    public static class ConditionEQTest {
        IsFirstTestHelper<ConditionEQ> ConditionEQHelper = new IsFirstTestHelper<ConditionEQ>(ConditionEQ.class);

        @Test
        public void trueTest() throws FatalErrorException {
            String[] testDataArr = {"==*ipa_g[0]",};
            ConditionEQHelper.trueListTest(testDataArr);
        }

        @Test
        public void falseTest() throws FatalErrorException{
            String[] testDataArr = {"<i_a",
                                    "<=i_b",
                                    ">*ip_d",
                                    ">=ia_f[3]",
                                    
                                    "!=100" };
            ConditionEQHelper.falseListTest(testDataArr);
        }
    }

    public static class ConditionNETest {
        IsFirstTestHelper<ConditionNE> ConditionNEHelper = new IsFirstTestHelper<ConditionNE>(ConditionNE.class);

        @Test
        public void trueTest() throws FatalErrorException {
            String[] testDataArr = {"!=100" };
            ConditionNEHelper.trueListTest(testDataArr);
        }

        @Test
        public void falseTest() throws FatalErrorException{
            String[] testDataArr = {"<i_a",
                                    "<=i_b",
                                    ">*ip_d",
                                    ">=ia_f[3]",
                                    "==*ipa_g[0]",
                                    };
            ConditionNEHelper.falseListTest(testDataArr);
        }
    }

}
