package lang.c.parse;

import lang.*;
import lang.c.*;

public class VoidDecl extends CParseRule {

    CParseRule typeList;

    public VoidDecl(CParseContext pcx) {
        super("VoidDecl");
		//setBNF("voidDecl ::= VOID IDENT LPAR RPAR { COMMA IDENT LPAR RPAR } SEMI"); //CV12~
        setBNF("voidDecl ::= VOID IDENT LPAR [ typeList ] RPAR { COMMA IDENT LPAR [ typeList ] RPAR } SEMI"); //CV13~
    }

    public static boolean isFirst(CToken tk) {
        return tk.getType() == CToken.TK_VOID;
	}

    public void parse(CParseContext pcx) throws FatalErrorException {
        CTokenizer ct = pcx.getTokenizer();
        CToken tk = ct.getNextToken(pcx); // void を読み飛ばす

        try {
            do{
                if (tk.getType() == CToken.TK_COMMA) {
                    tk = ct.getNextToken(pcx); // , を読み飛ばす
                }

                if(tk.getType() == CToken.TK_IDENT) {
                    registerName(pcx, tk);
                    tk = ct.getNextToken(pcx); // IDENTを読み飛ばす
                }else {
                    pcx.recoverableError(tk + " voidDecl: 識別子(IDENT)がありません");
                }
    
                if(tk.getType() == CToken.TK_LPAR) {
                    tk = ct.getNextToken(pcx); // ( を読み飛ばす
                }else {
                    pcx.warning(tk + " voidDecl: ( を補いました");
                }

                if(TypeList.isFirst(tk)){
                    typeList = new TypeList(pcx);
                    typeList.parse(pcx);
                }else{
                    pcx.recoverableError(tk + " voidDecl: 型がありません");  //,はあるのに引数の型指定が続いていない
                }
    
                if(tk.getType() == CToken.TK_RPAR) {
                    tk = ct.getNextToken(pcx); // ) を読み飛ばす
                }else {
                    pcx.warning(tk + " voidDecl: ) を補いました");
                }
            }while(tk.getType() == CToken.TK_COMMA);

            if(tk.getType() == CToken.TK_SEMI) {
                tk = ct.getNextToken(pcx); // ; を読み飛ばす
            }else {
                pcx.warning(tk + " voidDecl: ; を補いました");
            }
            
        } catch (RecoverableErrorException e) {
            // ; まで読み飛ばす ( ,やIDENTまで読み飛ばして次のプロトタイプ宣言から解析再開するのが理想だが、
            //                   引数の中にも,やIDENTがあり中途半端な読み飛ばしになる可能性があるため、諦めて一気に;まで読み飛ばす )
            ct.skipTo(pcx, CToken.TK_SEMI);
            tk = ct.getNextToken(pcx);
        }
    }

    private void registerName(CParseContext pcx, CToken tk) throws RecoverableErrorException {
		String name = tk.getText();
		CSymbolTableEntry entry = new CSymbolTableEntry(CType.getCType(CType.T_void), 1, true, true);
		if ( !pcx.getSymbolTable().registerGlobal(name, entry) ) {
			pcx.warning(tk + " voidDecl: 既に宣言されています");
		}
	}

    public void semanticCheck(CParseContext pcx) throws FatalErrorException {
    }

    public void codeGen(CParseContext pcx) throws FatalErrorException {
        CodeGenCommon cgc = pcx.getCodeGenCommon();
		cgc.printStartComment(getBNF(getId()));
		cgc.printCompleteComment(getBNF(getId()));
    }
}
