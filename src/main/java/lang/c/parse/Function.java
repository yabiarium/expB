package lang.c.parse;

import lang.*;
import lang.c.*;

public class Function extends CParseRule {

    CParseRule declBlock, argList;
    String functionName;
	boolean isExistMult = false;
	boolean isVoid = false;
	String returnLabel = ""; // 返り値のラベル
    int functionType;
    CToken func;

    public Function(CParseContext pcx) {
        super("Function");
		//setBNF("function ::= FUNC ( INT [ MULT ] | VOID ) IDENT LPAR RPAR declBlock"); //CV12~
        setBNF("function ::= FUNC ( INT [ MULT ] | VOID ) IDENT LPAR [ argList ] RPAR declblock"); //CV13~
    }

    public static boolean isFirst(CToken tk) {
        return tk.getType() == CToken.TK_FUNC;
	}

    public void parse(CParseContext pcx) throws FatalErrorException {
        pcx.getSymbolTable().setupLocalSymbolTable(); // 局所変数用の記号表を作成
        CTokenizer ct = pcx.getTokenizer();
        CToken tk = ct.getNextToken(pcx); // func を読み飛ばす
        func = tk; //declBlocでのエラー表示に使用

        if(tk.getType() == CToken.TK_INT) {
            tk = ct.getNextToken(pcx); // int を読み飛ばす
            if(tk.getType() == CToken.TK_MULT) {
                isExistMult = true;
                tk = ct.getNextToken(pcx); // * を読み飛ばす
            }
        }else if(tk.getType() == CToken.TK_VOID) {
            isVoid = true;
            tk = ct.getNextToken(pcx); // void を読み飛ばす
        }else{
            pcx.warning(tk + " function: 返り値の型を指定してください"); //指定が抜けている場合はerr型とする
        }

        try {
            if(tk.getType() == CToken.TK_IDENT) {
                functionName = tk.getText();
                registerFunction(pcx, tk);
                returnLabel = "RET_" + functionName;
                tk = ct.getNextToken(pcx); // IDENTを読み飛ばす
            }else {
                pcx.recoverableError(tk + " function: 識別子(IDENT)がありません");
            }
        } catch (RecoverableErrorException e) {
            // (, { まで読み飛ばす
            ct.skipTo(pcx, CToken.TK_LPAR, CToken.TK_RPAR, CToken.TK_LCUR);
            tk = ct.getCurrentToken(pcx);
        }

        if(tk.getType() == CToken.TK_LPAR) {
            tk = ct.getNextToken(pcx); // ( を読み飛ばす
        }else {
            pcx.warning(tk + " function: ( を補いました");
        }

        try {
            argList = new ArgList(pcx, functionName); //引数がない関数の場合でもArgListの意味解析(プロトタイプ宣言との一致)はしたいのでインスタンスだけ作成しておく
            if(ArgList.isFirst(tk)){
                argList.parse(pcx);
                tk = ct.getCurrentToken(pcx);
            }
        } catch (RecoverableErrorException e) {
            // argListのparseで回復エラーが出た場合の処理
            // {(DeclBlockの開始記号)か、Funcまで飛ばす
            ct.skipTo(pcx, CToken.TK_LCUR, CToken.TK_FUNC);
        }
        

        if(tk.getType() == CToken.TK_RPAR) {
            tk = ct.getNextToken(pcx); // ) を読み飛ばす
        }else {
            pcx.warning(tk + " function: ) を補いました");
        }

        try {
            if(DeclBlock.isFirst(tk)) { // {
                declBlock = new DeclBlock(pcx, functionName, func);
                declBlock.parse(pcx);
            }else {
                pcx.recoverableError(tk + " function: declBlock( { )がありません");
            }

        } catch (RecoverableErrorException e) {
            // funcまで読み飛ばす
            ct.skipTo(pcx, CToken.TK_FUNC);
        }
    }

    private void registerFunction(CParseContext pcx, CToken tk) throws FatalErrorException {
		// 変数登録
		CSymbolTableEntry entry;
		final boolean isConst = true;
		final boolean isFunction = true;
		int size = 1;
		if (isVoid) {
            functionType = CType.T_void;
		} else if (isExistMult) {
			functionType = CType.T_pint;
		} else {
			functionType = CType.T_int;
		}
        entry = new CSymbolTableEntry(CType.getCType(functionType), size, isConst, isFunction);

		if (!pcx.getSymbolTable().registerGlobal(functionName, entry) && !pcx.getSymbolTable().searchGlobal(functionName).verificateFunction(entry)) {
			pcx.recoverableError(tk + " function: 同じ識別子の関数があります");
		}
	}

    public void semanticCheck(CParseContext pcx) throws FatalErrorException {
        //プロトタイプ宣言の型を取得
        CSymbolTableEntry protFunction = pcx.getSymbolTable().searchGlobal(functionName);
        int protFunctionType = protFunction.getCType().getType();
        String protFunctinoTypeS = protFunction.getCType().toString();

        try {
            if(functionType != protFunctionType){ //プロトタイプ宣言と実装時で型が不一致
                pcx.recoverableError(func + " function: 宣言時の型["+protFunctinoTypeS+"]と異なります");
            }
            if(!protFunction.isFunction()){ //関数でない識別子に実装しようとしている
                pcx.recoverableError(func + " function: この識別子は関数として宣言されていません");
            }
        } catch (RecoverableErrorException e) {
            //コード生成なしのwarningとして処理
        }
        
        argList.semanticCheck(pcx);
        declBlock.semanticCheck(pcx);
    }

    public void codeGen(CParseContext pcx) throws FatalErrorException {
        CodeGenCommon cgc = pcx.getCodeGenCommon();
		cgc.printStartComment(getBNF(getId()));

        cgc.printLabel(functionName+":", "Function: 関数ラベルを作成");
        //局所変数領域の確保コードはdeclBlockで生成する（LocalSymbolTableの作成と削除をで行っているdeclBlockでしか領域をいくつ確保すればいいのか算出できないため。）
        if(declBlock != null){
            declBlock.codeGen(pcx);
        }
        cgc.printLabel(returnLabel+":", "Function: 関数の終了処理");
        cgc.printInstCodeGen("", "MOV R4, R6", "Function: スタックポインタを戻す(局所変数のスコープを外す)");
        cgc.printPopCodeGen("", "R4", "Function: 前のフレームポインタを復元");
        cgc.printInstCodeGen("", "RET", "Function: 呼び出し元へ戻る");

		cgc.printCompleteComment(getBNF(getId()));
    }
}
