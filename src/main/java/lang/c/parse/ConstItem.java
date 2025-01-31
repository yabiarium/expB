package lang.c.parse;

import lang.*;
import lang.c.*;

public class ConstItem extends CParseRule {

	String identName, num;
	CToken ident;
	boolean isExistMult = false; // *があるか
	boolean isExistAmp = false; // &があるか
	boolean isGlobal;
	CSymbolTableEntry entry;

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
		CToken col = tk; //宣言済みの変数のエラー表示で使用する

		try {
			if(tk.getType() == CToken.TK_MULT){
				tk = ct.getNextToken(pcx); // *を読み飛ばす
				isExistMult = true;
			}
			
			if(tk.getType() != CToken.TK_IDENT){
				pcx.recoverableError(tk + " constItem: *の後ろは IDENT です"); //先頭がIDENTならisFirstでチェックしているのでここには来ないため、このエラーメッセージでよい
			}
			ident = tk;
			identName = tk.getText();
			tk = ct.getNextToken(pcx); // IDENTを読み飛ばす

			if(tk.getType() == CToken.TK_ASSIGN){
				tk = ct.getNextToken(pcx); // =を読み飛ばす
			}else{
				if(tk.getType() == CToken.TK_AMP || tk.getType() == CToken.TK_NUM){
					pcx.warning(tk + " constItem: =を補いました");
				}else{
					pcx.recoverableError(tk + " constItem: =がありません");
				}
			}
			
			if(tk.getType() != CToken.TK_AMP && isExistMult){ // *しか存在しない場合はポインタ型とする
				isExistAmp = true;
				isExistMult = true;
				pcx.warning(tk + " constItem: & を補いました");
			}else if(tk.getType() == CToken.TK_AMP && !isExistMult){ // &しか存在しない場合はポインタ型とする
				isExistAmp = true;
				isExistMult = true;
				pcx.warning(tk + " constItem: * を補いました");
				tk = ct.getNextToken(pcx); // &を読み飛ばす
			}else if(tk.getType() == CToken.TK_AMP && isExistMult){ // *と&がセットで存在する(正常)
				isExistAmp = true;
				tk = ct.getNextToken(pcx); // &を読み飛ばす
			}
			
			if(tk.getType() == CToken.TK_NUM){
				num = tk.getText();
			}else{
				pcx.recoverableError(tk + " constItem: 定数の初期化がありません");	
			}
			tk = ct.getNextToken(pcx); // NUMを読み飛ばす

		} catch (RecoverableErrorException e) {
			// 処理はint/constDeclに託す
		}


		// 変数登録
		final boolean isConst = true;
		if (isExistMult) {
			entry = new CSymbolTableEntry(CType.getCType(CType.T_pint), 1, isConst);
		} else {
			entry = new CSymbolTableEntry(CType.getCType(CType.T_int), 1, isConst);
		}

		isGlobal = pcx.getSymbolTable().isGlobalMode();
		if (isGlobal) {
			if ( !pcx.getSymbolTable().registerGlobal(identName, entry) ) {
				pcx.warning(col + " constItem: 既に宣言されています");
			}
		} else{
			if ( !pcx.getSymbolTable().registerLocal(identName, entry) ) {
				pcx.warning(col + " constItem: 既に宣言されています");
			}
		}
		
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
		CodeGenCommon cgc = pcx.getCodeGenCommon();

		cgc.printStartComment(getBNF());
		if(isGlobal){
			cgc.printLabel(identName + ":	.word " + num, "constItem: 初期値を入れる");
		}else{
			cgc.printInstCodeGen("", "MOV #" + entry.getAddress() + ", R0", "constItem: 局所変数のフレームポインタからの相対位置を取得 " + ident);
			cgc.printInstCodeGen("", "ADD R4, R0", "Ident: 局所変数の格納番地を計算する " + ident);
			cgc.printPushCodeGen("", "R0", "Ident: 格納番地を積む " + ident);
		}
		
		cgc.printCompleteComment(getBNF());
    }
}
