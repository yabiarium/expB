package lang.c.parse;

import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

import lang.FatalErrorException;
import lang.c.CType;
import lang.c.testhelpter.SemanticCheckTestHelper;
import lang.c.testhelpter.TestDataAndCType;
import lang.c.testhelpter.TestDataAndErrMessage;

@RunWith(Enclosed.class)
public class T02_41SemanticCheckTest {
    
    public static class factoAmpTest {
        
        SemanticCheckTestHelper<FactorAmp> factorAmpHelper = new SemanticCheckTestHelper<FactorAmp>(FactorAmp.class);

        // リストを渡す typeテスト
        @Test
        public void typeList() throws FatalErrorException {
            TestDataAndCType[] ttList = {
                new TestDataAndCType("&100", CType.T_pint),
            };
            factorAmpHelper.typeListTest(ttList);
        }
    }

    public static class ExpressionTest {
        SemanticCheckTestHelper<Expression> expressionHelper = new SemanticCheckTestHelper<Expression>(Expression.class);

        //足し算のテスト
        @Test
        public void addInt() throws FatalErrorException{
            String[] testDataArr = {
                "1+1",
            };
            expressionHelper.acceptListTest(testDataArr);

            TestDataAndCType[] ttList = {
                new TestDataAndCType("1+1", CType.T_int),
            };
            expressionHelper.typeListTest(ttList);
        }

        @Test
        public void addPInt() throws FatalErrorException{
            String[] testDataArr = {
                "1+&1",
                "&1+1",
            };
            expressionHelper.acceptListTest(testDataArr);

            TestDataAndCType[] ttList = {
                new TestDataAndCType("1+&1", CType.T_pint),
                new TestDataAndCType("&1+1", CType.T_pint),
            };
            expressionHelper.typeListTest(ttList);
        }

        @Test
        public void addError() throws FatalErrorException{
            TestDataAndErrMessage[] teList = {
                new TestDataAndErrMessage("&1+&1", "左辺の型[int*]と右辺の型[int*]は足せません"),
            }; 
            expressionHelper.rejectListTest(teList);
        }

        //引き算のテスト
        @Test
        public void subInt() throws FatalErrorException{
            String[] testDataArr = {
                "2-1",
                "&2-&1",
                "&3-1-&1",
            };
            expressionHelper.acceptListTest(testDataArr);

            TestDataAndCType[] ttList = {
                new TestDataAndCType("2-1", CType.T_int),
                new TestDataAndCType("&2-&1", CType.T_int),
                new TestDataAndCType("&3-1-&1", CType.T_int),
            };
            expressionHelper.typeListTest(ttList);
        }

        @Test
        public void subPInt() throws FatalErrorException{
            String[] testDataArr = {
                "&2-1",
            };
            expressionHelper.acceptListTest(testDataArr);

            TestDataAndCType[] ttList = {
                new TestDataAndCType("&2-1", CType.T_pint),
            };
            expressionHelper.typeListTest(ttList);
        }

        @Test
        public void subError() throws FatalErrorException{
            TestDataAndErrMessage[] teList = {
                new TestDataAndErrMessage("2-&1", "左辺の型[int]と右辺の型[int*]は引けません"),
                new TestDataAndErrMessage("&3-&1-&1", "左辺の型[int]と右辺の型[int*]は引けません"),
            }; 
            expressionHelper.rejectListTest(teList);
        }
        
    }

    public static class ProgramTest {
        SemanticCheckTestHelper<Program> programHelper = new SemanticCheckTestHelper<Program>(Program.class);

        // 個別acceptテスト
        @Test
        public void accept() throws FatalErrorException{
            programHelper.acceptTest("1+3");
            programHelper.acceptTest("321+123");
        }
    
        // リストacceptテスト
        @Test
        public void acceptList() throws FatalErrorException{
            String[] testDataArr = {
                "1+3",
                "332+123"
            };
            programHelper.acceptListTest(testDataArr);
        }
    
        /*
        Reject() については， semanticCheck で実行される fatalError() を確認する 必要がある
        cv00 にはその例が存在しない
        以下は parse() で発生させた fatalError を キャッチしている（semanticCheck() には意味のないテスト)
         */
        @Test
        public void reject() throws FatalErrorException{
            // このテストは結局 parse() のエラーをチェックしているに過ぎない．
            TestDataAndErrMessage te1 = new TestDataAndErrMessage("1+3@@@@@", "プログラムの最後にゴミがあります");
            TestDataAndErrMessage te2 = new TestDataAndErrMessage("1234+", "+の後ろはtermです");

            programHelper.rejectTest(te1);
            programHelper.rejectTest(te2);
        }

        /*
        Reject() については， semanticCheck で実行される fatalError() を確認する 必要がある
        cv00 にはその例が存在しない
        以下は parse() で発生させた fatalError を キャッチしている（semanticCheck() には意味のないテスト)
         */
        @Test
        public void rejectList() throws FatalErrorException{
            // このテストは結局 parse() のエラーをチェックしているに過ぎない．
            TestDataAndErrMessage[] teList = {
                new TestDataAndErrMessage("1+3@@@@@", "プログラムの最後にゴミがあります"),
                new TestDataAndErrMessage("1+", "+の後ろはtermです")
            }; 
            programHelper.rejectListTest(teList);
        }
    }
}
