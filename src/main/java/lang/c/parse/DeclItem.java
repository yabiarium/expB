package lang.c.parse;

import lang.*;
import lang.c.*;

public class DeclItem extends CParseRule {

	int size;
	String identName;
	boolean isExistMult = false; // *があるか
	boolean isArray = false; // 配列か
	boolean isFunction = false;
	boolean isGlobal;
	CParseRule typeList;

	public DeclItem(CParseContext pcx) {
		super("DeclItem");
		//setBNF("declItem ::= [ MULT ] IDENT [ LBRA NUM RBRA ]"); //CV10~
		//setBNF("declItem ::= [ MULT ] IDENT [ LBRA NUM RBRA | LPAR RPAR ]"); //CV12~
		setBNF("declItem ::= [ MULT ] IDENT [ LBRA NUM RBRA | LPAR [ typeList ] RPAR ]"); //CV13~
	}

	public static boolean isFirst(CToken tk) {
		return tk.getType() == CToken.TK_MULT || tk.getType() == CToken.TK_IDENT;
	}

	public void parse(CParseContext pcx) throws FatalErrorException {
		CTokenizer ct = pcx.getTokenizer();
		CToken tk = ct.getCurrentToken(pcx);
		CToken ident;

		try {
			if(tk.getType() == CToken.TK_MULT){
				tk = ct.getNextToken(pcx); // *を読み飛ばす
				isExistMult = true;
			}
			
			if(tk.getType() != CToken.TK_IDENT){
				pcx.recoverableError(tk + " declItem: *の後ろは IDENT です"); //先頭がIDENTならisFirstでチェックしているのでここには来ないため、このエラーメッセージでよい
			}
			identName = tk.getText();
			ident = tk;
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

			}else if(tk.getType() == CToken.TK_LPAR){
				isFunction = true;
				registerFunction(pcx, ident); //isFunctionの判定が終わってから登録する
				tk = ct.getNextToken(pcx); // (を読み飛ばす

				if(TypeList.isFirst(tk)){
					typeList = new TypeList(pcx, identName);
					typeList.parse(pcx);
					tk = ct.getCurrentToken(pcx);
				}else if(tk.getType() != CToken.TK_RPAR){
                    pcx.recoverableError(tk + " declItem: 引数が正しくありません");
                }

				if(tk.getType() == CToken.TK_RPAR){
					tk = ct.getNextToken(pcx); // )を読む, 正常終了
				}else{
					pcx.warning(tk + " declItem: ) を補いました");
				}
			}

			if(!isFunction){
				registerFunction(pcx, ident); //関数以外の変数はここで登録処理する
			}

		} catch (RecoverableErrorException e) {
			// 処理はint/constDeclで;まで飛ばすのでその手前まで処理する
			ct.skipTo(pcx, CToken.TK_COMMA ,CToken.TK_SEMI);
		}
	}

	private void registerFunction(CParseContext pcx, CToken tk) throws FatalErrorException {
		// 変数登録
		CSymbolTableEntry entry;
		final boolean isConst = false;
		int declItemType;
		if (isArray) {
			if (isExistMult) {
				declItemType = CType.T_pint_array;
			} else {
				declItemType = CType.T_int_array;
			}
		} else {
			size = 1;
			if (isExistMult) {
				declItemType = CType.T_pint;
			} else {
				declItemType = CType.T_int;
			}
		}
		entry = new CSymbolTableEntry(CType.getCType(declItemType), size, isConst, isFunction);

		
		isGlobal = pcx.getSymbolTable().isGlobalMode(); //この節点が関数内から呼ばれたか否か
		if (isGlobal || isFunction) { //グローバル領域から呼ばれた || プロトタイプ宣言である(BNF的に関数内関数は書けないので、関数の宣言はすべてグローバル変数のテーブルで管理する)
			if ( !pcx.getSymbolTable().registerGlobal(identName, entry) ) {
				pcx.warning(tk + " declItem: 既に宣言されています");
			}
		}else{
			if ( !pcx.getSymbolTable().registerLocal(identName, entry) ) {
				pcx.warning(tk + " declItem: 既に宣言されています");
			}
		}
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
		CodeGenCommon cgc = pcx.getCodeGenCommon();

		cgc.printStartComment(getBNF());

		//ローカル変数の時は代入時にスタックに積むのでこのコードは生成しない
		//関数も生成しない
		if(isGlobal && !isFunction){
			if (isArray) {
				cgc.printLabel(identName + ":	.blkw " + size, "DeclItem: 配列は要素数分確保");
			}else{
				cgc.printLabel(identName + ":	.word 0", "DeclItem: 変数は0で初期化");
			}
		}

		cgc.printCompleteComment(getBNF());
    }
}
