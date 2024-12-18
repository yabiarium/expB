package lang.c.parse;

import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

import lang.FatalErrorException;
import lang.c.testhelpter.CodeGenTestHelper;

@RunWith(Enclosed.class)
public class T08_51CodeGenTest {

    public static class StatementTest {
        // ConditionBlockでのテストでもよかったかも
        CodeGenTestHelper<Statement> statementHelper = new CodeGenTestHelper<Statement>(Statement.class);

        @Test
        public void notFactorTest1() throws FatalErrorException {
            String testData =   "if (!true) {\r\n" +
                                "    i_a=3;\r\n" +
                                "}";
            String expected = """
                        MOV #0x0001,(R6)+   ;; Condition: trueを積む
                        MOV -(R6),R0        ;; NotFactor: Conditionの結果を取り出す
                        XOR #0x0001,R0      ;;            NOT演算
                        MOV R0,(R6)+        ;;            結果を積む
                        MOV -(R6),R0        ;; StatementIf: ConditionBlockの結果を取り出す
                        BRZ ELSE1

                        ;; i_a=3
                        MOV #i_a,(R6)+
                        MOV #3,(R6)+
                        MOV -(R6),R1
                        MOV -(R6),R0
                        MOV R1,(R0)

                        JMP IFEND1
                    ELSE1:
                    IFEND1:
                    """;
            statementHelper.checkCodeGen(testData, expected);
        }

        @Test
        public void termAndTest() throws FatalErrorException {
            String testData =   "if (!true && !false) {\r\n" +
                                "    i_a=3;\r\n" +
                                "}";
            String expected = """
                        MOV #0x0001,(R6)+   ;; Condition: trueを積む
                        MOV -(R6),R0        ;; NotFactor: Conditionの結果を取り出す
                        XOR #0x0001,R0      ;;            NOT演算
                        MOV R0,(R6)+        ;;            結果を積む

                        MOV #0x0000,(R6)+   ;; Condition: Falseを積む
                        MOV -(R6),R0        ;; NotFactor
                        XOR #0x0001,R0
                        MOV R0,(R6)+
                        MOV -(R6),R0        ;; TermAnd: 右の結果を取り出す
                        MOV -(R6),R1        ;;          左の結果を取り出す
                        AND R1,R0           ;;          AND演算
                        MOV R0,(R6)+        ;;          結果を積む

                        MOV -(R6),R0        ;; StatementIf
                        BRZ ELSE1

                        MOV #i_a,(R6)+      ;; i_a=3
                        MOV #3,(R6)+
                        MOV -(R6),R1
                        MOV -(R6),R0
                        MOV R1,(R0)

                        JMP IFEND1
                    ELSE1:
                    IFEND1:
                    """;
            statementHelper.checkCodeGen(testData, expected);
        }

        @Test
        public void expressionOrTest() throws FatalErrorException {
            String testData =   "while (i_a==0 || !false) {\r\n" +
                                "    i_a=3;\r\n" +
                                "}";
            String expected = """
                    WHILEBEGIN1:

                    ;; i_a==0 ORの左辺
                        MOV #i_a,(R6)+
                        MOV -(R6),R0
                        MOV (R0),(R6)+
                        MOV #0,(R6)+
                        MOV -(R6),R0
                        MOV -(R6),R1
                        MOV #0x0001,R2
                        CMP R0,R1
                        BRZ EQ1
                        CLR R2
                    EQ1:
                        MOV R2,(R6)+

                    ;; !false ORの右辺
                        MOV #0x0000,(R6)+
                        MOV -(R6),R0
                        XOR #0x0001,R0
                        MOV R0,(R6)+

                        MOV -(R6),R0        ;; ExpressionOr: 右を取り出す
                        MOV -(R6),R1        ;;               左を取り出す
                        OR R1,R0            ;;               OR演算
                        MOV R0,(R6)+        ;;               結果を積む

                        MOV -(R6),R0        ;; StatementWhile: 本体
                        BRZ WHILEEND1

                    ;; i_a=3
                        MOV #i_a,(R6)+
                        MOV #3,(R6)+
                        MOV -(R6),R1
                        MOV -(R6),R0
                        MOV R1,(R0)

                        JMP WHILEBEGIN1
                    WHILEEND1:
                    """;
            statementHelper.checkCodeGen(testData, expected);
        }

    }

    public static class ConditionBlockTest {
        CodeGenTestHelper<ConditionBlock> conditionBlockHelper = new CodeGenTestHelper<ConditionBlock>(ConditionBlock.class);
        
        @Test
        public void termAndandExpressionOrTest() throws FatalErrorException {
            //優先順位が !>&&>|| になっているか
            String testData =   "(i_a==0 || !false && i_a+3<=10)";
            String expected = """
                        MOV #i_a,(R6)+      ;; OR: 左辺EQ i_a==0
                        MOV -(R6),R0
                        MOV (R0),(R6)+
                        MOV #0,(R6)+
                        MOV -(R6),R0
                        MOV -(R6),R1
                        MOV #0x0001,R2
                        CMP R0,R1
                        BRZ EQ1
                        CLR R2
                    EQ1:
                        MOV R2,(R6)+

                        MOV #0x0000,(R6)+   ;; OR: 右辺 AND -> !false
                        MOV -(R6),R0
                        XOR #0x0001,R0
                        MOV R0,(R6)+

                        MOV #i_a,(R6)+      ;; AND: 右辺LE i_a+3<=10
                        MOV -(R6),R0
                        MOV (R0),(R6)+
                        MOV #3,(R6)+
                        MOV -(R6),R0
                        MOV -(R6),R1
                        ADD R1,R0
                        MOV R0,(R6)+  ;;i_a+3ここまで
                        MOV #10,(R6)+
                        MOV -(R6),R0
                        MOV -(R6),R1
                        MOV #0x0001,R2
                        CMP R0,R1
                        BRN LE1
                        BRZ LE1
                        CLR R2
                    LE1:
                        MOV R2,(R6)+

                        MOV -(R6),R0    ;; TermAND本体
                        MOV -(R6),R1
                        AND R1,R0
                        MOV R0,(R6)+

                        MOV -(R6),R0    ;; OR: 右(AND)の結果を取り出す
                        MOV -(R6),R1    ;;     左(EQ)の結果を取り出す
                        OR R1,R0
                        MOV R0,(R6)+
                    """;
            conditionBlockHelper.checkCodeGen(testData, expected);
        }
        
        @Test
        public void conditionUnsignedFactorTest() throws FatalErrorException {
            //優先順位が変更されているか
            String testData =   "([i_a==0 || false] && i_a+3<=10)";
            String expected = """
                        MOV #i_a,(R6)+      ;; OR: 左辺EQ i_a==0
                        MOV -(R6),R0
                        MOV (R0),(R6)+
                        MOV #0,(R6)+
                        MOV -(R6),R0
                        MOV -(R6),R1
                        MOV #0x0001,R2
                        CMP R0,R1
                        BRZ EQ1
                        CLR R2
                    EQ1:
                        MOV R2,(R6)+
                        
                        MOV #0x0000,(R6)+   ;; OR: 右辺 false

                        MOV -(R6),R0        ;; OR本体
                        MOV -(R6),R1
                        OR R1,R0
                        MOV R0,(R6)+

                        MOV #i_a,(R6)+      ;; AND: 右辺LE i_a+3<=10
                        MOV -(R6),R0
                        MOV (R0),(R6)+
                        MOV #3,(R6)+
                        MOV -(R6),R0
                        MOV -(R6),R1
                        ADD R1,R0
                        MOV R0,(R6)+
                        MOV #10,(R6)+
                        MOV -(R6),R0
                        MOV -(R6),R1
                        MOV #0x0001,R2
                        CMP R0,R1
                        BRN LE1
                        BRZ LE1
                        CLR R2
                    LE1:
                        MOV R2,(R6)+

                        MOV -(R6),R0        ;; AND本体: 右(LE)を取り出す
                        MOV -(R6),R1        ;;          左(OR)を取り出す
                        AND R1,R0
                        MOV R0,(R6)+
                    """;
            conditionBlockHelper.checkCodeGen(testData, expected);
        }
    }

}