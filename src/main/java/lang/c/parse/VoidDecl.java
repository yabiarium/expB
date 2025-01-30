package lang.c.parse;

import lang.*;
import lang.c.*;

public class VoidDecl extends CParseRule {

    public VoidDecl(CParseContext pcx) {
        super("VoidDecl");
		setBNF("voidDecl ::= VOID IDENT LPAR RPAR { COMMA IDENT LPAR RPAR } SEMI"); //CV12~
    }

    public static boolean isFirst(CToken tk) {
        return tk.getType() == CToken.TK_VOID;
	}

    public void parse(CParseContext pcx) throws FatalErrorException {
        CTokenizer ct = pcx.getTokenizer();
        CToken tk = ct.getNextToken(pcx); // void を読み飛ばす

        try {
            if(tk.getType() == CToken.TK_IDENT) {
                tk = ct.getNextToken(pcx); // IDENTを読み飛ばす
            }else {
                pcx.recoverableError(tk + " voidDecl: 識別子(IDENT)がありません");
            }

            if(tk.getType() == CToken.TK_LPAR) {
                tk = ct.getNextToken(pcx); // ( を読み飛ばす
            }else {
                pcx.warning(tk + " voidDecl: ( を補いました");
            }

            if(tk.getType() == CToken.TK_RPAR) {
                tk = ct.getNextToken(pcx); // ) を読み飛ばす
            }else {
                pcx.warning(tk + " voidDecl: ) を補いました");
            }

            while(tk.getType() == CToken.TK_COMMA) {
                tk = ct.getNextToken(pcx); // , を読み飛ばす

                if(tk.getType() == CToken.TK_IDENT) {
                    tk = ct.getNextToken(pcx); // IDENTを読み飛ばす
                }else {
                    pcx.recoverableError(tk + " voidDecl: 識別子(IDENT)がありません");
                }
    
                if(tk.getType() == CToken.TK_LPAR) {
                    tk = ct.getNextToken(pcx); // ( を読み飛ばす
                }else {
                    pcx.warning(tk + " voidDecl: ( を補いました");
                }
    
                if(tk.getType() == CToken.TK_RPAR) {
                    tk = ct.getNextToken(pcx); // ) を読み飛ばす
                }else {
                    pcx.warning(tk + " voidDecl: ) を補いました");
                }
            }

            if(tk.getType() == CToken.TK_SEMI) {
                tk = ct.getNextToken(pcx); // ; を読み飛ばす
            }else {
                pcx.warning(tk + " voidDecl: ; を補いました");
            }
            
        } catch (RecoverableErrorException e) {
            // ; まで読み飛ばす ( ,やIDENTまで読み飛ばして次のプロトタイプ宣言から解析再開するのが理想だが、
            //                   引数の中にも,やIDENTがあり中途半端な読み飛ばしになる場合があるため、諦めて一気に;まで読み飛ばす )
            ct.skipTo(pcx, CToken.TK_SEMI);
            tk = ct.getNextToken(pcx);
        }
    }

    public void semanticCheck(CParseContext pcx) throws FatalErrorException {
    }

    public void codeGen(CParseContext pcx) throws FatalErrorException {
    }
}
