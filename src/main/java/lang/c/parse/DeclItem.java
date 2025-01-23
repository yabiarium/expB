package lang.c.parse;

import lang.*;
import lang.c.*;

public class DeclItem extends CParseRule {

	public DeclItem(CParseContext pcx) {
		super("DeclItem");
		setBNF("declItem ::= [ MULT ] IDENT [ LBRA NUM RBRA ]"); //CV10~
	}

	public static boolean isFirst(CToken tk) {
		return tk.getType() == CToken.TK_MULT || tk.getType() == CToken.TK_IDENT;
	}

	public void parse(CParseContext pcx) throws FatalErrorException {
		CTokenizer ct = pcx.getTokenizer();
		CToken tk = ct.getCurrentToken(pcx);

		try {
			if(tk.getType() == CToken.TK_MULT){
				tk = ct.getNextToken(pcx); // *を読んだことにする
			}
			
			if(tk.getType() != CToken.TK_IDENT){
				pcx.recoverableError(tk + "declItem: *の後ろは IDENT です"); //先頭がIDENTならisFirstでチェックしているのでここには来ないため、このエラーメッセージでよい
			}
	
			tk = ct.getNextToken(pcx); // IDENTを読む
			if(tk.getType() == CToken.TK_LBRA){
				tk = ct.getNextToken(pcx); // [を読む
				if(tk.getType() != CToken.TK_NUM){
					pcx.recoverableError(tk + "declItem: 配列の要素数がありません");
				}
				tk = ct.getNextToken(pcx); // NUMを読む
				if(tk.getType() == CToken.TK_RBRA){
					tk = ct.getNextToken(pcx); // ]を読む, 正常終了
				}else{
					pcx.warning(tk + "declItem: ] を補いました");
				}
			}

		} catch (RecoverableErrorException e) {
			// 処理はint/constDeclに託す
		}
		
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
		//CodeGenCommon cgc = pcx.getCodeGenCommon();
    }
}
