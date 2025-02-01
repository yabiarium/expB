package lang.c.parse;

import lang.*;
import lang.c.*;

public class Function extends CParseRule {

    CParseRule declBlock;
    String functionName;
	boolean isExistMult = false;
	boolean isVoid = false;
	String returnLabel = ""; // 返り値のラベル
    int functionType;
    CToken func;

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
                returnLabel = "RET_" + functionName + pcx.getSeqId(functionName);
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
        
        declBlock.semanticCheck(pcx);
    }

    public void codeGen(CParseContext pcx) throws FatalErrorException {
        CodeGenCommon cgc = pcx.getCodeGenCommon();
		cgc.printStartComment(getBNF(getId()));

        cgc.printLabel(functionName+":", "Function: 関数ラベルを作成");
        cgc.printPushCodeGen("", "R0", "Function: レジスタを退避させる");
        cgc.printPushCodeGen("", "R1", "Function: レジスタを退避させる");
        cgc.printPushCodeGen("", "R2", "Function: レジスタを退避させる");
        cgc.printPushCodeGen("", "R3", "Function: レジスタを退避させる");
        if(declBlock != null){
            declBlock.codeGen(pcx);
        }
        cgc.printLabel(returnLabel+":", "Function: 関数の終了処理");
        cgc.printPopCodeGen("", "R3", "Function: レジスタを復帰させる");
        cgc.printPopCodeGen("", "R2", "Function: レジスタを復帰させる");
        cgc.printPopCodeGen("", "R1", "Function: レジスタを復帰させる");
        cgc.printInstCodeGen("", "SUB #1, R6", "Function: R0には返り値が入っているので書き換えない(退避させた分が1つ残っているのでSPを1戻す)");
        cgc.printInstCodeGen("", "RET", "Function: 呼び出し元へ");

		cgc.printCompleteComment(getBNF(getId()));
    }
}
