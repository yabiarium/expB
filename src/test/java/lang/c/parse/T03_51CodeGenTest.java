package lang.c.parse;

// import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

import lang.FatalErrorException;
import lang.c.testhelpter.CodeGenTestHelper;

@RunWith(Enclosed.class)
public class T03_51CodeGenTest {

    public static class TermTest {
        CodeGenTestHelper<Term> termHelper = new CodeGenTestHelper<Term>(Term.class);

        @Test
        public void term1() throws FatalErrorException {
            String testData = "2 * 3";
            String expected = """
                        MOV #2, (R6)+   ; 引数をスタックに積む
                        MOV #3, (R6)+
                        JSR MUL         ; サブルーチン呼び出し(返り値はR0に)
                        SUB #2, R6
                        MOV R0, (R6)+   ; 結果を積む
                    """;

                    // MUL: ;引数を取り出す
                    //     SUB #1, R6      ; SPを引数の所へ
                    //     MOV -(R6), R2   ; 右(かける数)を取り出す
                    //     MOV -(R6), R1   ; 左(かけられる数)を取り出す
                    //     MOV #0, R0      ; R0を演算結果の一時保存用にリセット
                    // MUL_CALC: ;計算する
                    //     ADD R1, R0
                    //     SUB #1, R2      ; かける数-1
                    //     CMP #0, R2      
                    //     BRZ MUL_END     ; かける数が0なら繰り返しを抜ける
                    //     JMP MUL_CALC    ; かける数が0でない場合
                    // MUL_END:
                    //     ADD #3, R6      ; RET前にSPを戻り番地の所に戻す
                    //     RET ; 呼出元へ
                    // """;
            // ↑ """の横位置が先頭と考える
            termHelper.checkCodeGen(testData, expected);
        }

        @Test
        public void term2() throws FatalErrorException {
            String testData = "2 / 3";
            String expected = """
                        MOV #2, (R6)+   ; 引数をスタックに積む
                        MOV #3, (R6)+
                        JSR DIV         ; サブルーチン呼び出し(返り値はR0に)
                        SUB #2, R6
                        MOV R0, (R6)+   ; 結果を積む
                    """;
                        
                    // DIV: ;引数を取り出す
                    //     SUB #1, R6      ; SPを引数の所へ
                    //     MOV -(R6), R2   ; 右(割る数)を取り出す
                    //     MOV -(R6), R1   ; 左(わられる数)を取り出す
                    //     MOV #0, R0      ; R0を演算結果の一時保存用にリセット

                    //     CMP #0, R1      ; 割られる数と0を比較
                    //     BRZ DIV_ZERO1   ; 0なら
                    //     CMP #0, R2      ; 0でないなら、割る数と0を比較
                    //     BRZ DIV_ZERO1   ; 割る数が0ならエラー

                    // DIV_CALC: ;両方0でなければ、計算する
                    //     CMP R2, R1      ; 割られる数-割る数
                    //     BRN DIV_END     ; R2>R1(割られる数の方が小さい)なら
                    //     SUB R2, R1      ; 割られる数-割る数
                    //     ADD #1, R0      ; 商+1      
                    //     JMP DIV_CALC
                    // DIV_END:
                    //     ADD #3, R6      ; RET前にSPを戻り番地の所に戻す
                    //     RET ; 呼出元へ

                    // DIV_ZERO1: ;エラー処理用(式に0がある場合)
                    //     MOV #0, R0      ; 0を返す
                    //     JMP DIV_END
                    // """;
            // ↑ """の横位置が先頭と考える
            termHelper.checkCodeGen(testData, expected);
        }
        
    }

    public static class PlusFactorTest {
        CodeGenTestHelper<PlusFactor> plusFactorHelper = new CodeGenTestHelper<PlusFactor>(PlusFactor.class);

        @Test
        public void plusFactor() throws FatalErrorException {
            String testData = "+2";
            // ここに miniCompiler の実行結果を貼り付けるのはNG行為
            String expected = """
                        MOV	#2, (R6)+  ;; Number が出力
                    """;
            // ↑ """の横位置が先頭と考える
            plusFactorHelper.checkCodeGen(testData, expected);
        }
    }

    public static class MinusFactorTest {
        CodeGenTestHelper<MinusFactor> minusFactorHelper = new CodeGenTestHelper<MinusFactor>(MinusFactor.class);

        @Test
        public void minusFactor() throws FatalErrorException {
            String testData = "-2";
            // ここに miniCompiler の実行結果を貼り付けるのはNG行為
            String expected = """
                        MOV	#2, (R6)+
                        MOV	-(R6), R1  ;; 右を取り出す
                        MOV #0, R0     ;; 左に0を入れる
                        SUB R1, R0     ;; R0[int]からR1[int] を引く
                        MOV R0, (R6)+  ;; 演算結果R0[int]をスタックに積む
                    """;
            // ↑ """の横位置が先頭と考える
            minusFactorHelper.checkCodeGen(testData, expected);
        }
    }

    public static class UnsignedFactorTest {
        CodeGenTestHelper<UnsignedFactor> unsignedFactorHelper = new CodeGenTestHelper<UnsignedFactor>(UnsignedFactor.class);

        @Test
        public void unsignedFactor() throws FatalErrorException {
            String testData = "(1+2)";
            // ここに miniCompiler の実行結果を貼り付けるのはNG行為
            String expected = """
                        MOV #1, (R6)+
                        MOV	#2, (R6)+
                        MOV	-(R6), R0  ;; 右を取り出す
                        MOV -(R6), R1  ;; 左を取り出す
                        ADD R1, R0
                        MOV R0, (R6)+  ;; 演算結果R0をスタックに積む
                    """;
            // ↑ """の横位置が先頭と考える
            unsignedFactorHelper.checkCodeGen(testData, expected);
        }
    }


    public static class ExpressionTest {
        CodeGenTestHelper<Expression> expressionHelper = new CodeGenTestHelper<Expression>(Expression.class);

        @Test
        public void expression1() throws FatalErrorException {
            String testData = "(1+2)*3";
            // ここに miniCompiler の実行結果を貼り付けるのはNG行為
            String expected = """
                    ;; 1+2
                        MOV	#1, (R6)+  ;; Number が出力
                        MOV	#2, (R6)+  ;; Number が出力
                        MOV	-(R6), R0
                        MOV	-(R6), R1
                        ADD R1,R0
                        MOV R0,(R6)+   ;; ここまで ExpAdd
                    ;; (1+2) *3
                        MOV #3,(R6)+
                        JSR MUL
                        SUB #2, R6
                        MOV R0,(R6)+
                    """;

                    // MUL:
                    //     SUB #1,R6
                    //     MOV -(R6),R2
                    //     MOV -(R6),R1
                    //     MOV #0,R0
                    // MUL_CALC:
                    //     ADD R1,R0
                    //     SUB #1,R2
                    //     CMP #0,R2
                    //     BRZ MUL_END
                    //     JMP MUL_CALC
                    // MUL_END:
                    //     ADD #3,R6
                    //     RET
                    // """;
            // ↑ """の横位置が先頭と考える
            expressionHelper.checkCodeGen(testData, expected);
        }

        @Test
        public void expression2() throws FatalErrorException {
            String testData = "1+2*3";
            // ここに miniCompiler の実行結果を貼り付けるのはNG行為
            String expected = """
                        MOV #1, (R6)+   ;;expAdd
                        MOV #2, (R6)+   ;;facMul
                        MOV #3, (R6)+
                        JSR MUL
                        SUB #2, R6
                        MOV R0, (R6)+   ;;facMul end

                        MOV -(R6), R0
                        MOV -(R6), R1
                        ADD R1, R0
                        MOV R0,(R6)+    ;;expAdd end
                    """;

                    // MUL: ;引数を取り出す
                    //     SUB #1, R6      ; SPを引数の所へ
                    //     MOV -(R6), R2   ; 右(かける数)を取り出す
                    //     MOV -(R6), R1   ; 左(かけられる数)を取り出す
                    //     MOV #0, R0      ; R0を演算結果の一時保存用にリセット
                    // MUL_CALC: ;計算する
                    //     ADD R1, R0
                    //     SUB #1, R2      ; かける数-1
                    //     CMP #0, R2      
                    //     BRZ MUL_END     ; かける数が0なら繰り返しを抜ける
                    //     JMP MUL_CALC    ; かける数が0でない場合
                    // MUL_END:
                    //     ADD #3, R6      ; RET前にSPを戻り番地の所に戻す
                    //     RET ; 呼出元へ

                    //     MOV -(R6), R0
                    //     MOV -(R6), R1
                    //     ADD R1, R0
                    //     MOV R0,(R6)+    ;;expAdd end
                    // """;
            // ↑ """の横位置が先頭と考える
            expressionHelper.checkCodeGen(testData, expected);
        }
    
        @Test
        public void expression3() throws FatalErrorException {
            String testData = "1/(2-3)";
            // ここに miniCompiler の実行結果を貼り付けるのはNG行為
            String expected = """
                            MOV #1,(R6)+
                            MOV #2, (R6)+
                            MOV #3, (R6)+
                            MOV -(R6), R1
                            MOV -(R6), R0
                            SUB R1, R0
                            MOV R0, (R6)+
                            JSR DIV
                            SUB #2, R6
                            MOV R0, (R6)+
                        """;

                    // DIV: ;引数を取り出す
                    //     SUB #1, R6      ; SPを引数の所へ
                    //     MOV -(R6), R2   ; 右(割る数)を取り出す
                    //     MOV -(R6), R1   ; 左(わられる数)を取り出す
                    //     MOV #0, R0      ; R0を演算結果の一時保存用にリセット

                    //     CMP #0, R1      ; 割られる数と0を比較
                    //     BRZ DIV_ZERO1   ; 0なら
                    //     CMP #0, R2      ; 0でないなら、割る数と0を比較
                    //     BRZ DIV_ZERO1   ; 割る数が0ならエラー

                    // DIV_CALC: ;両方0でなければ、計算する
                    //     CMP R2, R1      ; 割られる数-割る数
                    //     BRN DIV_END     ; R2>R1(割られる数の方が小さい)なら
                    //     SUB R2, R1      ; 割られる数-割る数
                    //     ADD #1, R0      ; 商+1      
                    //     JMP DIV_CALC
                    // DIV_END:
                    //     ADD #3, R6      ; RET前にSPを戻り番地の所に戻す
                    //     RET ; 呼出元へ

                    // DIV_ZERO1: ;エラー処理用(式に0がある場合)
                    //     MOV #0, R0      ; 0を返す
                    //     JMP DIV_END
                    // """;
            // ↑ """の横位置が先頭と考える．この場合 ADD や MOV が """" と同じか前にあると，その命令が先頭で先頭に空白やタブがない判定になる
            expressionHelper.checkCodeGen(testData, expected);
        }

        @Test
        public void expression4() throws FatalErrorException {
            String testData = "1/2-3";
            // ここに miniCompiler の実行結果を貼り付けるのはNG行為
            String expected = """
                        MOV #1,(R6)+
                        MOV #2,(R6)+
                        JSR DIV
                        SUB #2, R6
                        MOV R0,(R6)+

                        MOV #3,(R6)+
                        MOV -(R6),R1
                        MOV -(R6),R0
                        SUB R1,R0
                        MOV R0,(R6)+
                    """;

                    // DIV: ;引数を取り出す
                    //     SUB #1, R6      ; SPを引数の所へ
                    //     MOV -(R6), R2   ; 右(割る数)を取り出す
                    //     MOV -(R6), R1   ; 左(わられる数)を取り出す
                    //     MOV #0, R0      ; R0を演算結果の一時保存用にリセット

                    //     CMP #0, R1      ; 割られる数と0を比較
                    //     BRZ DIV_ZERO1   ; 0なら
                    //     CMP #0, R2      ; 0でないなら、割る数と0を比較
                    //     BRZ DIV_ZERO1   ; 割る数が0ならエラー

                    // DIV_CALC: ;両方0でなければ、計算する
                    //     CMP R2, R1      ; 割られる数-割る数
                    //     BRN DIV_END     ; R2>R1(割られる数の方が小さい)なら
                    //     SUB R2, R1      ; 割られる数-割る数
                    //     ADD #1, R0      ; 商+1      
                    //     JMP DIV_CALC
                    // DIV_END:
                    //     ADD #3, R6      ; RET前にSPを戻り番地の所に戻す
                    //     RET ; 呼出元へ

                    // DIV_ZERO1: ;エラー処理用(式に0がある場合)
                    //     MOV #0, R0      ; 0を返す
                    //     JMP DIV_END

                    //     MOV #3,(R6)+
                    //     MOV -(R6),R1
                    //     MOV -(R6),R0
                    //     SUB R1,R0
                    //     MOV R0,(R6)+
                    // """;
            // ↑ """の横位置が先頭と考える．この場合 ADD や MOV が """" と同じか前にあると，その命令が先頭で先頭に空白やタブがない判定になる
            expressionHelper.checkCodeGen(testData, expected);
        }

        @Test
        public void expression5() throws FatalErrorException {
            String testData = "(1+2)*3/-(4-5)";
            // ここに miniCompiler の実行結果を貼り付けるのはNG行為
            String expected = """
                        MOV #1,(R6)+     ;;expAdd
                        MOV #2,(R6)+
                        MOV -(R6),R0
                        MOV -(R6),R1
                        ADD R1,R0
                        MOV R0,(R6)+     ;;expAdd end
                        MOV #3,(R6)+     ;;termMul
                        JSR MUL
                        SUB #2, R6
                        MOV R0,(R6)+     ;;termMul end

                        MOV #4,(R6)+    ;;expSub
                        MOV #5,(R6)+
                        MOV -(R6),R1
                        MOV -(R6),R0
                        SUB R1,R0
                        MOV R0,(R6)+    ;;expSub end
                        MOV -(R6),R1    ;;MinusFac
                        MOV #0,R0
                        SUB R1,R0
                        MOV R0,(R6)+    ;;MinusFac end
                        JSR DIV
                        SUB #2, R6
                        MOV R0,(R6)+    ;;termDiv end
                    """;

                    // MUL: ;引数を取り出す
                    //     SUB #1, R6      ; SPを引数の所へ
                    //     MOV -(R6), R2   ; 右(かける数)を取り出す
                    //     MOV -(R6), R1   ; 左(かけられる数)を取り出す
                    //     MOV #0, R0      ; R0を演算結果の一時保存用にリセット
                    // MUL_CALC: ;計算する
                    //     ADD R1, R0
                    //     SUB #1, R2      ; かける数-1
                    //     CMP #0, R2      
                    //     BRZ MUL_END     ; かける数が0なら繰り返しを抜ける
                    //     JMP MUL_CALC    ; かける数が0でない場合
                    // MUL_END:
                    //     ADD #3, R6      ; RET前にSPを戻り番地の所に戻す
                    //     RET ; 呼出元へ

                    //     MOV #4,(R6)+    ;;expSub
                    //     MOV #5,(R6)+
                    //     MOV -(R6),R1
                    //     MOV -(R6),R0
                    //     SUB R1,R0
                    //     MOV R0,(R6)+    ;;expSub end
                    //     MOV -(R6),R1    ;;MinusFac
                    //     MOV #0,R0
                    //     SUB R1,R0
                    //     MOV R0,(R6)+    ;;MinusFac end
                    //     JSR DIV
                    //     SUB #2, R6
                    //     MOV R0,(R6)+    ;;termDiv end

                    // DIV: ;引数を取り出す
                    //     SUB #1, R6      ; SPを引数の所へ
                    //     MOV -(R6), R2   ; 右(割る数)を取り出す
                    //     MOV -(R6), R1   ; 左(わられる数)を取り出す
                    //     MOV #0, R0      ; R0を演算結果の一時保存用にリセット

                    //     CMP #0, R1      ; 割られる数と0を比較
                    //     BRZ DIV_ZERO1   ; 0なら
                    //     CMP #0, R2      ; 0でないなら、割る数と0を比較
                    //     BRZ DIV_ZERO1   ; 割る数が0ならエラー

                    // DIV_CALC: ;両方0でなければ、計算する
                    //     CMP R2, R1      ; 割られる数-割る数
                    //     BRN DIV_END     ; R2>R1(割られる数の方が小さい)なら
                    //     SUB R2, R1      ; 割られる数-割る数
                    //     ADD #1, R0      ; 商+1      
                    //     JMP DIV_CALC
                    // DIV_END:
                    //     ADD #3, R6      ; RET前にSPを戻り番地の所に戻す
                    //     RET ; 呼出元へ

                    // DIV_ZERO1: ;エラー処理用(式に0がある場合)
                    //     MOV #0, R0      ; 0を返す
                    //     JMP DIV_END
                    // """;
            // ↑ """の横位置が先頭と考える
            expressionHelper.checkCodeGen(testData, expected);
        }

        @Test
        public void expression6() throws FatalErrorException {
            String testData = "+4--5++2";
            // ここに miniCompiler の実行結果を貼り付けるのはNG行為
            String expected = """
                        MOV #4,(R6)+   ;;expSub & PlusFac
                        MOV #5,(R6)+   ;;MinusFac
                        MOV -(R6),R1
                        MOV #0,R0
                        SUB R1,R0
                        MOV R0,(R6)+   ;; MinusFac end
                        MOV -(R6),R1
                        MOV -(R6),R0
                        SUB R1,R0
                        MOV R0,(R6)+   ;;expSub end
                        MOV #2,(R6)+   ;;expAdd & PlusFac
                        MOV -(R6),R0
                        MOV -(R6),R1
                        ADD R1,R0
                        MOV R0,(R6)+   ;;expAdd end
                    """;
            // ↑ """の横位置が先頭と考える．この場合 ADD や MOV が """" と同じか前にあると，その命令が先頭で先頭に空白やタブがない判定になる
            expressionHelper.checkCodeGen(testData, expected);
        }
    }

}
