package lang.c.parse;

import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

import lang.FatalErrorException;
import lang.c.testhelpter.IsFirstTestHelper;

@RunWith(Enclosed.class)
public class T05_31IsFirstTest {

    public static class StatementAssignTest {
        IsFirstTestHelper<StatementAssign> StatementAssignHelper = new IsFirstTestHelper<StatementAssign>(StatementAssign.class);

        @Test
        public void trueTest() throws FatalErrorException {
            String[] testDataArr = {"*A_BC12",
                                    "A_BC12" };
            StatementAssignHelper.trueListTest(testDataArr);
        }
    }

    public static class StatementInputTest {
        IsFirstTestHelper<StatementInput> StatementInputHelper = new IsFirstTestHelper<StatementInput>(StatementInput.class);

        @Test
        public void trueTest() throws FatalErrorException {
            String[] testDataArr = {"input a;",
                                    "input *b;" };
            StatementInputHelper.trueListTest(testDataArr);
        }

        @Test
        public void falseTest() throws FatalErrorException{
            String[] testDataArr = {"a=b;",
                                    "output &c;" };
            StatementInputHelper.falseListTest(testDataArr);
        }
    }

    public static class StatementOutputTest {
        IsFirstTestHelper<StatementOutput> StatementOutputHelper = new IsFirstTestHelper<StatementOutput>(StatementOutput.class);

        @Test
        public void trueTest() throws FatalErrorException {
            String[] testDataArr = {"output 13;",
                                    "output a;",
                                    "output *b;" };
            StatementOutputHelper.trueListTest(testDataArr);
        }

        @Test
        public void falseTest() throws FatalErrorException{
            String[] testDataArr = {"a=b;",
                                    "input abc;" };
            StatementOutputHelper.falseListTest(testDataArr);
        }
    }

    public static class ProgramTest {
        IsFirstTestHelper<Program> ProgramHelper = new IsFirstTestHelper<Program>(Program.class);

        @Test
        public void trueTest() throws FatalErrorException {
            String[] testDataArr = {"a",
                                    "*ip_f",
                                    "input",
                                    "output" };
            ProgramHelper.trueListTest(testDataArr);
        }

        @Test
        public void falseTest() throws FatalErrorException{
            String[] testDataArr = {"100",
                                    "/200" };
            ProgramHelper.falseListTest(testDataArr);
        }
    }

}
