package lang.c.parse;

import lang.*;
import lang.c.*;

public class DeclItem extends CParseRule {

	int size;
	String identName;
	boolean isExistMult = false; // *があるか
	boolean isArray = false; // 配列か
	boolean isGlobal;

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
		CToken col = tk; //宣言済みの変数のエラー表示で使用する

		try {
			if(tk.getType() == CToken.TK_MULT){
				tk = ct.getNextToken(pcx); // *を読み飛ばす
				isExistMult = true;
			}
			
			if(tk.getType() != CToken.TK_IDENT){
				pcx.recoverableError(tk + " declItem: *の後ろは IDENT です"); //先頭がIDENTならisFirstでチェックしているのでここには来ないため、このエラーメッセージでよい
			}
			identName = tk.getText();
			tk = ct.getNextToken(pcx); // IDENTを読み飛ばす

			if(tk.getType() == CToken.TK_LBRA){
				isArray = true;
				tk = ct.getNextToken(pcx); // [を読み飛ばす

				if(tk.getType() == CToken.TK_NUM){
					size = Integer.valueOf(tk.getText());
				}else{
					pcx.recoverableError(tk + " declItem: 配列の要素数がありません");
				}
				tk = ct.getNextToken(pcx); // NUMを読み飛ばす
				
				if(tk.getType() == CToken.TK_RBRA){
					tk = ct.getNextToken(pcx); // ]を読む, 正常終了
				}else{
					pcx.warning(tk + " declItem: ] を補いました");
				}
			}

		} catch (RecoverableErrorException e) {
			// 処理はint/constDeclに託す
		}
		

		// 変数登録
		CSymbolTableEntry entry;
		final boolean isConst = false;
		if (isArray) {
			if (isExistMult) {
				entry = new CSymbolTableEntry(CType.getCType(CType.T_pint_array), size, isConst);
			} else {
				entry = new CSymbolTableEntry(CType.getCType(CType.T_int_array), size, isConst);
			}
		} else {
			size = 1;
			if (isExistMult) {
				entry = new CSymbolTableEntry(CType.getCType(CType.T_pint), size, isConst);
			} else {
				entry = new CSymbolTableEntry(CType.getCType(CType.T_int), size, isConst);
			}
		}

		try {
			if ( !pcx.getSymbolTable().registerLocal(identName, entry) ) {
				pcx.recoverableError(col + " declItem: 既に宣言されています"); //コード生成をしないwarningとして扱う
			}
		} catch (RecoverableErrorException e) {
		}
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
		//CodeGenCommon cgc = pcx.getCodeGenCommon();
    }
}
