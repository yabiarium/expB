package lang.c.parse;

import lang.*;
import lang.c.*;

public class StatementCall extends CParseRule {
    //返り値を取得しないcall

    CParseRule ident;
    String functionName;
    CToken sem;

    public StatementCall(CParseContext pcx) {
        super("StatementCall");
		//setBNF("statementCall ::= CALL ident LPAR RPAR SEMI"); //CV12~
        setBNF("statementCall ::= CALL ident LPAR [ expression { COMMA expression } ] RPAR SEMI"); //CV13~
    }

    public static boolean isFirst(CToken tk) {
        return tk.getType() == CToken.TK_CALL;
	}

    public void parse(CParseContext pcx) throws FatalErrorException {
        CTokenizer ct = pcx.getTokenizer();
        CToken tk = ct.getNextToken(pcx); // call を読み飛ばす

        try {
            if(Ident.isFirst(tk)) {
                functionName = tk.getText();
                sem = tk;
                ident = new Ident(pcx);
                ident.parse(pcx);
                tk = ct.getCurrentToken(pcx); // identの後の(を読む
            }else {
                pcx.recoverableError(tk + " statementCall: 識別子(ident)がありません");
            }

            if(tk.getType() == CToken.TK_LPAR) {
                tk = ct.getNextToken(pcx); // (を読み飛ばす
            }else {
                pcx.warning(tk + " statementCall: ( を補いました");
            }

            if(tk.getType() == CToken.TK_RPAR) {
                tk = ct.getNextToken(pcx); // )を読み飛ばす
            }else {
                pcx.warning(tk + " statementCall: ) を補いました");
            }

            if(tk.getType() == CToken.TK_SEMI) {
                tk = ct.getNextToken(pcx); // ;を読み飛ばす, 正常終了
            }else {
                pcx.warning(tk + " statementCall: ; を補いました");
            }
            
        } catch (RecoverableErrorException e) {
            // ; まで読み飛ばす
            ct.skipTo(pcx, CToken.TK_SEMI);
            tk = ct.getNextToken(pcx);
        }
    }

    public void semanticCheck(CParseContext pcx) throws FatalErrorException {
        try {
            if(ident != null){
                ident.semanticCheck(pcx);
                CSymbolTableEntry function = pcx.getSymbolTable().searchGlobal(functionName);
                if(function == null || !function.isFunction()){ //グローバル変数として登録されていないか、登録されているが関数でない場合
                    pcx.recoverableError(sem + " statementCall: この変数は関数ではありません");
                }
            }
        } catch (RecoverableErrorException e) {
        }
        
    }

    public void codeGen(CParseContext pcx) throws FatalErrorException {
        CodeGenCommon cgc = pcx.getCodeGenCommon();
		cgc.printStartComment(getBNF(getId()));

        if(ident != null){
            ident.codeGen(pcx);
        }

		cgc.printCompleteComment(getBNF(getId()));
    }
}
