package lang.c.parse;

import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

import lang.FatalErrorException;
// import lang.c.CType;
import lang.c.testhelpter.SemanticCheckTestHelper;
// import lang.c.testhelpter.TestDataAndCType;
// import lang.c.testhelpter.TestDataAndCTypeAndConstant;
import lang.c.testhelpter.TestDataAndErrMessage;

@RunWith(Enclosed.class)
public class T05_41SemanticCheckTest {

    public static class StatementAssignTest {
        SemanticCheckTestHelper<StatementAssign> StatementAssignHelper = new SemanticCheckTestHelper<StatementAssign>(StatementAssign.class);

        @Test
        public void trueTest() throws FatalErrorException {
            String[] testDataArr = {
                // (1) 整数型の扱い
                "i_A=i_B;",     // 正当
                // (2) ポインタ型の扱い
                "ip_A=ip_B;",    // 正当
                "*ip_A=i_A;",    // 正当
                "ip_A=&i_A;",     // 正当
                // (3) 配列型の扱い
                "ia_A[10]=i_B;",     // 正当
                "ip_A=&ia_B[10];",     // 正当
                // (4) ポインタ配列型の扱い
                "ipa_A[100]=&10;",    // 正当
                "ipa_B[200]=ip_C;",    // 正当
                "*ipa_A[10]=10;",     // 正当
                "*ipa_A[10]=*ip_D;",     // 正当
            };
            StatementAssignHelper.acceptListTest(testDataArr);
        }

        @Test
        public void falseTest() throws FatalErrorException {
            TestDataAndErrMessage[] teList = {
                // (1) 整数型の扱い
                new TestDataAndErrMessage("i_a=&1;", "左辺の型[int]と右辺の型[int*]が異なります"),       // 不当
                // (2) ポインタ型の扱い
                new TestDataAndErrMessage("ip_a=1;", "左辺の型[int*]と右辺の型[int]が異なります"),       // 不当
                // (3) 配列型の扱い
                new TestDataAndErrMessage("ia_a[10]=&1;", "左辺の型[int]と右辺の型[int*]が異なります"),       // 不当
                // (4) ポインタ配列型の扱い
                new TestDataAndErrMessage("ipa_a[2]=10;", "左辺の型[int*]と右辺の型[int]が異なります"),     // 不当
                new TestDataAndErrMessage("*ipa_a[3]=&10;", "左辺の型[int]と右辺の型[int*]が異なります"),  // 不当 
                // (5) 定数には代入できないことの確認
                new TestDataAndErrMessage("c_a=1;", "定数には代入できません"),        // 不当
            }; 
            StatementAssignHelper.rejectListTest(teList);
        }
    }


    public static class StatementInputTest {
        SemanticCheckTestHelper<StatementInput> StatementInputHelper = new SemanticCheckTestHelper<StatementInput>(StatementInput.class);

        @Test
        public void trueTest() throws FatalErrorException {
            String[] testDataArr = {
                // (1) 整数型の扱い
                "input i_A;",     // 正当
                // (2) ポインタ型の扱い
                "input ip_A;",    // 正当
                "input *ip_A;",    // 正当
                // (3) 配列型の扱い
                "input ia_A[10];",     // 正当
                // (4) ポインタ配列型の扱い
                "input ipa_A[100];",    // 正当
                "input *ipa_A[10];",     // 正当
            };
            StatementInputHelper.acceptListTest(testDataArr);
        }

        @Test
        public void falseTest() throws FatalErrorException {
            TestDataAndErrMessage[] teList = {
                // (5) 定数には代入できないことの確認
                new TestDataAndErrMessage("input c_a;", "定数には代入できません"),   // 不当
            }; 
            StatementInputHelper.rejectListTest(teList);
        }
    }


    public static class StatementOutputTest {
        SemanticCheckTestHelper<StatementOutput> StatementOutputHelper = new SemanticCheckTestHelper<StatementOutput>(StatementOutput.class);

        @Test
        public void trueTest() throws FatalErrorException {
            String[] testDataArr = {
                // (1) 整数型の扱い
                "output i_A;",     // 正当
                "output &i_A;",     // 正当
                "output 100+200;",     // 正当
                // (2) ポインタ型の扱い
                "output ip_A;",    // 正当
                "output *ip_A;",    // 正当
                // (3) 配列型の扱い
                "output ia_A[10];",     // 正当
                "output &ia_A[10];",     // 正当
                // (4) ポインタ配列型の扱い
                "output ipa_A[100];",    // 正当
                "output *ipa_B[200];",    // 正当
                // (5) 定数の扱い
                "output c_h;",    // 正当
            };
            StatementOutputHelper.acceptListTest(testDataArr);
        }
    }

}
