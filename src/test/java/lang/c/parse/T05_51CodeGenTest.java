package lang.c.parse;

import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

import lang.FatalErrorException;
import lang.c.testhelpter.CodeGenTestHelper;

@RunWith(Enclosed.class)
public class T05_51CodeGenTest {

    public static class StatementAssignTest {
        CodeGenTestHelper<StatementAssign> statementAssignHelper = new CodeGenTestHelper<StatementAssign>(StatementAssign.class);

        @Test
        public void assignInt() throws FatalErrorException {
            // (1) 整数型の扱い
            String testData = "i_a=0;"; // 正当assignInt()
            String expected = """
                        MOV	#i_a, (R6)+ ;; Ident が出力
                        MOV #0,(R6)+ ;; Number が出力
                        MOV -(R6),R1 ;; StatementAssign: 右辺値取り出し
                        MOV -(R6),R0 ;; StatementAssign; 左辺番地取り出し
                        MOV	R1,(R0)  ;; StatementAssign; 代入実行
                    """;
            statementAssignHelper.checkCodeGen(testData, expected);
        }

        @Test
        public void assignPointer1() throws FatalErrorException {
            String testData = "ip_d=&1;";
            // ここに miniCompiler の実行結果を貼り付けるのはNG行為
            String expected = """
                        MOV	#ip_d, (R6)+ ;; Ident が出力
                        MOV #1,(R6)+ ;; Number が出力
                        MOV -(R6),R1 ;; StatementAssign: 右辺値取り出し
                        MOV -(R6),R0 ;; StatementAssign; 左辺番地取り出し
                        MOV	R1,(R0)  ;; StatementAssign; 代入実行
                    """;
            // ↑ """の横位置が先頭と考える．この場合 ADD や MOV が """" と同じか前にあると，その命令が先頭で先頭に空白やタブがない判定になる
            statementAssignHelper.checkCodeGen(testData, expected);
        }
        
        @Test
        public void assignPointer2() throws FatalErrorException {
            String testData = "*ip_d=1;";
            // ここに miniCompiler の実行結果を貼り付けるのはNG行為
            String expected = """
                        MOV	#ip_d, (R6)+ ;; Ident が出力
                        MOV	-(R6),R0   ;; PrimaryMult が出力
                        MOV	(R0),(R6)+ ;; PrimaryMult が出力
                        MOV #1,(R6)+ ;; Number が出力
                        MOV -(R6),R1 ;; StatementAssign: 右辺値取り出し
                        MOV -(R6),R0 ;; StatementAssign; 左辺番地取り出し
                        MOV	R1,(R0)  ;; StatementAssign; 代入実行
                    """;
            statementAssignHelper.checkCodeGen(testData, expected);
        }

        @Test
        public void assignArray() throws FatalErrorException {
            // (3) 配列型の扱い
            String testData = "ia_f[3]=1;";	// 正当 assignArray()
            String expected = """
                    ;; Ident
                        MOV #ia_f,(R6)+
                    ;; Number
                        MOV #3,(R6)+
                    ;; Array
                        MOV -(R6),R0
                        MOV -(R6),R1
                        ADD R1,R0
                        MOV R0,(R6)+
                    ;; Number が出力
                        MOV #1,(R6)+
                    ;; StatementAssign
                        MOV -(R6),R1
                        MOV -(R6),R0 
                        MOV R1,(R0)
                    """;
            statementAssignHelper.checkCodeGen(testData, expected);
        }

        @Test
        public void assignPointerArray1() throws FatalErrorException {
            // (4) ポインタ配列型の扱い
            String testData = "ipa_g[3]=&3;";	// 正当 assignPointerArray1()
            String expected = """
                    ;; Ident
                        MOV #ipa_g,(R6)+
                    ;; Number
                        MOV #3,(R6)+
                    ;; Array
                        MOV -(R6),R0
                        MOV -(R6),R1
                        ADD R1,R0
                        MOV R0,(R6)+
                    ;; Number
                        MOV #3,(R6)+
                    ;; StatementAssign
                        MOV -(R6),R1
                        MOV -(R6),R0
                        MOV R1,(R0)
                    """;
            statementAssignHelper.checkCodeGen(testData, expected);
        }

        @Test
        public void assignPointerArray2() throws FatalErrorException {
            // (4) ポインタ配列型の扱い
            String testData = "*ipa_g[3]=3;";	// 正当 assignPointerArray2()
            String expected = """
                    ;; Ident
                        MOV #ipa_g,(R6)+
                    ;; Number
                        MOV #3,(R6)+
                    ;; Array
                        MOV -(R6),R0
                        MOV -(R6),R1
                        ADD R1,R0
                        MOV R0,(R6)+
                    ;; PrimaryMult
                        MOV -(R6),R0
                        MOV (R0),(R6)+
                    ;; Number
                        MOV #3,(R6)+
                    ;; StatementAssign
                        MOV -(R6),R1
                        MOV -(R6),R0
                        MOV R1,(R0)
                    """;
            statementAssignHelper.checkCodeGen(testData, expected);
        }

    }


    public static class StatementInputTest {
        CodeGenTestHelper<StatementInput> statementInputHelper = new CodeGenTestHelper<StatementInput>(StatementInput.class);

        @Test
        public void assignInt() throws FatalErrorException {
            // (1) 整数型の扱い
            String testData = "input i_a;";	// 正当assignInt()
            String expected = """
                    ;; Ident
                        MOV #i_a,(R6)+
                    ;; StatementInput
                        MOV #0xFFE0,R1
                        MOV -(R6),R0
                        MOV (R1),(R0)
                    """;
            statementInputHelper.checkCodeGen(testData, expected);
        }

        @Test
        public void assignPointer1() throws FatalErrorException {
            // (2) ポインタ型の扱い
            String testData = "input ip_d;";	// 正当 assignPointer1()
            String expected = """
                    ;; Ident
                        MOV #ip_d,(R6)+
                    ;; StatementInput
                        MOV #0xFFE0,R1
                        MOV -(R6),R0
                        MOV (R1),(R0)
                    """;
            statementInputHelper.checkCodeGen(testData, expected);
        }
        
        @Test
        public void assignPointer2() throws FatalErrorException {
            // (2) ポインタ型の扱い
            String testData = "input *ip_e;";	// 正当 assignPointer2()
            String expected = """
                    ;; Ident
                        MOV #ip_e,(R6)+
                    ;; PrymaryMult
                        MOV -(R6),R0
                        MOV (R0),(R6)+
                    ;; StatementInput
                        MOV #0xFFE0,R1
                        MOV -(R6),R0
                        MOV (R1),(R0)
                    """;
            statementInputHelper.checkCodeGen(testData, expected);
        }

        @Test
        public void assignArray() throws FatalErrorException {
            // (3) 配列型の扱い
            String testData = "input ia_f[3];";	// 正当 assignArray()
            String expected = """
                    ;; Ident
                        MOV #ia_f,(R6)+
                    ;; Number
                        MOV #3,(R6)+
                    ;; Array
                        MOV -(R6),R0
                        MOV -(R6),R1
                        ADD R1,R0
                        MOV R0,(R6)+
                    ;; StatementInput
                        MOV #0xFFE0,R1
                        MOV -(R6),R0
                        MOV (R1),(R0)
                    """;
            statementInputHelper.checkCodeGen(testData, expected);
        }

        @Test
        public void assignPointerArray1() throws FatalErrorException {
            // (4) ポインタ配列型の扱い
            String testData = "input ipa_g[3];";	// 正当 assignPointerArray1()
            String expected = """
                    ;; Ident
                        MOV #ipa_g,(R6)+
                    ;; Number
                        MOV #3,(R6)+
                    ;; Array
                        MOV -(R6),R0
                        MOV -(R6),R1
                        ADD R1,R0
                        MOV R0,(R6)+
                    ;; StatementInput
                        MOV #0xFFE0,R1
                        MOV -(R6),R0
                        MOV (R1),(R0)
                    """;
            statementInputHelper.checkCodeGen(testData, expected);
        }

        @Test
        public void assignPointerArray2() throws FatalErrorException {
            // (4) ポインタ配列型の扱い
            String testData = "input *ipa_g[3];";	// 正当 assignPointerArray2()
            String expected = """
                    ;; Ident
                        MOV #ipa_g,(R6)+
                    ;;  Number
                        MOV #3,(R6)+
                    ;; Array
                        MOV -(R6),R0
                        MOV -(R6),R1
                        ADD R1,R0
                        MOV R0,(R6)+
                    ;; PrymaryMult
                        MOV -(R6),R0
                        MOV (R0),(R6)+
                    ;; StatementInput
                        MOV #0xFFE0,R1
                        MOV -(R6),R0
                        MOV (R1),(R0)
                    """;
            statementInputHelper.checkCodeGen(testData, expected);
        }

    }    

    public static class StatementOutputTest {
        CodeGenTestHelper<StatementOutput> statementOutputHelper = new CodeGenTestHelper<StatementOutput>(StatementOutput.class);

        @Test
        public void assignInt1() throws FatalErrorException {
            // (1) 整数型の扱い
            String testData = "output i_a;";	// 正当assignInt1()
            String expected = """
                    ;; Ident
                        MOV #i_a,(R6)+
                    ;; AddressToValue
                        MOV -(R6),R0
                        MOV (R0),(R6)+
                    ;; StatementOutput
                        MOV -(R6),R1
                        MOV #0xFFE0,R0
                        MOV R1,(R0)
                    """;
            statementOutputHelper.checkCodeGen(testData, expected);
        }

        @Test
        public void assignInt2() throws FatalErrorException {
            // (1) 整数型の扱い
            String testData = "output &i_a;";	// 正当assignInt2()
            String expected = """
                        MOV #i_a,(R6)+
                        MOV -(R6),R1
                        MOV #0xFFE0,R0
                        MOV R1,(R0)
                    """;
            statementOutputHelper.checkCodeGen(testData, expected);
        }

        @Test
        public void assignInt3() throws FatalErrorException {
            // (1) 整数型の扱い
            String testData = "output 100;";	// 正当assignInt3()
            String expected = """
                        MOV #100,(R6)+
                        MOV -(R6),R1
                        MOV #0xFFE0,R0
                        MOV R1,(R0)
                    """;
            statementOutputHelper.checkCodeGen(testData, expected);
        }

        @Test
        public void assignPointer1() throws FatalErrorException {
            // (2) ポインタ型の扱い
            String testData = "output ip_d;";	// 正当 assignPointer1()
            String expected = """
                        MOV #ip_d,(R6)+
                        MOV -(R6),R0
                        MOV (R0),(R6)+
                        MOV -(R6),R1
                        MOV #0xFFE0,R0
                        MOV R1,(R0)
                    """;
            statementOutputHelper.checkCodeGen(testData, expected);
        }
        
        @Test
        public void assignPointer2() throws FatalErrorException {
            // (2) ポインタ型の扱い
            String testData = "output *ip_e;";	// 正当 assignPointer2()
            String expected = """
                        MOV #ip_e,(R6)+
                        MOV -(R6),R0
                        MOV (R0),(R6)+
                        MOV -(R6),R0
                        MOV (R0),(R6)+
                        MOV -(R6),R1
                        MOV #0xFFE0,R0
                        MOV R1,(R0)
                    """;
            statementOutputHelper.checkCodeGen(testData, expected);
        }

        @Test
        public void assignArray1() throws FatalErrorException {
            // (3) 配列型の扱い
            String testData = "output ia_f[3];";	// 正当 assignArray1()
            String expected = """
                        MOV #ia_f,(R6)+
                        MOV #3,(R6)+
                        MOV -(R6),R0
                        MOV -(R6),R1
                        ADD R1,R0
                        MOV R0,(R6)+
                        MOV -(R6),R0
                        MOV (R0),(R6)+
                        MOV -(R6),R1
                        MOV #0xFFE0,R0
                        MOV R1,(R0)
                    """;
            statementOutputHelper.checkCodeGen(testData, expected);
        }

        @Test
        public void assignArray2() throws FatalErrorException {
            // (3) 配列型の扱い
            String testData = "output &ia_f[3];";	// 正当 assignArray2()
            String expected = """
                        MOV #ia_f,(R6)+
                        MOV #3,(R6)+
                        MOV -(R6),R0
                        MOV -(R6),R1
                        ADD R1,R0
                        MOV R0,(R6)+
                        MOV -(R6),R1
                        MOV #0xFFE0,R0
                        MOV R1,(R0)
                    """;
            statementOutputHelper.checkCodeGen(testData, expected);
        }

        @Test
        public void assignPointerArray1() throws FatalErrorException {
            // (4) ポインタ配列型の扱い
            String testData = "output ipa_g[3];";	// 正当 assignPointerArray1()
            String expected = """
                        MOV #ipa_g,(R6)+
                        MOV #3,(R6)+
                        MOV -(R6),R0
                        MOV -(R6),R1
                        ADD R1,R0
                        MOV R0,(R6)+
                        MOV -(R6),R0
                        MOV (R0),(R6)+
                        MOV -(R6),R1
                        MOV #0xFFE0,R0
                        MOV R1,(R0)
                    """;
            statementOutputHelper.checkCodeGen(testData, expected);
        }

        @Test
        public void assignPointerArray2() throws FatalErrorException {
            // (4) ポインタ配列型の扱い
            String testData = "output *ipa_g[3];";	// 正当 assignPointerArray2()
            String expected = """
                        MOV #ipa_g,(R6)+
                        MOV #3,(R6)+
                        MOV -(R6),R0
                        MOV -(R6),R1
                        ADD R1,R0
                        MOV R0,(R6)+
                        MOV -(R6),R0
                        MOV (R0),(R6)+
                        MOV -(R6),R0
                        MOV (R0),(R6)+
                        MOV -(R6),R1
                        MOV #0xFFE0,R0
                        MOV R1,(R0)
                    """;
            statementOutputHelper.checkCodeGen(testData, expected);
        }

    }

    public static class ProgramTest {
        CodeGenTestHelper<Program> ProgramHelper = new CodeGenTestHelper<Program>(Program.class);

        @Test
        public void assignInt1() throws FatalErrorException {
            // (1) 複数statementの取り扱い
            String testData = """
                        input i_a;             // 正当program1()
                        i_b = i_a + 155;
                        output i_b;
                    """;
            String expected = """
                        .=0x0100
                        JMP __START
                    i_a:  .word 100
                    i_b:  .word 200
                    i_c:  .word 250
                    ip_d:  .word 0x0103
                    ip_e:  .word 0x0109
                    ia_f:  .blkw 4
                    ipa_g:  .blkw 4
                    c_h:  .word 400
                    __START:
                        MOV #0x1000,R6

                    ;; Ident
                        MOV #i_a,(R6)+
                    ;; StatementInput
                        MOV #0xFFE0,R1
                        MOV -(R6),R0
                        MOV (R1),(R0)

                    ;; Ident
                        MOV #i_b,(R6)+
                    ;; Ident
                        MOV #i_a,(R6)+
                    ;; AddressToValue
                        MOV -(R6),R0
                        MOV (R0),(R6)+
                    ;; Number
                        MOV #155,(R6)+
                    ;; expAdd?
                        MOV -(R6),R0
                        MOV -(R6),R1
                        ADD R1,R0
                        MOV R0,(R6)+
                    ;; StatementAssign
                        MOV -(R6),R1
                        MOV -(R6),R0
                        MOV R1,(R0)

                    ;; Ident
                        MOV #i_b,(R6)+
                    ;; AddressToValue
                        MOV -(R6),R0
                        MOV (R0),(R6)+
                    ;; StatementOutput
                        MOV -(R6),R1
                        MOV #0xFFE0,R0
                        MOV R1,(R0)
                        
                        MOV -(R6),R0
                        HLT
                        .end
                    """;
            ProgramHelper.checkCodeGen(testData, expected);
        }

        @Test
        public void assignInt2() throws FatalErrorException {
            // (1) 複数statementの取り扱い
            String testData = """
                        ip_d = &i_a;           // 正当program2()
                        input *ip_d;
                        output i_a;
                    """;
            String expected = """
                        .=0x0100
                        JMP __START
                    i_a:  .word 100
                    i_b:  .word 200
                    i_c:  .word 250
                    ip_d:  .word 0x0103
                    ip_e:  .word 0x0109
                    ia_f:  .blkw 4
                    ipa_g:  .blkw 4
                    c_h:  .word 400
                    __START:
                        MOV #0x1000,R6
                    
                    ;; Ident
                        MOV #ip_d,(R6)+
                    ;; Ident
                        MOV #i_a,(R6)+
                    ;; StatementAssign
                        MOV -(R6),R1
                        MOV -(R6),R0
                        MOV R1,(R0)

                    ;; Ident
                        MOV #ip_d,(R6)+
                    ;; AddressToValue
                        MOV -(R6),R0
                        MOV (R0),(R6)+
                    ;; StatementInput
                        MOV #0xFFE0,R1
                        MOV -(R6),R0
                        MOV (R1),(R0)

                    ;; Ident
                        MOV #i_a,(R6)+
                    ;; AddressToValue
                        MOV -(R6),R0
                        MOV (R0),(R6)+
                    ;; StatementOutput
                        MOV -(R6),R1
                        MOV #0xFFE0,R0
                        MOV R1,(R0)

                        MOV -(R6),R0
                        HLT
                        .end
                    """;
            ProgramHelper.checkCodeGen(testData, expected);
        }

    }
}