package lang.c.parse;

import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

import lang.FatalErrorException;
import lang.c.testhelpter.ParseTestHelper;
import lang.c.testhelpter.TestDataAndErrMessage;

@RunWith(Enclosed.class)
public class T05_32ParseTest {
    
    public static class StatementAssignTest {
        ParseTestHelper<StatementAssign> StatementAssignHelper = new ParseTestHelper<StatementAssign>(StatementAssign.class);

        @Test
        public void trueTest() throws FatalErrorException {
            String[] testDataArr = {
                    // (1) 整数型の代入
                    "i_a = 0;",
                    // (2) ポインタ型の代入
                    "ip_d = &i_a;",
                    "*ip_d = 100;",
                    // (3) 配列型の代入
                    "ia_f[0] = 100;",
                    "ip_e = &ia_f[1];",
                    // (4) ポインタ配列型の代入
                    "ipa_g[2] = ip_d;",
                    "*ipa_g[2] = *ip_e;"
            };
            StatementAssignHelper.parseAcceptTestList(testDataArr);
        }

        @Test
        public void falseTest() throws FatalErrorException {
            TestDataAndErrMessage[] arr = {
                // (1) 代入文の間違い
                // new TestDataAndErrMessage("i_a=0",";がありません"),   // 構文解析エラー (セミコロンなし)
                // new TestDataAndErrMessage("i_a 0","=がありません"),   // 構文解析エラー (＝なし)
                // new TestDataAndErrMessage("i_a=;","=の後ろはexpressionです"),   // 構文解析エラー (expression なし)
            };
            StatementAssignHelper.parseRejectTestList(arr);
        }
    }


    public static class StatementInputTest {
        ParseTestHelper<StatementInput> StatementInputHelper = new ParseTestHelper<StatementInput>(StatementInput.class);

        @Test
        public void trueTest() throws FatalErrorException {
            String[] testDataArr = {
                    // (1) 整数型
                    "input i_a;",
                    // (2) ポインタ型
                    "input ip_d;",
                    "input *ip_d;",
                    // (3) 配列型
                    "input ia_f[0];",
                    // (4) ポインタ配列型
                    "input ipa_g[2];",
                    "input *ipa_g[2];"
            };
            StatementInputHelper.parseAcceptTestList(testDataArr);
        }

        @Test
        public void falseTest() throws FatalErrorException {
            TestDataAndErrMessage[] arr = {
                // (1) input文の間違い
                // new TestDataAndErrMessage("input ;","inputの後ろはprimaryです"),   // 構文解析エラー (primary なし)
                // new TestDataAndErrMessage("input 100;","inputの後ろはprimaryです"),   // 構文解析エラー (primary なし)
                // new TestDataAndErrMessage("input i",";がありません"),   // 構文解析エラー (; なし)
            };
            StatementInputHelper.parseRejectTestList(arr);
        }
    }


    public static class StatementOutputTest {
        ParseTestHelper<StatementOutput> StatementOutputHelper = new ParseTestHelper<StatementOutput>(StatementOutput.class);

        @Test
        public void trueTest() throws FatalErrorException {
            String[] testDataArr = {
                    // (1) 整数型
                    "output i_a;",
                    "output 200;",
                    // (2) ポインタ型
                    "output &ip_d;",
                    "output ip_d;",
                    "output *ip_d;",
                    // (3) 配列型
                    "output ia_f[0];",
                    "output &ia_f[1];",
                    // (4) ポインタ配列型
                    "output &ipa_g[2];",
                    "output ipa_g[2];",
                    "output *ipa_g[2];"
            };
            StatementOutputHelper.parseAcceptTestList(testDataArr);
        }

        @Test
        public void falseTest() throws FatalErrorException {
            TestDataAndErrMessage[] arr = {
                // (1) output文の間違い
                // new TestDataAndErrMessage("output ;","outputの後ろはexpressionです"),   // 構文解析エラー (expression なし)
                // new TestDataAndErrMessage("output 100",";がありません"),   // 構文解析エラー (; なし)
            };
            StatementOutputHelper.parseRejectTestList(arr);
        }
    }


    public static class ProgramTest {
        ParseTestHelper<Program> ProgramHelper = new ParseTestHelper<Program>(Program.class);

        @Test
        public void trueTest() throws FatalErrorException {
            String[] testDataArr = {
                // 連続したステートメントの受理
                "input i_a;\n" +
                "i_b = *ip_d + i_a;\n" +
                "output i_b;"
            };
            ProgramHelper.parseAcceptTestList(testDataArr);
        }

        @Test
        public void falseTest() throws FatalErrorException {
            TestDataAndErrMessage[] arr = {
                // ステートメント間に ゴミが挿入
                //new TestDataAndErrMessage("input i_a;  ;  i_b = *ip_d + i_a;  output i_b;", "プログラムの最後にゴミがあります")  // statement が検出されない場合，処理が終わり，プログラムの最後にゴミがありますの判定のはず
            };
            ProgramHelper.parseRejectTestList(arr);
        }
    }

}