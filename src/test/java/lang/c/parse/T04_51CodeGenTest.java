package lang.c.parse;

// import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

import lang.FatalErrorException;
import lang.c.testhelpter.CodeGenTestHelper;

@RunWith(Enclosed.class)
public class T04_51CodeGenTest {

    public static class ExpressionTest {
        CodeGenTestHelper<Expression> expressionHelper = new CodeGenTestHelper<Expression>(Expression.class);

        @Test
        public void intTest1() throws FatalErrorException {
            String testData = "i_a";
            // 変数名から間接参照で値(100)を取り出す
            String expected = """
                        MOV #i_a, (R6)+  ;i_aのアドレスを積む
                        MOV -(R6), R0
                        MOV (R0), (R6)+  ;100 i_aの値を積む
                    """;
            // ↑ """の横位置が先頭と考える
            expressionHelper.checkCodeGen(testData, expected);
        }

        @Test
        public void intTest2() throws FatalErrorException {
            String testData = "&i_b";
            // ここに miniCompiler の実行結果を貼り付けるのはNG行為
            String expected = """
                        MOV #i_b, (R6)+  ;i_aのアドレスを積む
                    """;
            // ↑ """の横位置が先頭と考える
            expressionHelper.checkCodeGen(testData, expected);
        }
    
        @Test
        public void constIint1() throws FatalErrorException {
            String testData = "c_h";
            // ここに miniCompiler の実行結果を貼り付けるのはNG行為
            String expected = """
                            MOV #c_h, (R6)+  ;c_hのアドレスを積む
                            MOV -(R6), R0
                            MOV (R0), (R6)+  ;400 c_hの値を積む
                        """;
            // ↑ """の横位置が先頭と考える．この場合 ADD や MOV が """" と同じか前にあると，その命令が先頭で先頭に空白やタブがない判定になる
            expressionHelper.checkCodeGen(testData, expected);
        }

        @Test
        public void pointerTest1() throws FatalErrorException {
            String testData = "ip_d";
            // ここに miniCompiler の実行結果を貼り付けるのはNG行為
            String expected = """
                        MOV #ip_d, (R6)+  ;ip_dのアドレスを積む
                        MOV -(R6), R0
                        MOV (R0), (R6)+  ;0x0103 ip_dの値を積む
                    """;
            // ↑ """の横位置が先頭と考える
            expressionHelper.checkCodeGen(testData, expected);
        }

        @Test
        public void pointerTest2() throws FatalErrorException {
            String testData = "*ip_e";
            // ここに miniCompiler の実行結果を貼り付けるのはNG行為
            String expected = """
                        MOV #ip_e, (R6)+  ;ip_eのアドレスを積む
                        MOV -(R6), R0
                        MOV (R0), (R6)+  ;0x0109 ip_eの値を積む
                        MOV -(R6), R0
                        MOV (R0), (R6)+  ;0x0109の値を積む
                    """;
            // ↑ """の横位置が先頭と考える
            expressionHelper.checkCodeGen(testData, expected);
        }

        @Test
        public void arrayTest1() throws FatalErrorException {
            String testData = "ia_f[3]"; //=ia_f配列の4つ目の要素
            // ここに miniCompiler の実行結果を貼り付けるのはNG行為
            String expected = """
                    ;ident
                        MOV #ia_f, (R6)+  ;ia_fのアドレスを積む
                    ;array
                        MOV #3, (R6)+ ;expression→NUM
                        MOV -(R6), R0 ;array expressionの結果をR0に取り出す
                        MOV -(R6), R1 ;ia_fのアドレス=配列の先頭をR1に取り出す
                        ADD R1, R0  ;相対アドレスを計算
                        MOV R0, (R6)+
                    ;addressToValue
                        MOV -(R6), R0  ;ia_f[3]のアドレスを取り出す
                        MOV (R0), (R6)+  ;値を積む
                    """;
            // ↑ """の横位置が先頭と考える
            expressionHelper.checkCodeGen(testData, expected);
        }

        @Test
        public void arrayTest2() throws FatalErrorException {
            String testData = "&ia_f[3]";
            // ここに miniCompiler の実行結果を貼り付けるのはNG行為
            String expected = """
                    ;ident
                        MOV #ia_f, (R6)+  ;ia_fのアドレスを積む
                    ;array
                        MOV #3, (R6)+ ;expression→NUM
                        MOV -(R6), R0 ;array expressionの結果をR0に取り出す
                        MOV -(R6), R1 ;ia_fのアドレス=配列の先頭をR1に取り出す
                        ADD R1, R0  ;相対アドレスを計算
                        MOV R0, (R6)+
                    """;
            // ↑ """の横位置が先頭と考える
            expressionHelper.checkCodeGen(testData, expected);
        }

        @Test
        public void pointerArrayTest1() throws FatalErrorException {
            String testData = "ipa_g[3]";
            // ここに miniCompiler の実行結果を貼り付けるのはNG行為
            String expected = """
                    ;ident
                        MOV #ipa_g, (R6)+  ;ipa_gのアドレスを積む
                    ;array
                        MOV #3, (R6)+ ;expression→NUM
                        MOV -(R6), R0 ;array expressionの結果をR0に取り出す
                        MOV -(R6), R1 ;ipa_gのアドレス=配列の先頭をR1に取り出す
                        ADD R1, R0  ;相対アドレスを計算
                        MOV R0, (R6)+
                    ;addressToValue
                        MOV -(R6), R0  ;ipa_g[3]のアドレスを取り出す
                        MOV (R0), (R6)+  ;値を積む
                    """;
            // ↑ """の横位置が先頭と考える
            expressionHelper.checkCodeGen(testData, expected);
        }

        @Test
        public void pointerArrayTest2() throws FatalErrorException {
            String testData = "*ipa_g[3]";
            // ここに miniCompiler の実行結果を貼り付けるのはNG行為
            String expected = """
                    ;ident
                        MOV #ipa_g, (R6)+  ;ipa_gのアドレスを積む
                    ;array
                        MOV #3, (R6)+ ;expression→NUM
                        MOV -(R6), R0 ;array expressionの結果をR0に取り出す
                        MOV -(R6), R1 ;ipa_gのアドレス=配列の先頭をR1に取り出す
                        ADD R1, R0  ;相対アドレスを計算
                        MOV R0, (R6)+
                    ;primaryMult
                        MOV -(R6), R0  ;ipa_g[3]のアドレスを取り出す
                        MOV (R0), (R6)+  ;値を積む
                    ;addressToValue
                        MOV -(R6), R0  ;ipa_g[3]に入っていたアドレスを取り出す
                        MOV (R0), (R6)+  ;値を積む
                    """;
            // ↑ """の横位置が先頭と考える
            expressionHelper.checkCodeGen(testData, expected);
        }

    }

}
