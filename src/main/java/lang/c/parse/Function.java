package lang.c.parse;

import lang.*;
import lang.c.*;

public class Function extends CParseRule {

    CParseRule declBlock;

    public Function(CParseContext pcx) {
        super("Function");
		setBNF("function ::= FUNC ( INT [ MULT ] | VOID ) IDENT LPAR RPAR declBlock"); //CV12~
    }

    public static boolean isFirst(CToken tk) {
        return tk.getType() == CToken.TK_FUNC;
	}

    public void parse(CParseContext pcx) throws FatalErrorException {
        CTokenizer ct = pcx.getTokenizer();
        CToken tk = ct.getNextToken(pcx); // func を読み飛ばす

        if(tk.getType() == CToken.TK_INT) {
            tk = ct.getNextToken(pcx); // int を読み飛ばす
            if(tk.getType() == CToken.TK_MULT) {
                tk = ct.getNextToken(pcx); // * を読み飛ばす
            }
        }else if(tk.getType() == CToken.TK_VOID) {
            tk = ct.getNextToken(pcx); // void を読み飛ばす
        }else{
            pcx.warning(tk + " function: 返り値の型を指定してください"); //指定が抜けている場合はerr型とする
        }

        try {
            if(tk.getType() == CToken.TK_IDENT) {
                tk = ct.getNextToken(pcx); // IDENTを読み飛ばす
            }else {
                pcx.recoverableError(tk + " function: 識別子(IDENT)がありません");
            }
        } catch (RecoverableErrorException e) {
            // (, ), { まで読み飛ばす
            ct.skipTo(pcx, CToken.TK_LPAR, CToken.TK_RPAR, CToken.TK_LCUR);
            tk = ct.getCurrentToken(pcx);
        }

        if(tk.getType() == CToken.TK_LPAR) {
            tk = ct.getNextToken(pcx); // ( を読み飛ばす
        }else {
            pcx.warning(tk + " function: ( を補いました");
        }

        if(tk.getType() == CToken.TK_RPAR) {
            tk = ct.getNextToken(pcx); // ) を読み飛ばす
        }else {
            pcx.warning(tk + " function: ) を補いました");
        }

        try {
            if(DeclBlock.isFirst(tk)) { // {
                declBlock = new DeclBlock(pcx);
                declBlock.parse(pcx);
            }else {
                pcx.recoverableError(tk + " function: declBlock( { )がありません");
            }

        } catch (RecoverableErrorException e) {
            // funcまで読み飛ばす
            ct.skipTo(pcx, CToken.TK_FUNC);
        }
    }

    public void semanticCheck(CParseContext pcx) throws FatalErrorException {
    }

    public void codeGen(CParseContext pcx) throws FatalErrorException {
    }
}
