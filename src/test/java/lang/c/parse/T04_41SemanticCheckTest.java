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
public class T04_41SemanticCheckTest {
    public static class IdentTest {
        SemanticCheckTestHelper<Ident> identHelper = new SemanticCheckTestHelper<Ident>(Ident.class);
        // リストを渡す typeテスト
        @Test
        public void typeAndConstant() throws FatalErrorException {
            TestDataAndCTypeAndConstant[] ttList = {
                new TestDataAndCTypeAndConstant("i_ABC", CType.T_int, false),
                new TestDataAndCTypeAndConstant("ip_ABC", CType.T_pint, false),
                new TestDataAndCTypeAndConstant("ia_ABC", CType.T_int_array, false),
                new TestDataAndCTypeAndConstant("ipa_ABC", CType.T_pint_array, false),
                new TestDataAndCTypeAndConstant("c_ABC", CType.T_int, true),
            };
            identHelper.typeAndConstantListTest(ttList);
        }

        @Test
        public void trueTest() throws FatalErrorException {
            String[] testDataArr = {
                "i_A",     // 正当
                "ip_A",     // 正当
                "ia_A",     // 正当
                "ipa_A",     // 正当
            };
            identHelper.acceptListTest(testDataArr);
        }

        @Test
        public void falseTest() throws FatalErrorException {
            TestDataAndErrMessage[] teList = {
                new TestDataAndErrMessage("aaa", "変数名規則にマッチしません"),
            }; 
            identHelper.rejectListTest(teList);
        }
    }

}
