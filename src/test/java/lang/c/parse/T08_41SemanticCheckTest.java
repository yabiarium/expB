package lang.c.parse;

import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

import lang.FatalErrorException;
import lang.c.CType;
import lang.c.testhelpter.SemanticCheckTestHelper;
import lang.c.testhelpter.TestDataAndCType;
import lang.c.testhelpter.TestDataAndCTypeAndConstant;
import lang.c.testhelpter.TestDataAndErrMessage;

@RunWith(Enclosed.class)
public class T08_41SemanticCheckTest {

    public static class NotFactorTest {
        SemanticCheckTestHelper<NotFactor> Helper = new SemanticCheckTestHelper<NotFactor>(NotFactor.class);

        @Test
        public void trueTest() throws FatalErrorException {
            String[] testDataArr = {
                "!true",
                "!false",
            };
            Helper.acceptListTest(testDataArr);
        }

        @Test
        public void typeTest() throws FatalErrorException {
            TestDataAndCTypeAndConstant[] ttList = {
                new TestDataAndCTypeAndConstant("!true", CType.T_bool, true),
                new TestDataAndCTypeAndConstant("!false", CType.T_bool, true),
                new TestDataAndCTypeAndConstant("!10<i_a", CType.T_bool, true),
                new TestDataAndCTypeAndConstant("!i_c>=ia_f[3]", CType.T_bool, true),
            };
            Helper.typeAndConstantListTest(ttList);
        }

        @Test
        public void falseTest() throws FatalErrorException {
            TestDataAndErrMessage[] teList = {
                // notFactorの意味チェックではなく、conditionのparse()で判定するようにした
                new TestDataAndErrMessage("!1", "expressionの後ろにはconditionXXが必要です"),
            }; 
            Helper.rejectListTest(teList);
        }
    }

    public static class ConditionTermTest {
        SemanticCheckTestHelper<ConditionTerm> Helper = new SemanticCheckTestHelper<ConditionTerm>(ConditionTerm.class);

        @Test
        public void trueTest() throws FatalErrorException {
            String[] testDataArr = {
                "true && false",
                "i_a==10 || true",
            };
            Helper.acceptListTest(testDataArr);
        }

        @Test
        public void typeList() throws FatalErrorException {
            TestDataAndCType[] ttList = {
                //bool型どうしがbool型になることを確認
                new TestDataAndCType("i_a==10 && true", CType.T_bool),
                new TestDataAndCType("true && false", CType.T_bool),
                new TestDataAndCType("true || false", CType.T_bool),
            };
            Helper.typeListTest(ttList);
        }

        @Test
        public void falseTest() throws FatalErrorException {
            TestDataAndErrMessage[] teList = {
                //bool型以外はconditionのparse()で排されるようになっている
                //(NotFactor, TermAnd, ExpressionOrでの意味チェックエラーは正常にparse()が実行されていれば発生しない)
                new TestDataAndErrMessage("i_a+10 && true", "expressionの後ろにはconditionXXが必要です"),
            }; 
            Helper.rejectListTest(teList);
        }
    }

}
