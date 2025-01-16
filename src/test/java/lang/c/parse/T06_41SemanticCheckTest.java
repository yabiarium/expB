package lang.c.parse;

import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

import lang.FatalErrorException;
import lang.c.CType;
import lang.c.testhelpter.SemanticCheckTestHelper;
// import lang.c.testhelpter.TestDataAndCType;
import lang.c.testhelpter.TestDataAndCTypeAndConstant;
import lang.c.testhelpter.TestDataAndErrMessage;

@RunWith(Enclosed.class)
public class T06_41SemanticCheckTest {

    public static class ConditionTest {
        SemanticCheckTestHelper<Condition> conditionHelper = new SemanticCheckTestHelper<Condition>(Condition.class);

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
            conditionHelper.acceptListTest(testDataArr);
        }

        @Test
        public void typeTest() throws FatalErrorException {
            TestDataAndCTypeAndConstant[] ttList = {
                new TestDataAndCTypeAndConstant("true", CType.T_bool, true),
                new TestDataAndCTypeAndConstant("false", CType.T_bool, true),
                new TestDataAndCTypeAndConstant("10<i_a", CType.T_bool, true),
                new TestDataAndCTypeAndConstant("i_c>=ia_f[3]", CType.T_bool, true),
            };
            conditionHelper.typeAndConstantListTest(ttList);
        }

        @Test
        public void falseTest() throws FatalErrorException {
            TestDataAndErrMessage[] teList = {
                // new TestDataAndErrMessage("1 == &2", "左辺の型[int]と右辺の型[int*]が一致しないので比較できません"),  // 不当：両辺の方が異なる．
                // new TestDataAndErrMessage("1 < &2", "左辺の型[int]と右辺の型[int*]が一致しないので比較できません"),  // 不当：両辺の方が異なる．
                // new TestDataAndErrMessage("ip_a < 200", "左辺の型[int*]と右辺の型[int]が一致しないので比較できません"),  // 不当：両辺の方が異なる．
                // new TestDataAndErrMessage("*ipa_g[2] == ip_d", "左辺の型[int]と右辺の型[int*]が一致しないので比較できません"),  // 不当：両辺の方が異なる．
            }; 
            conditionHelper.rejectListTest(teList);
        }
    }

}
