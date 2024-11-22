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

    public static class VariableTest {
        SemanticCheckTestHelper<Variable> variableHelper = new SemanticCheckTestHelper<Variable>(Variable.class);
        // リストを渡す typeテスト
        @Test
        public void typeAndConstant() throws FatalErrorException {
            TestDataAndCTypeAndConstant[] ttList = {
                new TestDataAndCTypeAndConstant("ia_ABC[123]", CType.T_int, false),
                new TestDataAndCTypeAndConstant("ipa_ABC[123]", CType.T_pint, false),
            };
            variableHelper.typeAndConstantListTest(ttList);
        }
    }

    public static class PrimaryTest {
        SemanticCheckTestHelper<Primary> primaryHelper = new SemanticCheckTestHelper<Primary>(Primary.class);
        // リストを渡す typeテスト
        @Test
        public void typeAndConstant() throws FatalErrorException {
            TestDataAndCTypeAndConstant[] ttList = {
                new TestDataAndCTypeAndConstant("*ip_ABC", CType.T_int, false),
                new TestDataAndCTypeAndConstant("*ipa_ABC[123]", CType.T_int, false),
            };
            primaryHelper.typeAndConstantListTest(ttList);
        }
    }

    public static class ExpressionTest {
        SemanticCheckTestHelper<Expression> expressionHelper = new SemanticCheckTestHelper<Expression>(Expression.class);

        @Test
        public void intFalseTest() throws FatalErrorException{
            TestDataAndErrMessage[] teList = {
                new TestDataAndErrMessage("*i_a", "*の後ろは[int*]です"), // エラーメッセージを自分の実装でのものに変更
                new TestDataAndErrMessage("i_a[3]", "配列変数は T_int_array か T_pint_array です"), // エラーメッセージを自分の実装でのものに変更
                new TestDataAndErrMessage("&10+&i_a", "左辺の型[int*]と右辺の型[int*]は足せません"), // ここはcv00の時点で(エラーの出力の枠組みが)実装済みなのでそのまま，，のはず
                new TestDataAndErrMessage("10-&i_a", "左辺の型[int]から右辺の型[int*]は引けません"), // エラーメッセージを自分の実装でのものに変更
            }; 
            expressionHelper.rejectListTest(teList);
        }

        @Test
        public void pointerFalseTest() throws FatalErrorException{
            TestDataAndErrMessage[] teList = {
                // (2) ポインタ型の扱い
                new TestDataAndErrMessage("-ip_d", "-の後ろはT_intです[int*]"), // 不当（ポインタに負号をつけるって、どゆこと？）
                new TestDataAndErrMessage("ip_d[3]", "配列変数は T_int_array か T_pint_array です"), // 不当（Ｃでは正当だが、この実験では不当にすること）
                new TestDataAndErrMessage("&ip_e", "&の後ろはT_intです[int*]"), // 不当（ポインタのポインタは許していない）
                new TestDataAndErrMessage("10 - ip_d", "左辺の型[int]から右辺の型[int*]は引けません"), // 不当 (int - pint)
                new TestDataAndErrMessage("*ip_e - &10", "左辺の型[int]から右辺の型[int*]は引けません"), // 不当 (int - pint)
            }; 
            expressionHelper.rejectListTest(teList);
        }

        @Test
        public void intArrayFalseTest() throws FatalErrorException{
            TestDataAndErrMessage[] teList = {
                // (3) 配列型の扱い
                new TestDataAndErrMessage("ia_f", "配列型の後ろに[]がありません"), // 不当
                new TestDataAndErrMessage("*ia_f", "配列型の後ろに[]がありません"), // 不当（Ｃでは正当だが、この実験では不当にすること）
                new TestDataAndErrMessage("ia_f[3] - &1", "左辺の型[int]から右辺の型[int*]は引けません"), // 不当 (int - pint)
                new TestDataAndErrMessage("1 - &ia_f[3]", "左辺の型[int]から右辺の型[int*]は引けません"), // 不当 (int - pint)
            }; 
            expressionHelper.rejectListTest(teList);
        }

        @Test
        public void pointerArrayFalseTest() throws FatalErrorException{
            TestDataAndErrMessage[] teList = {
                // (4) ポインタ配列型の扱い
                new TestDataAndErrMessage("ipa_g", "配列型の後ろに[]がありません"), // 不当
                new TestDataAndErrMessage("*ipa_g", "配列型の後ろに[]がありません"), // 不当（Ｃでは正当だが、この実験では不当にすること）
                new TestDataAndErrMessage("ipa_g[3] + ipa_g[1]", "左辺の型[int*]と右辺の型[int*]は足せません"), // 不当（pint + pint)
                new TestDataAndErrMessage("*ipa_g[2] - &100", "左辺の型[int]から右辺の型[int*]は引けません"), // 不当（int - pint)
            }; 
            expressionHelper.rejectListTest(teList);
        }
    }

}
