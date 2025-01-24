package lang.c.parse;

import lang.*;
import lang.c.*;

public class ConstItem extends CParseRule {

	int size;
	String identName;
	boolean isExistMult = false; // *があるか
	boolean isExistAmp = false; // &があるか
	boolean isArray = false; // 配列か
	boolean isGlobal;

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
			identName = tk.getText();
			tk = ct.getNextToken(pcx); // IDENTを読み飛ばす

			if(tk.getType() == CToken.TK_ASSIGN){
				isArray = true;
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
				size = Integer.valueOf(tk.getText());
			}else{
				pcx.recoverableError(tk + " constItem: 定数の初期化がありません");	
			}
			tk = ct.getNextToken(pcx); // NUMを読み飛ばす

		} catch (RecoverableErrorException e) {
			// 処理はint/constDeclに託す
		}


		// 変数登録
		CSymbolTableEntry entry;
		final boolean isConst = true;
		size = 1;
		if (isExistMult) {
			entry = new CSymbolTableEntry(CType.getCType(CType.T_pint), size, isConst);
		} else {
			entry = new CSymbolTableEntry(CType.getCType(CType.T_int), size, isConst);
		}

		if ( !pcx.getSymbolTable().registerLocal(identName, entry) ) {
			pcx.recoverableError(col + " 既に宣言されている変数です"); //コード生成をしないwarningとして扱う
		}
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
		//CodeGenCommon cgc = pcx.getCodeGenCommon();
    }
}
