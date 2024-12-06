package lang.c.parse;

import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

import lang.FatalErrorException;
import lang.c.testhelpter.CodeGenTestHelper;

@RunWith(Enclosed.class)
public class T06_51CodeGenTest {

    public static class ConditionTest {
        CodeGenTestHelper<Condition> conditionHelper = new CodeGenTestHelper<Condition>(Condition.class);

        @Test
        public void trueTest() throws FatalErrorException {
            String testData = "true";
            // ここに miniCompiler の実行結果を貼り付けるのはNG行為
            String expected = """
                        MOV #0x0001, (R6)+ ;; Condition: true をスタックに積む
                    """;
            // ↑ """の横位置が先頭と考える．この場合 ADD や MOV が """" と同じか前にあると，その命令が先頭で先頭に空白やタブがない判定になる
            conditionHelper.checkCodeGen(testData, expected);
        }

        @Test
        public void falseTest() throws FatalErrorException {
            String testData = "false";
            // ここに miniCompiler の実行結果を貼り付けるのはNG行為
            String expected = """
                        MOV #0x0000, (R6)+ ;; Condition: false をスタックに積む
                    """;
            // ↑ """の横位置が先頭と考える．この場合 ADD や MOV が """" と同じか前にあると，その命令が先頭で先頭に空白やタブがない判定になる
            conditionHelper.checkCodeGen(testData, expected);
        }

        @Test
        public void LTTest1() throws FatalErrorException {
            String testData = "1 < 2";
            // ここに miniCompiler の実行結果を貼り付けるのはNG行為
            String expected = """
                        MOV #1, (R6)+       ;; ConditionLT: 左辺を積む
                        MOV #2, (R6)+       ;;    右辺を積む
                        MOV -(R6), R0       ;;    右辺をR0に
                        MOV -(R6), R1       ;;    左辺をR1に
                        MOV #0x0001, R2     ;;    R2に true を積む
                        CMP R0, R1          ;;    R1<R0  ==>>   R1-R0<0 (negativeか判定すればいい)
                        BRN LT1             ;;    R1<R0 なら LT1 に分岐
                        CLR R2              ;;    R2 を false に
                    LT1:                    ;;    true だったときのジャンプ先 (ここに飛ぶときは R2 は true)
                        MOV R2,(R6)+        ;;    真偽結果をスタックに積む
                    """;
            // ↑ """の横位置が先頭と考える．この場合 ADD や MOV が """" と同じか前にあると，その命令が先頭で先頭に空白やタブがない判定になる
            conditionHelper.checkCodeGen(testData, expected);
        }

        @Test
        public void LETest1() throws FatalErrorException {
            String testData = "1 <= 2";
            // ここに miniCompiler の実行結果を貼り付けるのはNG行為
            String expected = """
                        MOV #1, (R6)+       ;; ConditionLE: 左辺を積む
                        MOV #2, (R6)+       ;;    右辺を積む
                        MOV -(R6), R0       ;;    右辺をR0に
                        MOV -(R6), R1       ;;    左辺をR1に
                        MOV #0x0001, R2     ;;    R2に true を積む
                        CMP R0, R1          ;;    R1<R0  ==>>   R1-R0<=0 (negativeまたはzeroか判定すればいい)
                        BRN LE1             ;;    R1<R0 なら LE1 に分岐
                        BRZ LE1             ;;    R1==R0なら LE1 に分岐
                        CLR R2              ;;    R2 を false に
                    LE1:                    ;;    true だったときのジャンプ先 (ここに飛ぶときは R2 は true)
                        MOV R2,(R6)+        ;;    真偽結果をスタックに積む
                    """;
            // ↑ """の横位置が先頭と考える．この場合 ADD や MOV が """" と同じか前にあると，その命令が先頭で先頭に空白やタブがない判定になる
            conditionHelper.checkCodeGen(testData, expected);
        }

        @Test
        public void GTTest1() throws FatalErrorException {
            String testData = "1 > 2";
            // ここに miniCompiler の実行結果を貼り付けるのはNG行為
            String expected = """
                        MOV #1, (R6)+       ;; ConditionGT: 左辺を積む
                        MOV #2, (R6)+       ;;    右辺を積む
                        MOV -(R6), R1       ;;    右辺をR1に
                        MOV -(R6), R0       ;;    左辺をR0に
                        MOV #0x0001, R2     ;;    R2に true を積む
                        CMP R0, R1          ;;    R1<R0  ==>>   R1-R0<0 (negativeか判定すればいい)
                        BRN GT1             ;;    R1<R0 なら GT1 に分岐
                        CLR R2              ;;    R2 を false に
                    GT1:                    ;;    true だったときのジャンプ先 (ここに飛ぶときは R2 は true)
                        MOV R2,(R6)+        ;;    真偽結果をスタックに積む
                    """;
            // ↑ """の横位置が先頭と考える．この場合 ADD や MOV が """" と同じか前にあると，その命令が先頭で先頭に空白やタブがない判定になる
            conditionHelper.checkCodeGen(testData, expected);
        }

        @Test
        public void GETest1() throws FatalErrorException {
            String testData = "1 >= 2";
            // ここに miniCompiler の実行結果を貼り付けるのはNG行為
            String expected = """
                        MOV #1, (R6)+       ;; ConditionGE: 左辺を積む
                        MOV #2, (R6)+       ;;    右辺を積む
                        MOV -(R6), R1       ;;    右辺をR1に
                        MOV -(R6), R0       ;;    左辺をR0に
                        MOV #0x0001, R2     ;;    R2に true を積む
                        CMP R0, R1          ;;    R1<R0  ==>>   R1-R0<=0 (negativeまたはzeroか判定すればいい)
                        BRN GE1             ;;    R1<R0 なら GE1 に分岐
                        BRZ GE1             ;;    R1==R0なら GE1 に分岐
                        CLR R2              ;;    R2 を false に
                    GE1:                    ;;    true だったときのジャンプ先 (ここに飛ぶときは R2 は true)
                        MOV R2,(R6)+        ;;    真偽結果をスタックに積む
                    """;
            // ↑ """の横位置が先頭と考える．この場合 ADD や MOV が """" と同じか前にあると，その命令が先頭で先頭に空白やタブがない判定になる
            conditionHelper.checkCodeGen(testData, expected);
        }

        @Test
        public void EQTest1() throws FatalErrorException {
            String testData = "1 == 2";
            // ここに miniCompiler の実行結果を貼り付けるのはNG行為
            String expected = """
                        MOV #1, (R6)+       ;; ConditionEQ: 左辺を積む
                        MOV #2, (R6)+       ;;    右辺を積む
                        MOV -(R6), R0       ;;    右辺をR0に
                        MOV -(R6), R1       ;;    左辺をR1に
                        MOV #0x0001, R2     ;;    R2に true を積む
                        CMP R0, R1          ;;    R1==R0  ==>>   R1-R0==0 (zeroか判定すればいい)
                        BRZ EQ1             ;;    R1==R0なら EQ1 に分岐
                        CLR R2              ;;    R2 を false に
                    EQ1:                    ;;    true だったときのジャンプ先 (ここに飛ぶときは R2 は true)
                        MOV R2,(R6)+        ;;    真偽結果をスタックに積む
                    """;
            // ↑ """の横位置が先頭と考える．この場合 ADD や MOV が """" と同じか前にあると，その命令が先頭で先頭に空白やタブがない判定になる
            conditionHelper.checkCodeGen(testData, expected);
        }

        @Test
        public void NETest1() throws FatalErrorException {
            String testData = "1 != 2";
            // ここに miniCompiler の実行結果を貼り付けるのはNG行為
            String expected = """
                        MOV #1, (R6)+       ;; ConditionNE: 左辺を積む
                        MOV #2, (R6)+       ;;    右辺を積む
                        MOV -(R6), R0       ;;    右辺をR0に
                        MOV -(R6), R1       ;;    左辺をR1に
                        MOV #0x0000, R2     ;;    R2に false を積む
                        CMP R0, R1          ;;    R1==R0  ==>>   R1-R0==0 (zeroか判定すればいい)
                        BRZ NE1             ;;    R1==R0なら LE1 に分岐
                        MOV #0x0001, R2     ;;    R2 を true に
                    NE1:                    ;;    false(R1==R0) だったときのジャンプ先 (ここに飛ぶときは R2 は false)
                        MOV R2,(R6)+        ;;    真偽結果をスタックに積む
                    """;
            // ↑ """の横位置が先頭と考える．この場合 ADD や MOV が """" と同じか前にあると，その命令が先頭で先頭に空白やタブがない判定になる
            conditionHelper.checkCodeGen(testData, expected);
        }

        @Test
        public void LTTest2() throws FatalErrorException {
            String testData = "i_a < 3";
            // ここに miniCompiler の実行結果を貼り付けるのはNG行為
            String expected = """
                        MOV #i_a,(R6)+  ;; 左辺 Ident
                        MOV -(R6),R0    ;; AddressToValue
                        MOV (R0),(R6)+
                        MOV #3,(R6)+    ;; 右辺 Number
                        MOV -(R6),R0    ;; ConditionLT
                        MOV -(R6),R1
                        MOV #0x0001,R2
                        CMP R0,R1
                        BRN LT1
                        CLR R2
                    LT1:
                        MOV R2,(R6)+
                    """;
            // ↑ """の横位置が先頭と考える．この場合 ADD や MOV が """" と同じか前にあると，その命令が先頭で先頭に空白やタブがない判定になる
            conditionHelper.checkCodeGen(testData, expected);
        }

        @Test
        public void GTTest2() throws FatalErrorException {
            String testData = "10 > *ip_a";
            // ここに miniCompiler の実行結果を貼り付けるのはNG行為
            String expected = """
                        MOV #10, (R6)+      ;; 左辺 Number
                        MOV #ip_a, (R6)+    ;; 右辺 Ident
                        MOV -(R6), R0       ;; AddressToValue
                        MOV (R0), (R6)+
                        MOV -(R6), R0       ;; PrimaryMult
                        MOV (R0), (R6)+
                        MOV -(R6), R1       ;; ConditionGT
                        MOV -(R6), R0
                        MOV #0x0001, R2
                        CMP R0, R1
                        BRN GT1
                        CLR R2
                    GT1:
                        MOV R2, (R6)+
                    """;
            // ↑ """の横位置が先頭と考える．この場合 ADD や MOV が """" と同じか前にあると，その命令が先頭で先頭に空白やタブがない判定になる
            conditionHelper.checkCodeGen(testData, expected);
        }


        @Test
        public void EQTest2() throws FatalErrorException {
            String testData = "ia_a[1] == 4";
            // ここに miniCompiler の実行結果を貼り付けるのはNG行為
            String expected = """
                        MOV #ia_a, (R6)+    ;; 左辺 Ident
                        MOV #1, (R6)+       ;; Number
                        MOV -(R6), R0       ;; Array
                        MOV -(R6), R1
                        ADD R1, R0
                        MOV R0, (R6)+
                        MOV -(R6), R0       ;; AddressToValue
                        MOV (R0), (R6)+
                        MOV #4, (R6)+       ;; 右辺 Number
                        MOV -(R6), R0       ;; ConditionEQ
                        MOV -(R6), R1
                        MOV #0x0001, R2
                        CMP R0, R1
                        BRZ EQ1
                        CLR R2
                    EQ1:
                        MOV R2, (R6)+
                    """;
            // ↑ """の横位置が先頭と考える．この場合 ADD や MOV が """" と同じか前にあると，その命令が先頭で先頭に空白やタブがない判定になる
            conditionHelper.checkCodeGen(testData, expected);
        }

    }
}