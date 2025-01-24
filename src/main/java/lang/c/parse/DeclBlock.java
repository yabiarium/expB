package lang.c.parse;

import lang.*;
import lang.c.*;

import java.util.ArrayList;
import java.util.List;

public class DeclBlock extends CParseRule {

    CParseRule declaration, statement;
    List<CParseRule> declarationList = new ArrayList<>();
    List<CParseRule> statementList = new ArrayList<>();
    int variableSize = 0;
    
    public DeclBlock(CParseContext pcx) {
        super("DeclBlock");
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
            statement = new Statement(pcx);
            statement.parse(pcx);
            statementList.add(statement);
			tk = ct.getCurrentToken(pcx);
		}

		
        if (tk.getType() == CToken.TK_RCUR) {
            tk = ct.getNextToken(pcx);
        }else{
            pcx.warning(tk + " DeclBlock: } を補いました");
        }
        //ct.getNextToken(pcx); // ifは次の字句を読んでしまうのでそれに合わせる
		
		variableSize = pcx.getSymbolTable().getAddressOffset();
		pcx.getSymbolTable().deleteLocalSymbolTable();
    }

    public void semanticCheck(CParseContext pcx) throws FatalErrorException {
        if (statementList != null) {
			for (CParseRule item : statementList) {
				item.semanticCheck(pcx);
			}
		}
    }

    public void codeGen(CParseContext pcx) throws FatalErrorException {
        CodeGenCommon cgc = pcx.getCodeGenCommon();
        cgc.printStartComment(getBNF());

        cgc.printPushCodeGen("", "R4", "declBlock: 前のフレームポインタをスタックに保存");
        cgc.printInstCodeGen("", "MOV R6, R4", "declBlock: 現在のスタックポインタの位置をフレームポインタに設定");
        cgc.printInstCodeGen("", "ADD #" + variableSize + ", R6", "declItem: 局所変数の領域を確保する");
        
        if (declarationList != null) {
			for (CParseRule item : declarationList) {
				item.codeGen(pcx);
			}
		}
        
		if (statementList != null) {
			for (CParseRule item : statementList) {
				item.codeGen(pcx);
			}
		}
        cgc.printInstCodeGen("", "MOV R4, R6", "declItem: スタックポインタを戻す(局所変数のスコープを外す)");
        cgc.printPopCodeGen("", "R4", "declBlock: 前のフレームポインタを復元");

        cgc.printCompleteComment(getBNF());
    }

}
