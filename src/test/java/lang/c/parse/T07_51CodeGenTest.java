package lang.c.parse;

import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

import lang.FatalErrorException;
import lang.c.testhelpter.CodeGenTestHelper;

@RunWith(Enclosed.class)
public class T07_51CodeGenTest {

    public static class StatementIfTest {
        CodeGenTestHelper<Statement> statementHelper = new CodeGenTestHelper<Statement>(Statement.class);

        @Test
        public void StatementIfTest1() throws FatalErrorException {
            String testData =   "if (false) {\r\n" +
                                "    i_a=3;\r\n" +
                                "}";
            String expected = """
                        MOV #0x0000,(R6)+   ;; Condition: falseを積む
                        MOV -(R6), R0       ;; StatementIf: Conditionの結果を取り出す(falseならZフラグが立つ)
                        BRZ ELSE1

                        ;; i_a=3のコード
                        MOV #i_a,(R6)+      ;; 左辺 Ident
                        MOV #3,(R6)+        ;; 右辺 Number
                        MOV -(R6),R1        ;; StatementAssign
                        MOV -(R6),R0
                        MOV R1,(R0)
                        
                        JMP IFEND1
                    ELSE1:                  ;; elseの処理内容はなし
                    IFEND1:
                    """;
            statementHelper.checkCodeGen(testData, expected);
        }

        @Test
        public void StatementIfTest2() throws FatalErrorException {
            String testData =   "if (true) {\r\n" +
                                "    i_a=3;\r\n" +
                                "}";
            String expected = """
                        MOV #0x0001,(R6)+   ;; Condition: trueを積む
                        MOV -(R6), R0       ;; StatementIf: Conditionの結果を取り出す(falseならZフラグが立つ)
                        BRZ ELSE1

                        MOV #i_a,(R6)+      ;; 左辺 Ident
                        MOV #3,(R6)+        ;; 右辺 Number
                        MOV -(R6),R1        ;; StatementAssign
                        MOV -(R6),R0
                        MOV R1,(R0)
                        
                        JMP IFEND1
                    ELSE1:                  ;; elseの処理内容はなし
                    IFEND1:
                    """;
            statementHelper.checkCodeGen(testData, expected);
        }


        @Test
        public void StatementIfTest3() throws FatalErrorException {
            String testData =   "if (true) {\r\n" +
                                "   i_a=1;\r\n" +
                                "} else {\r\n" +
                                "   i_a=2;\r\n" +
                                "}";
            String expected = """
                        MOV #0x0001,(R6)+   ;; Condition: trueを積む
                        MOV -(R6), R0       ;; StatementIf: Conditionの結果を取り出す(falseならZフラグが立つ)
                        BRZ ELSE1

                        MOV #i_a,(R6)+      ;; 左辺 Ident
                        MOV #1,(R6)+        ;; 右辺 Number
                        MOV -(R6),R1        ;; StatementAssign
                        MOV -(R6),R0
                        MOV R1,(R0)
                        
                        JMP IFEND1
                    ELSE1:
                        MOV #i_a,(R6)+      ;; 左辺 Ident
                        MOV #2,(R6)+        ;; 右辺 Number
                        MOV -(R6),R1        ;; StatementAssign
                        MOV -(R6),R0
                        MOV R1,(R0)
                    IFEND1:
                    """;
            statementHelper.checkCodeGen(testData, expected);
        }


        @Test
        public void StatementIfTest4() throws FatalErrorException {
            String testData =   "if (i_a == 3) {\r\n" + //
                                "   i_a=0;\r\n" + //
                                "} else if (i_a == 4){\r\n" + //
                                "   i_a=1;\r\n" + //
                                "} else {\r\n" + //
                                "   i_a=2;\r\n" + //
                                "}";
            String expected = """
                        MOV #i_a,(R6)+      ;; 左辺 Ident
                        MOV -(R6),R0        ;; AddressToValue
                        MOV (R0),(R6)+
                        MOV #3,(R6)+        ;; 右辺 Number
                        MOV -(R6),R0        ;; EQ
                        MOV -(R6),R1
                        MOV #0x0001,R2      ;; EQ: trueを積む
                        CMP R0,R1
                        BRZ EQ1
                        CLR R2
                    EQ1:
                        MOV R2,(R6)+

                        MOV -(R6),R0        ;; StatementIf: Conditionの結果を取り出す(falseならZフラグが立つ)
                        BRZ ELSE1

                    ;; if (i_a == 3){ ここの処理 i_a=0; }
                        MOV #i_a,(R6)+      ;; 左辺 Ident
                        MOV #0,(R6)+        ;; 右辺 Number
                        MOV -(R6),R1        ;; StatementAssign
                        MOV -(R6),R0
                        MOV R1,(R0)

                        JMP IFEND1
                    ELSE1:                  ;; ↓ i_a==4の判定
                        MOV #i_a,(R6)+      ;; EQ->左辺 Ident
                        MOV -(R6),R0        ;; AddressToValue
                        MOV (R0),(R6)+
                        MOV #4,(R6)+        ;; 右辺 Number

                        MOV -(R6),R0        ;; EQ
                        MOV -(R6),R1
                        MOV #0x0001,R2      ;; EQ: trueを積む
                        CMP R0,R1
                        BRZ EQ2
                        CLR R2
                    EQ2:
                        MOV R2,(R6)+
                                            ;; ELSEのstatement2->statementIfへ
                        MOV -(R6),R0        ;; StatementIf: Conditionの結果を取り出す(falseならZフラグが立つ)
                        BRZ ELSE2

                    ;; if (i_a == 4){ ここの処理 i_a=1; }
                        MOV #i_a,(R6)+      ;; 左辺 Ident
                        MOV #1,(R6)+        ;; 右辺 Number
                        MOV -(R6),R1        ;; StatementAssign
                        MOV -(R6),R0
                        MOV R1,(R0)

                        JMP IFEND2
                    ELSE2:
                        MOV #i_a,(R6)+      ;; i_a=2
                        MOV #2,(R6)+
                        MOV -(R6),R1
                        MOV -(R6),R0
                        MOV R1,(R0)
                    IFEND2:
                    IFEND1:
                    """;
            statementHelper.checkCodeGen(testData, expected);
        }


        @Test
        public void StatementIfTest5() throws FatalErrorException {
            // 単独ステートメントが使えるような実装の人はこれも試すこと
            String testData =   "if (true) i_a=1;\r\n" +
                                "else i_b=1;";
            String expected = """
                        MOV #0x0001,(R6)+       ;; Condition: trueを積む
                        MOV -(R6),R0            ;; StatementIf: Conditionの結果を取り出す(falseならZフラグが立つ)
                        BRZ ELSE1

                        MOV #i_a,(R6)+      ;; i_a=1 の処理
                        MOV #1,(R6)+
                        MOV -(R6),R1
                        MOV -(R6),R0
                        MOV R1,(R0)

                        JMP IFEND1
                    ELSE1:
                        MOV #i_b,(R6)+      ;; i_b=1 の処理
                        MOV #1,(R6)+
                        MOV -(R6),R1
                        MOV -(R6),R0
                        MOV R1,(R0)
                    IFEND1:
                    """;
            statementHelper.checkCodeGen(testData, expected);
        }


        @Test
        public void StatementIfTest6() throws FatalErrorException {
            // 単独ステートメントを使えるように実装したなら，これも実行できるはず
            String testData =  "if (true) if (false) if (true) i_a=1; ";
            String expected = """
                        MOV #0x0001,(R6)+   ;; Condition: trueを積む
                        MOV -(R6),R0        ;; StatementIf: Conditionの結果を取り出す(falseならZフラグが立つ)
                        BRZ ELSE1
                        MOV #0x0000,(R6)+   ;; Condition: falseを積む
                        MOV -(R6),R0        ;; StatementIf: Conditionの結果を取り出す(falseならZフラグが立つ)
                        BRZ ELSE2
                        MOV #0x0001,(R6)+   ;; Condition: trueを積む
                        MOV -(R6),R0        ;; StatementIf: Conditionの結果を取り出す(falseならZフラグが立つ)
                        BRZ ELSE3

                        MOV #i_a,(R6)+      ;; i_a=1 の処理
                        MOV #1,(R6)+
                        MOV -(R6),R1
                        MOV -(R6),R0
                        MOV R1,(R0)

                        JMP IFEND3      ;; 一番内側(3つ目)のif文が終了
                    ELSE3:                  ;; elseの処理内容はなし
                    IFEND3:
                        JMP IFEND2
                    ELSE2:                  ;; elseの処理内容はなし

                    IFEND2:
                        JMP IFEND1
                    ELSE1:                  ;; elseの処理内容はなし
                    IFEND1:
                    """;
            statementHelper.checkCodeGen(testData, expected);
        }
    }


    public static class ProgramIfTest {
        CodeGenTestHelper<Program> programHelper = new CodeGenTestHelper<Program>(Program.class);

        @Test
        public void ifTest7() throws FatalErrorException {
            String testData =   "i_a = 54;\r\n" + //
                                "if (i_a == 3) {\r\n" + //
                                "   i_a=0;\r\n" + //
                                "} else if (i_a == 4){\r\n" + //
                                "   i_a=1;\r\n" + //
                                "} else if (i_a ==54){\r\n" + //
                                "   i_a=2;\r\n" + //
                                "} else {\r\n" + //
                                "   i_a=3;\r\n" + //
                                "}";
            String expected = """
                        .=0x0100
                        JMP __START
                    i_a:.word 100
                    i_b:.word 200
                    i_c:.word 250
                    ip_d:.word 0x0103
                    ip_e:.word 0x0109
                    ia_f:.blkw 4
                    ipa_g:.blkw 4
                    c_h:.word 400

                    __START:
                        MOV #0x1000,R6

                    ;; i_a = 54
                        MOV #i_a,(R6)+      ;; Ident
                        MOV #54,(R6)+       ;; Number
                        MOV -(R6),R1        ;; StatementAssign
                        MOV -(R6),R0
                        MOV R1,(R0)
                    ;; i_a == 3             ;; StatementIf -> conditionBlock
                        MOV #i_a,(R6)+      ;; EQ -> 左辺:Ident
                        MOV -(R6),R0        ;; AddressToValue
                        MOV (R0),(R6)+
                        MOV #3,(R6)+        ;; 右辺:Number
                        MOV -(R6),R0        ;; EQ本体
                        MOV -(R6),R1
                        MOV #0x0001,R2
                        CMP R0,R1
                        BRZ EQ1
                        CLR R2
                    EQ1:
                        MOV R2,(R6)+        ;; EQ終わり

                        MOV -(R6),R0        ;; StatementIf本体
                        BRZ ELSE1

                    ;; i_a=0
                        MOV #i_a,(R6)+
                        MOV #0,(R6)+
                        MOV -(R6),R1
                        MOV -(R6),R0
                        MOV R1,(R0)

                        JMP IFEND1
                    ELSE1:
                    ;; i_a == 4             ;; StatementIf2つ目
                        MOV #i_a,(R6)+      ;; EQ -> 左辺:Ident
                        MOV -(R6),R0        ;; AddressToValue
                        MOV (R0),(R6)+
                        MOV #4,(R6)+        ;; 右辺:Number
                        MOV -(R6),R0        ;; EQ本体
                        MOV -(R6),R1
                        MOV #0x0001,R2
                        CMP R0,R1
                        BRZ EQ2
                        CLR R2
                    EQ2:
                        MOV R2,(R6)+        ;; EQ終わり

                        MOV -(R6),R0        ;; StatementIf本体
                        BRZ ELSE2

                    ;; i_a=1
                        MOV #i_a,(R6)+
                        MOV #1,(R6)+
                        MOV -(R6),R1
                        MOV -(R6),R0
                        MOV R1,(R0)

                        JMP IFEND2
                    ELSE2:
                    ;; i_a == 54            ;; StatementIf3つ目
                        MOV #i_a,(R6)+
                        MOV -(R6),R0
                        MOV (R0),(R6)+
                        MOV #54,(R6)+
                        MOV -(R6),R0
                        MOV -(R6),R1
                        MOV #0x0001,R2
                        CMP R0,R1
                        BRZ EQ3
                        CLR R2
                    EQ3:
                        MOV R2,(R6)+

                        MOV -(R6),R0        ;; StatementIf本体
                        BRZ ELSE3

                    ;; i_a=2
                        MOV #i_a,(R6)+
                        MOV #2,(R6)+
                        MOV -(R6),R1
                        MOV -(R6),R0
                        MOV R1,(R0)

                        JMP IFEND3
                    ELSE3:
                    ;; i_a=3
                        MOV #i_a,(R6)+
                        MOV #3,(R6)+
                        MOV -(R6),R1
                        MOV -(R6),R0
                        MOV R1,(R0)

                    IFEND3:
                    IFEND2:
                    IFEND1:

                        MOV -(R6),R0
                        HLT
                        .end
                    """;
            programHelper.checkCodeGen(testData, expected);
        }


        @Test
        public void ifTest8() throws FatalErrorException {
            String testData =   "if (false) {\r\n" + //
                                "    i_a=3;\r\n" + //
                                "}\r\n" + //
                                "\r\n" + //
                                "if (false) {\r\n" + //
                                "    i_a=3;\r\n" + //
                                "}\r\n" + //
                                "\r\n" + //
                                "if (true) {\r\n" + //
                                "   i_a=1;\r\n" + //
                                "} else {\r\n" + //
                                "   i_a=2;\r\n" + //
                                "}";
            String expected = """
                        .=0x0100
                        JMP __START
                    i_a:.word 100
                    i_b:.word 200
                    i_c:.word 250
                    ip_d:.word 0x0103
                    ip_e:.word 0x0109
                    ia_f:.blkw 4
                    ipa_g:.blkw 4
                    c_h:.word 400

                    __START:
                        MOV #0x1000,R6

                        MOV #0x0000,(R6)+   ;; Condition
                        MOV -(R6),R0        ;; StatementIf
                        BRZ ELSE1

                    ;; i_a=3
                        MOV #i_a,(R6)+
                        MOV #3,(R6)+
                        MOV -(R6),R1
                        MOV -(R6),R0
                        MOV R1,(R0)

                        JMP IFEND1
                    ELSE1:                  ;; elseの処理なし
                    IFEND1:

                        MOV #0x0000,(R6)+   ;; Condition
                        MOV -(R6),R0        ;; StatementIf
                        BRZ ELSE2

                    ;; i_a=3
                        MOV #i_a,(R6)+
                        MOV #3,(R6)+
                        MOV -(R6),R1
                        MOV -(R6),R0
                        MOV R1,(R0)

                        JMP IFEND2
                    ELSE2:                  ;; elseの処理なし
                    IFEND2:

                        MOV #0x0001,(R6)+   ;; Condition
                        MOV -(R6),R0        ;; StatementIf
                        BRZ ELSE3
                    
                    ;; i_a=1
                        MOV #i_a,(R6)+
                        MOV #1,(R6)+
                        MOV -(R6),R1
                        MOV -(R6),R0
                        MOV R1,(R0)

                        JMP IFEND3
                    ELSE3:
                    ;; i_a=2
                        MOV #i_a,(R6)+
                        MOV #2,(R6)+
                        MOV -(R6),R1
                        MOV -(R6),R0
                        MOV R1,(R0)
                    IFEND3:

                        MOV -(R6),R0
                        HLT
                        .end
                    """;
            programHelper.checkCodeGen(testData, expected);
        }

    }


    public static class StatementWhileTest {
        CodeGenTestHelper<Statement> statementHelper = new CodeGenTestHelper<Statement>(Statement.class);

        @Test
        public void StatementWhileTest1() throws FatalErrorException {
            String testData =   "while (i_b == 1) {\r\n" + //
                                "    input i_a;\r\n" + //
                                "    i_b = 4;\r\n" + //
                                "}";
            String expected = """
                    WHILEBEGIN1:            ;; StatementWhile: While条件式判定前
                        MOV #i_b,(R6)+      ;; Condition -> 左辺:Ident
                        MOV -(R6),R0
                        MOV (R0),(R6)+
                        MOV #1,(R6)+        ;; 右辺
                        MOV -(R6),R0
                        MOV -(R6),R1
                        MOV #0x0001,R2
                        CMP R0,R1
                        BRZ EQ1
                        CLR R2
                    EQ1:
                        MOV R2,(R6)+        ;; EQ終わり

                        MOV -(R6),R0
                        BRZ WHILEEND1

                    ;; input i_a
                        MOV #i_a,(R6)+
                        MOV #0xFFE0,R1
                        MOV -(R6),R0
                        MOV (R1),(R0)
                    ;; i_b=4
                        MOV #i_b,(R6)+
                        MOV #4,(R6)+
                        MOV -(R6),R1
                        MOV -(R6),R0
                        MOV R1,(R0)
                        
                        JMP WHILEBEGIN1
                    WHILEEND1:
                    """;
            statementHelper.checkCodeGen(testData, expected);
        }


        @Test
        public void StatementWhileTest2() throws FatalErrorException {
            String testData =   "while (true) {\r\n" + //
                                "   input i_a;\r\n" + //
                                "   while (false) {\r\n" + //
                                "      output i_a;\r\n" + //
                                "   }\r\n" + //
                                "   i_a=4;\r\n" + //
                                "}";
            String expected = """
                    WHILEBEGIN1:
                        MOV #0x0001,(R6)+       ;; Condition trueを積む
                        MOV -(R6),R0
                        BRZ WHILEEND1

                    ;; input i_a
                        MOV #i_a,(R6)+
                        MOV #0xFFE0,R1
                        MOV -(R6),R0
                        MOV (R1),(R0)

                    WHILEBEGIN2:                ;; StatementWhile2ここから
                        MOV #0x0000,(R6)+
                        MOV -(R6),R0
                        BRZ WHILEEND2

                    ;; output i_a
                        MOV #i_a,(R6)+
                        MOV -(R6),R0
                        MOV (R0),(R6)+
                        MOV -(R6),R1
                        MOV #0xFFE0,R0
                        MOV R1,(R0)

                        JMP WHILEBEGIN2
                    WHILEEND2:                  ;; StatementWhile2ここまで

                    ;; i_a=4
                        MOV #i_a,(R6)+
                        MOV #4,(R6)+
                        MOV -(R6),R1
                        MOV -(R6),R0
                        MOV R1,(R0)

                        JMP WHILEBEGIN1
                    WHILEEND1:
                    """;
            statementHelper.checkCodeGen(testData, expected);
        }


        @Test
        public void StatementWhileTest3() throws FatalErrorException {
            String testData =   "while(true) i_c=1;";
            String expected = """
                    WHILEBEGIN1:
                        MOV #0x0001,(R6)+
                        MOV -(R6),R0
                        BRZ WHILEEND1

                    ;; i_c=1
                        MOV #i_c,(R6)+
                        MOV #1,(R6)+
                        MOV -(R6),R1
                        MOV -(R6),R0
                        MOV R1,(R0)

                        JMP WHILEBEGIN1
                    WHILEEND1:
                    """;
            statementHelper.checkCodeGen(testData, expected);
        }


        @Test
        public void StatementWhileTest4() throws FatalErrorException {
            String testData =   "while(false) while(true) while(true) input i_a;";
            String expected = """
                    WHILEBEGIN1:                ;; While1ここから
                        MOV #0x0000,(R6)+
                        MOV -(R6),R0
                        BRZ WHILEEND1

                    WHILEBEGIN2:                ;; While2ここから
                        MOV #0x0001,(R6)+
                        MOV -(R6),R0
                        BRZ WHILEEND2

                    WHILEBEGIN3:                ;; While3ここから
                        MOV #0x0001,(R6)+
                        MOV -(R6),R0
                        BRZ WHILEEND3
                    
                    ;; input i_a
                        MOV #i_a,(R6)+
                        MOV #0xFFE0,R1
                        MOV -(R6),R0
                        MOV (R1),(R0)

                        JMP WHILEBEGIN3
                    WHILEEND3:                  ;; While3ここまで

                        JMP WHILEBEGIN2
                    WHILEEND2:                  ;; While2ここまで

                        JMP WHILEBEGIN1
                    WHILEEND1:                  ;; While1ここまで
                    """;
            statementHelper.checkCodeGen(testData, expected);
        }
    }


    public static class ProgramWhileTest {
        CodeGenTestHelper<Program> programHelper = new CodeGenTestHelper<Program>(Program.class);

        @Test
        public void whileTest5() throws FatalErrorException {
            String testData =   "i_b = 1;\r\n" + //
                                "\r\n" + //
                                "while (i_b == 1) {\r\n" + //
                                "    input i_a;\r\n" + //
                                "    i_b = 4;\r\n" + //
                                "}\r\n" + //
                                "\r\n" + //
                                "while (true) {\r\n" + //
                                "   input i_a;\r\n" + //
                                "   while (false) {\r\n" + //
                                "      output i_a;\r\n" + //
                                "   }\r\n" + //
                                "   i_a=4;\r\n" + //
                                "}";
            String expected = """
                        .=0x0100
                        JMP __START
                    i_a:.word 100
                    i_b:.word 200
                    i_c:.word 250
                    ip_d:.word 0x0103
                    ip_e:.word 0x0109
                    ia_f:.blkw 4
                    ipa_g:.blkw 4
                    c_h:.word 400

                    __START:
                        MOV #0x1000,R6

                    ;; i_b=1
                        MOV #i_b,(R6)+
                        MOV #1,(R6)+
                        MOV -(R6),R1
                        MOV -(R6),R0
                        MOV R1,(R0)

                    WHILEBEGIN1:            ;; while1ここから
                        MOV #i_b,(R6)+      ;; ConditionEQ -> 左辺:Ident
                        MOV -(R6),R0
                        MOV (R0),(R6)+
                        MOV #1,(R6)+        ;; 右辺:Number
                        MOV -(R6),R0
                        MOV -(R6),R1
                        MOV #0x0001,R2
                        CMP R0,R1
                        BRZ EQ1
                        CLR R2
                    EQ1:
                        MOV R2,(R6)+

                        MOV -(R6),R0        ;; StatementWhile1
                        BRZ WHILEEND1

                    ;; input i_a
                        MOV #i_a,(R6)+
                        MOV #0xFFE0,R1
                        MOV -(R6),R0
                        MOV (R1),(R0)
                    ;; i_b=4
                        MOV #i_b,(R6)+
                        MOV #4,(R6)+
                        MOV -(R6),R1
                        MOV -(R6),R0
                        MOV R1,(R0)

                        JMP WHILEBEGIN1
                    WHILEEND1:              ;; while1ここまで

                    WHILEBEGIN2:            ;; while2ここから
                        MOV #0x0001,(R6)+   ;; Condition trueを積む
                        MOV -(R6),R0        ;; StatementWhile2
                        BRZ WHILEEND2
                    
                    ;; input i_a
                        MOV #i_a,(R6)+
                        MOV #0xFFE0,R1
                        MOV -(R6),R0
                        MOV (R1),(R0)

                    WHILEBEGIN3:            ;; while3ここから
                        MOV #0x0000,(R6)+   ;; Condition falseを積む
                        MOV -(R6),R0        ;; StatementWhile3
                        BRZ WHILEEND3

                    ;; output i_a
                        MOV #i_a,(R6)+
                        MOV -(R6),R0
                        MOV (R0),(R6)+
                        MOV -(R6),R1
                        MOV #0xFFE0,R0
                        MOV R1,(R0)

                        JMP WHILEBEGIN3
                    WHILEEND3:              ;; while3ここまで

                    ;; i_a=4
                        MOV #i_a,(R6)+
                        MOV #4,(R6)+
                        MOV -(R6),R1
                        MOV -(R6),R0
                        MOV R1,(R0)

                        JMP WHILEBEGIN2
                    WHILEEND2:              ;; while2ここまで

                        MOV -(R6),R0
                        HLT
                        .end
                    """;
            programHelper.checkCodeGen(testData, expected);
        }
    }

}