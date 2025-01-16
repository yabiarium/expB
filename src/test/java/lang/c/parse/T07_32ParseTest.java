package lang.c.parse;

import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

import lang.FatalErrorException;
import lang.c.testhelpter.ParseTestHelper;
// import lang.c.testhelpter.ParseTestHelper2;
import lang.c.testhelpter.TestDataAndErrMessage;

@RunWith(Enclosed.class)
public class T07_32ParseTest {

    public static class StatementBlockTest {
        ParseTestHelper<StatementBlock> statementBlockHelper = new ParseTestHelper<StatementBlock>(StatementBlock.class);

        @Test
        public void accept() throws FatalErrorException {
            String[] testDataArr = {
                    "{ i_c = 1; i_d = 3; }", // 全部解釈されて accept
                    "{ i_c = 1;}",
                    "{}",
            };
            statementBlockHelper.parseAcceptTestList(testDataArr);
        }

        @Test
        public void reject() throws FatalErrorException {
            TestDataAndErrMessage[] arr = {
                    //new TestDataAndErrMessage("{ i_a = 1; ip_b = &i_a;", "}がありません"), //CV09: StatementBlockにて}が補われ棄却されなくなった
            };
            statementBlockHelper.parseRejectTestList(arr);
        }
    }

    public static class ConditionBlockTest {
        ParseTestHelper<ConditionBlock> conditionBlockHelper = new ParseTestHelper<ConditionBlock>(ConditionBlock.class);

        @Test
        public void accept() throws FatalErrorException {
            String[] testDataArr = {
                    "(false)", // 全部解釈されて accept
            };
            conditionBlockHelper.parseAcceptTestList(testDataArr);
        }

        @Test
        public void reject() throws FatalErrorException {
            TestDataAndErrMessage[] arr = {
                    // new TestDataAndErrMessage("(", "(の後ろはconditionExpressionです"),
                    // new TestDataAndErrMessage("( true", ")がありません"),
            };
            conditionBlockHelper.parseRejectTestList(arr);
        }
    }

    public static class StatementIfTest {
        ParseTestHelper<StatementIf> statementIfHelper = new ParseTestHelper<StatementIf>(StatementIf.class);

        @Test
        public void accept() throws FatalErrorException {
            String[] testDataArr = {
                    "if (true) { i_a=1; i_b=2; }",
                    "if (true) { i_a=1; i_b=2; } else { i_a=2; i_b=3;}",
                    "if (true) { i_a=1; i_b=2; } else if ( true ) { i_a=2; } else { i_a=3; }",

                    "if (true) { if (true) { if (true) { i_a=1; i_b=2; }}}",
                    
                    "if (true) i_a=1;",
                    "if (true) i_a=1; else i_a=2;",
                    "if (true) if (true) if (true) i_a=3;",
            };
            statementIfHelper.parseAcceptTestList(testDataArr);
        }

        @Test
        public void reject() throws FatalErrorException {
            TestDataAndErrMessage[] arr = {
                    // new TestDataAndErrMessage("if i_a==1", "ifの後ろはconditionBlockです"),
                    // new TestDataAndErrMessage("if (true)", "conditionBlockの後ろはstatementです"),
                    // new TestDataAndErrMessage("if (true) i_a=1; else", "elseの後ろはstatementです"),
            };
            statementIfHelper.parseRejectTestList(arr);
        }
    }

    public static class StatementWhileTest {
        ParseTestHelper<StatementWhile> statementWhileHelper = new ParseTestHelper<StatementWhile>(StatementWhile.class);

        @Test
        public void accept() throws FatalErrorException {
            String[] testDataArr = {
                    "while (true) { i_a=1; }",
                    "while (true) { while (true) { i_a=1; i_b=2;} }",
                    // 以下は単独ステートメントも許す場合 (StatementBlock を使っている場合上手くやれば以下は自動的にできる)
                    "while (true) i_a=1;",
                    "while (true) while (true) while (true) i_a=1;",
            };
            statementWhileHelper.parseAcceptTestList(testDataArr);
        }

        @Test
        public void reject() throws FatalErrorException {
            TestDataAndErrMessage[] arr = {
                    // new TestDataAndErrMessage("while i_a==1", "whileの後ろはconditionBlockです"),
                    // new TestDataAndErrMessage("while (true)", "conditionBlockの後ろはstatementです"),
            };
            statementWhileHelper.parseRejectTestList(arr);
        }
    }

    public static class ProgramTest {
        ParseTestHelper<Program> ProgramHelper = new ParseTestHelper<Program>(Program.class);

        @Test
        public void accept() throws FatalErrorException {
            String[] testDataArr = {
                    "while (true) { i_a=1; }",
                    "while (true) { while (true) { i_a=1; i_b=2;} }",
                    // 以下は単独ステートメントも許す場合 (StatementBlock を使っている場合上手くやれば以下は自動的にできる)
                    "while (true) i_a=1;",
                    "while (true) while (true) while (true) i_a=1;",
            };
            ProgramHelper.parseAcceptTestList(testDataArr);
        }

        @Test
        public void reject() throws FatalErrorException {
            TestDataAndErrMessage[] arr = {
                    //new TestDataAndErrMessage("while i_a==1", "whileの後ろはconditionBlockです"),
                    //else{}の後にelseがある
                    //new TestDataAndErrMessage("if (true){}else{}else", "プログラムの最後にゴミがあります"),
            };
            ProgramHelper.parseRejectTestList(arr);
        }
    }
}