package lang.c.parse;

import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

import lang.FatalErrorException;
import lang.c.testhelpter.ParseTestHelper;
import lang.c.testhelpter.ParseTestHelper2;
import lang.c.testhelpter.TestDataAndErrMessage;

@RunWith(Enclosed.class)
public class T06_32ParseTest {
    
    public static class ConditionLTTest {
        ParseTestHelper2<Expression,ConditionLT> ConditionLTHelper = new ParseTestHelper2<Expression,ConditionLT>(Expression.class,ConditionLT.class);

        @Test
        public void trueTest() throws FatalErrorException {
            String[] testDataArr = {
                    "10<i_c",
            };
            ConditionLTHelper.parseAcceptTestList(testDataArr);
        }

        @Test
        public void falseTest() throws FatalErrorException {
            TestDataAndErrMessage[] arr = {
                new TestDataAndErrMessage("10<###", "<の後ろはexpressionです"),
            };
            ConditionLTHelper.parseRejectTestList(arr);
        }
    }

    public static class ConditionLETest {
        ParseTestHelper2<Expression,ConditionLE> ConditionLEHelper = new ParseTestHelper2<Expression,ConditionLE>(Expression.class,ConditionLE.class);

        @Test
        public void trueTest() throws FatalErrorException {
            String[] testDataArr = {
                    "10<=i_c"
            };
            ConditionLEHelper.parseAcceptTestList(testDataArr);
        }

        @Test
        public void falseTest() throws FatalErrorException {
            TestDataAndErrMessage[] arr = {
                new TestDataAndErrMessage("10<=###", "<=の後ろはexpressionです"),
            };
            ConditionLEHelper.parseRejectTestList(arr);
        }
    }

    public static class ConditionGTTest {
        ParseTestHelper2<Expression,ConditionGT> ConditionGTHelper = new ParseTestHelper2<Expression,ConditionGT>(Expression.class,ConditionGT.class);

        @Test
        public void trueTest() throws FatalErrorException {
            String[] testDataArr = {
                    "10>i_c"
            };
            ConditionGTHelper.parseAcceptTestList(testDataArr);
        }

        @Test
        public void falseTest() throws FatalErrorException {
            TestDataAndErrMessage[] arr = {
                new TestDataAndErrMessage("10>###", ">の後ろはexpressionです"),
            };
            ConditionGTHelper.parseRejectTestList(arr);
        }
    }

    public static class ConditionGETest {
        ParseTestHelper2<Expression,ConditionGE> ConditionGEHelper = new ParseTestHelper2<Expression,ConditionGE>(Expression.class,ConditionGE.class);

        @Test
        public void trueTest() throws FatalErrorException {
            String[] testDataArr = {
                    "10>=i_c"
            };
            ConditionGEHelper.parseAcceptTestList(testDataArr);
        }

        @Test
        public void falseTest() throws FatalErrorException {
            TestDataAndErrMessage[] arr = {
                new TestDataAndErrMessage("10>=###", ">=の後ろはexpressionです"),
            };
            ConditionGEHelper.parseRejectTestList(arr);
        }
    }

    public static class ConditionEQTest {
        ParseTestHelper2<Expression,ConditionEQ> ConditionEQHelper = new ParseTestHelper2<Expression,ConditionEQ>(Expression.class,ConditionEQ.class);

        @Test
        public void trueTest() throws FatalErrorException {
            String[] testDataArr = {
                    "10==i_c"
            };
            ConditionEQHelper.parseAcceptTestList(testDataArr);
        }

        @Test
        public void falseTest() throws FatalErrorException {
            TestDataAndErrMessage[] arr = {
                new TestDataAndErrMessage("10==###", "==の後ろはexpressionです"),
            };
            ConditionEQHelper.parseRejectTestList(arr);
        }
    }

    public static class ConditionNETest {
        ParseTestHelper2<Expression,ConditionNE> ConditionNEHelper = new ParseTestHelper2<Expression,ConditionNE>(Expression.class,ConditionNE.class);

        @Test
        public void trueTest() throws FatalErrorException {
            String[] testDataArr = {
                    "10!=i_c"
            };
            ConditionNEHelper.parseAcceptTestList(testDataArr);
        }

        @Test
        public void falseTest() throws FatalErrorException {
            TestDataAndErrMessage[] arr = {
                new TestDataAndErrMessage("10!=###", "!=の後ろはexpressionです"),
            };
            ConditionNEHelper.parseRejectTestList(arr);
        }
    }

    public static class ConditionTest {
        //ParseTestHelper2<Expression,Condition> ConditionHelper = new ParseTestHelper2<Expression,Condition>(Expression.class,Condition.class);
        ParseTestHelper<Condition> ConditionHelper = new ParseTestHelper<Condition>(Condition.class);

        @Test
        public void trueTest() throws FatalErrorException {
            String[] testDataArr = {
                    "true",
                    "false",
                    "10<i_a",
                    "ia_f[0]<=i_b",
                    "i_b>*ip_d",
                    "i_c>=ia_f[3]",
                    "*ip_e==*ipa_g[0]",
                    "200!=100"
            };
            ConditionHelper.parseAcceptTestList(testDataArr);
        }

        @Test
        public void falseTest() throws FatalErrorException {
            TestDataAndErrMessage[] arr = {
                new TestDataAndErrMessage("10<","<の後ろはexpressionです"),
                new TestDataAndErrMessage("ia_f[0]<=###","<=の後ろはexpressionです"),
                new TestDataAndErrMessage("i_b>//",">の後ろはexpressionです"),
                new TestDataAndErrMessage("i_c>=|||",">=の後ろはexpressionです"),
                new TestDataAndErrMessage("*ip_e==%%%","==の後ろはexpressionです"),
                new TestDataAndErrMessage("200!=","!=の後ろはexpressionです"),
            };
            ConditionHelper.parseRejectTestList(arr);
        }
    }

}