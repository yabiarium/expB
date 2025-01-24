package lang.c.parse;

import lang.*;
import lang.c.*;

public class ConstItem extends CParseRule {

	public ConstItem(CParseContext pcx) {
		super("ConstItem");
		setBNF("constItem ::= [ MULT ] IDENT ASSIGN [ AMP ] NUM"); //CV10~
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
				pcx.recoverableError(tk + " constItem: *の後ろは IDENT です"); //先頭がIDENTならisFirstでチェックしているのでここには来ないため、このエラーメッセージでよい
			}
	
			tk = ct.getNextToken(pcx); // IDENTを読む
			if(tk.getType() == CToken.TK_ASSIGN){
				tk = ct.getNextToken(pcx); // =を読む
			}else{
				if(tk.getType() == CToken.TK_AMP || tk.getType() == CToken.TK_NUM){
					pcx.warning(tk + " constItem: =を補いました");
				}else{
					pcx.recoverableError(tk + " constItem: =がありません");
				}
			}

			if(tk.getType() == CToken.TK_AMP){
				tk = ct.getNextToken(pcx); // &を読む
			}
			if(tk.getType() != CToken.TK_NUM){
				pcx.recoverableError(tk + "constItem: 定数の初期化がありません");
			}
			tk = ct.getNextToken(pcx); // NUMを読む

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
