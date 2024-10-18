package lang.c.parse;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

import lang.FatalErrorException;
import lang.c.testhelpter.CodeGenTestHelper;

@RunWith(Enclosed.class)
public class T02_51CodeGenTest {

    public static class FactorTest {
        CodeGenTestHelper<Factor> factorHelper = new CodeGenTestHelper<Factor>(Factor.class);

        @Test
        public void factor0() throws FatalErrorException {
            String testData = "&2";
            // ここに miniCompiler の実行結果を貼り付けるのはNG行為
            String expected = """
                        ;;; 予測の方に自分が予測コードを書く際に必要となる情報等は記入して良い
                        MOV #2, (R6)+   ;; Number で出力
                    """;
            // ↑ """の横位置が先頭と考える．コード・擬似コードは先頭に空白やタブが必要なことを思い出そう
            factorHelper.checkCodeGen(testData, expected);
        }
    }

    public static class ExpressionTest {
        CodeGenTestHelper<Expression> expressionHelper = new CodeGenTestHelper<Expression>(Expression.class);

        @Test
        public void expression() throws FatalErrorException {
            String testData = "&3-1-&1";
            // ここに miniCompiler の実行結果を貼り付けるのはNG行為
            String expected = """
                    ;; &3-1
                        MOV	#3, (R6)+
                        MOV	#1, (R6)+
                        MOV	-(R6), R1  ;; ここから ExpressionSub
                        MOV	-(R6), R0
                        SUB	R1, R0
                        MOV	R0, (R6)+  ;; ここまで ExpressionSub
                    ;; &3-1 -&1
                        MOV	#1, (R6)+
                        MOV	-(R6), R1  ;; ここから ExpressionSub
                        MOV	-(R6), R0
                        SUB	R1, R0
                        MOV	R0, (R6)+  ;; ここまで ExpressionSub
                    """;
            // ↑ """の横位置が先頭と考える．この場合 ADD や MOV が """" と同じか前にあると，その命令が先頭で先頭に空白やタブがない判定になる
            expressionHelper.checkCodeGen(testData, expected);
        }
    }

    public static class ProgramTest {

        CodeGenTestHelper<Program> programHelper = new CodeGenTestHelper<Program>(Program.class);

        @Test
        public void program0() throws FatalErrorException {
            String testData = "1+2-3+4-5";  // 実装を間違えると，+4-5 が認識できずエラーとなる
            // ここに miniCompiler の実行結果を貼り付けるのはテストの実施方法としてNG
            // この期待コードを提示してもらって説明してもらうのでコメントを沢山記載しておこう
            String expected = """
                        .=0x0100
                        JMP __START
                    __START:
                        MOV #0x1000,R6

                    ;;ここからexpression4()のテストの結果
                    ;; 1 +2
                        MOV	#1, (R6)+  ;; Number が出力
                        MOV	#2, (R6)+  ;; Number が出力
                        MOV	-(R6), R0  ;; ここから ExpressionAdd
                        MOV	-(R6), R1
                        ADD	R1, R0
                        MOV	R0, (R6)+  ;; ここまで ExpressionAdd
                    ;; 1+2 -3
                        MOV	#3, (R6)+  ;; Number が出力
                        MOV	-(R6), R1  ;; ここから ExpressionSub
                        MOV	-(R6), R0
                        SUB	R1, R0
                        MOV	R0, (R6)+  ;; ここまで ExpressionSub
                    ;; 1+2-3 +4
                        MOV	#4, (R6)+  ;; Number が出力
                        MOV	-(R6), R0  ;; ここから ExpressionAdd
                        MOV	-(R6), R1
                        ADD	R1, R0
                        MOV	R0, (R6)+  ;; ここまで ExpressionAdd
                    ;; 1+2-3+4 -5
                        MOV	#5, (R6)+  ;; Number が出力
                        MOV	-(R6), R1  ;; ここから ExpressionSub
                        MOV	-(R6), R0
                        SUB	R1, R0
                        MOV	R0, (R6)+  ;; ここまで ExpressionSub
                    
                    ;; これで 式 が終了
                        MOV -(R6), R0   ;; pop R0
                        HLT
                        .end
                    """;
            // ↑ """の横位置が先頭と考える．つまりこの行の""" と __START: の横位置が一致していないとテストが失敗する
            // ラベルは行先頭から書かないといけないこと．コード・擬似コードは先頭に空白やタブが必要なことを思い出そう

            // Check only code portion, not validate comments
            programHelper.checkCodeGen(testData, expected);
        }
    }
}
