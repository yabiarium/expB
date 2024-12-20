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
public class T03_41SemanticCheckTest {
    
    public static class MinusFactorTest {
        SemanticCheckTestHelper<MinusFactor> minusFactorHelper = new SemanticCheckTestHelper<MinusFactor>(MinusFactor.class);
        // リストを渡す typeテスト
        @Test
        public void typeList() throws FatalErrorException {
            TestDataAndErrMessage[] teList = {
                new TestDataAndErrMessage("-&100", "minusFactor: semanticCheck(): -の後ろはT_intです[int*]"),
            }; 
            minusFactorHelper.rejectListTest(teList);
        }
    }

    public static class TermTest {
        SemanticCheckTestHelper<Term> termHelper = new SemanticCheckTestHelper<Term>(Term.class);
        // リストを渡す typeテスト
        @Test
        public void typeList() throws FatalErrorException {
            TestDataAndCType[] ttList = {
                new TestDataAndCType("10*20", CType.T_int),
                new TestDataAndCType("-30/2", CType.T_int),
            };
            termHelper.typeListTest(ttList);
        }

        @Test
        public void multDivErr() throws FatalErrorException {
            TestDataAndErrMessage[] teList = {
                new TestDataAndErrMessage("(&1-2)*3", "左辺の型[int*]と右辺の型[int]は掛けられません"),
                new TestDataAndErrMessage("(&1+2)/3", "左辺の型[int*]は右辺の型[int]で割れません"),
                new TestDataAndErrMessage("1*(&2+3))", "左辺の型[int]と右辺の型[int*]は掛けられません"),
                new TestDataAndErrMessage("1/(&2-3)", "左辺の型[int]は右辺の型[int*]で割れません"),
                new TestDataAndErrMessage("(&1+3)*(&1+4)", "左辺の型[int*]と右辺の型[int*]は掛けられません"),
                new TestDataAndErrMessage("(&1+3)/(&1+4)", "左辺の型[int*]は右辺の型[int*]で割れません"),
            }; 
            termHelper.rejectListTest(teList);
        }

    }
}
