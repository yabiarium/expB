package lang.c.parse;

import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

import lang.FatalErrorException;
import lang.c.testhelpter.ParseTestHelper;
import lang.c.testhelpter.TestDataAndErrMessage;

@RunWith(Enclosed.class)
public class T04_32ParseTest {
    
    public static class ArrayTest {
        ParseTestHelper<Array> arrayeHelper = new ParseTestHelper<Array>(Array.class);

        @Test
        public void trueTest() throws FatalErrorException {
            String[] testDataArr = {
                    "[100]",
                    "[ia_f]",
                    "[*ipa_g]",
                    "[ia_f[i_c]]",
            };
            arrayeHelper.parseAcceptTestList(testDataArr);
        }

        @Test
        public void falseTest() throws FatalErrorException {
            TestDataAndErrMessage[] arr = {
                //new TestDataAndErrMessage("[100","]がありません"), //CV09: 後ろの]が補われるため、エラーは出るが解析中の棄却はされない
                //new TestDataAndErrMessage("[]","[の後ろはexpressionです"), //CV09: expressionがない場合、]まで読み飛ばされるため棄却されない
            };
            arrayeHelper.parseRejectTestList(arr);
        }
    }

    public static class VariableTest {
        ParseTestHelper<Variable> variableHelper = new ParseTestHelper<Variable>(Variable.class);

        @Test
        public void trueTest() throws FatalErrorException {
            String[] testDataArr = {
                "i_c",
                "ia_f[100]",
                "ip_d",
                "ipa_g[ip_b]"
            };
            variableHelper.parseAcceptTestList(testDataArr);
        }

        @Test
        public void falseTest() throws FatalErrorException {
            TestDataAndErrMessage[] arr = {
                new TestDataAndErrMessage("*12","isFirst() が false です"),
                //new TestDataAndErrMessage("ipa_c[]","[の後ろはexpressionです"), //CV09: Arrayにおいてexpがないと]まで読み飛ばされて回復できるエラーとなるため棄却されなくなる
            };
            variableHelper.parseRejectTestList(arr);
        }
    }

    public static class PrimaryTest {
        ParseTestHelper<Primary> primaryHelper = new ParseTestHelper<Primary>(Primary.class);

        @Test
        public void trueTest() throws FatalErrorException {
            String[] testDataArr = {
                "ip_a",
                "ia_f[100]",
                "*ip_d",
                "*ipa_g[ip_b]"
            };
            primaryHelper.parseAcceptTestList(testDataArr);
        }

        @Test
        public void falseTest() throws FatalErrorException {
            TestDataAndErrMessage[] arr = {
                new TestDataAndErrMessage("*12","*の後ろはvariableです"),
                //new TestDataAndErrMessage("ipa_g[]","[の後ろはexpressionです"), //CV09: VariableTestのfalseTestと同様
            };
            primaryHelper.parseRejectTestList(arr);
        }
    }


    public static class FactorAmpTest {
        ParseTestHelper<FactorAmp> factorAmpyHelper = new ParseTestHelper<FactorAmp>(FactorAmp.class);
        @Test
        public void falseTest() throws FatalErrorException {
            TestDataAndErrMessage[] arr = {
                new TestDataAndErrMessage("&*ip_a","&の後ろに*は置けません"),
            };
            factorAmpyHelper.parseRejectTestList(arr);
        }
    }


    public static class ExpressionTest {
        ParseTestHelper<Expression> expressionHelper = new ParseTestHelper<Expression>(Expression.class);

        @Test
        public void trueTest() throws FatalErrorException {
            String[] testDataArr = {
                "i+a",               // parse() は通るはず (semanticCheck() でエラーを出すので)
                "*ip_a-*ipa[i_c]",
                "i[b]",              // parse() は通るはず (semanticCheck() でエラーになるデータ)    
                "12[4]",             // expression の parse() は通る (12 までで受理し，'['で止まるから：program ではエラーになるはず)
                "*aaa**bbb"          // これも通るはず (これも semanticCheck() の仕事)
            };
            expressionHelper.parseAcceptTestList(testDataArr);
        }

        @Test
        public void falseTest() throws FatalErrorException {
            TestDataAndErrMessage[] arr = {
                //new TestDataAndErrMessage("i_a[","[の後ろはexpressionです"),  //CV09: 同様、棄却はされない
                //new TestDataAndErrMessage("bbb[200","]がありません"), //CV09: Arrayにおいて]が補われる
                new TestDataAndErrMessage("*aaa****bbb","*の後ろはvariableです"),
                new TestDataAndErrMessage("(aaa-bbb",")がありません"),
            };
            expressionHelper.parseRejectTestList(arr);
        }
    }
}