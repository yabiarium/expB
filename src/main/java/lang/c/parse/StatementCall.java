package lang.c.parse;

import lang.*;
import lang.c.*;

public class StatementCall extends CParseRule {

    CParseRule ident;

    public StatementCall(CParseContext pcx) {
        super("StatementCall");
		setBNF("statementCall ::= CALL ident LPAR RPAR SEMI"); //CV12~
    }

    public static boolean isFirst(CToken tk) {
        return tk.getType() == CToken.TK_CALL;
	}

    public void parse(CParseContext pcx) throws FatalErrorException {
        CTokenizer ct = pcx.getTokenizer();
        CToken tk = ct.getNextToken(pcx); // call を読み飛ばす

        try {
            if(Ident.isFirst(tk)) {
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
    }

    public void codeGen(CParseContext pcx) throws FatalErrorException {
    }
}
