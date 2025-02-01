package lang.c.parse;

import lang.*;
import lang.c.*;

import java.util.ArrayList;
import java.util.List;

public class DeclBlock extends CParseRule {

    CParseRule declaration, statement;
    List<CParseRule> declarationList = new ArrayList<>();
    //List<CParseRule> statementList = new ArrayList<>();
    List<StatementInfo> statementList = new ArrayList<>(); //CV12~
    int variableSize = 0;
    boolean isExistReturn = false;
    String functionName, returnLabel;
    CToken functionToken;

    private class StatementInfo {
        public CParseRule statement;
        public boolean isReturn;
        public CToken token;

        public StatementInfo(CParseRule statement, boolean isReturn, CToken token) {
            this.statement = statement;
            this.isReturn = isReturn;
            this.token = token;
        }
    }
    
    //CV12: 引数にfunctionNameを追加(この節点DeclBlockが呼び出されるのはfunctionからのみなので問題ないはず…)
    public DeclBlock(CParseContext pcx, String functionName, CToken functionToken) {
        super("DeclBlock");
        this.functionName = functionName;
        this.functionToken = functionToken;
        this.returnLabel = "RET_" + functionName;
        setBNF("declBlock ::= LCUR { declaration } { statement } RCUR"); //CV11~
    }

    public static boolean isFirst(CToken tk) {
        return tk.getType() == CToken.TK_LCUR;
    }

    public void parse(CParseContext pcx) throws FatalErrorException {
		CTokenizer ct = pcx.getTokenizer();
		CToken tk = ct.getCurrentToken(pcx);
        pcx.getSymbolTable().setupLocalSymbolTable(); // 局所変数用の記号表を作成
		tk = ct.getNextToken(pcx); // {を読み飛ばす

		while (Declaration.isFirst(tk)) {
            declaration = new Declaration(pcx);
            declaration.parse(pcx);
			declarationList.add(declaration);
			tk = ct.getCurrentToken(pcx);
		}

		while (Statement.isFirst(tk)) {
            boolean isReturn = false;
            if(tk.getType() == CToken.TK_RETURN){ //今から読む行がreturnの場合
                isReturn = true;
                isExistReturn = true;
            }
            statement = new Statement(pcx, functionName);
            statement.parse(pcx);
            statementList.add(new StatementInfo(statement, isReturn, tk));
			tk = ct.getCurrentToken(pcx);
		}

		
        if (tk.getType() == CToken.TK_RCUR) {
            tk = ct.getNextToken(pcx);
        }else{
            pcx.warning(tk + " declBlock: } を補いました");
        }
        //ct.getNextToken(pcx); // ifは次の字句を読んでしまうのでそれに合わせる
		
		variableSize = pcx.getSymbolTable().getAddressOffset();
		pcx.getSymbolTable().deleteLocalSymbolTable();
    }

    public void semanticCheck(CParseContext pcx) throws FatalErrorException {
        if (statementList != null) {

            //returnと関数の型が対応しているかの処理
            //このDeclBlockが属するfunctionの型を取得(プロトタイプ宣言の型が優先される)
            CSymbolTableEntry function = pcx.getSymbolTable().searchGlobal(functionName); //CV12: 左辺(functionのみ)
            int functionType = function.getCType().getType();
            String functinoTypeS = function.getCType().toString();
            
            try {
                if(isExistReturn){
                    if(functionType == CType.T_err){ //return文が存在するのにfunctionの型がない(err)場合
                        pcx.recoverableError(functionToken + " declBlock: 関数の型が必要です");
                    }
                }else{
                    if(functionType != CType.T_void && functionType != CType.T_err){ //return文が存在しないのにfunctionの型がある(void/err以外)場合
                        pcx.recoverableError(functionToken + " declBlock: "+functinoTypeS+"型の返り値が必要です");
                    }
                }
                
                for (StatementInfo statementInfo : statementList) {
                    boolean isReturn = statementInfo.isReturn;
                    CParseRule item = statementInfo.statement;
                    CToken tk = statementInfo.token;
                    item.semanticCheck(pcx);
                    
                    if(isReturn){ //今読んでいるのがreturn文の場合
                        int st = item.getCType().getType();
                        String sts = item.getCType().toString();
                        //functionの型がvoid/err以外で、return文の型と不一致
                        if(functionType != st && (functionType != CType.T_void && functionType != CType.T_err)){
                            pcx.recoverableError(tk + " declBlock: 関数の型["+functinoTypeS+"]と返り値の型["+sts+"]が異なります");
                        }
                        if(st != CType.T_void && functionType == CType.T_void){ //return文+式が存在するのにfunctionがvoidの場合
                            pcx.recoverableError(functionToken + " declBlock: 関数がvoid型にもかかわらず、返り値が存在します");
                        }
                    }
                }

            } catch (RecoverableErrorException e) {
                //コード生成なしのwarningとして処理
            }
		}
    }

    public void codeGen(CParseContext pcx) throws FatalErrorException {
        CodeGenCommon cgc = pcx.getCodeGenCommon();
        cgc.printStartComment(getBNF());

        cgc.printPushCodeGen("", "R4", "declBlock: 前のフレームポインタをスタックに保存");
        cgc.printInstCodeGen("", "MOV R6, R4", "declBlock: 現在のスタックポインタの位置をフレームポインタに設定");
        cgc.printInstCodeGen("", "ADD #" + variableSize + ", R6", "declBlock: 局所変数の領域を確保する");
        
        if (declarationList != null) {
			for (CParseRule item : declarationList) {
				item.codeGen(pcx);
			}
		}
        
		if (statementList != null) {
            for (StatementInfo statementInfo : statementList) {
                statementInfo.statement.codeGen(pcx);
            }
		}
        
        cgc.printCompleteComment(getBNF());
    }

}
