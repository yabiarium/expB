package lang.c.parse;

import lang.*;
import lang.c.*;

public class Statement extends CParseRule {
	// 新しく非終端記号に対応するクラスを作成する際は，必ず拡張BNF をコメントでつけること
	// また，更新する際は，拡張BNFの「履歴」を残すこと（例えば，実験３まで：．．．． と 実験４から：．．． のように）
	CParseRule statementXX;

	public Statement(CParseContext pcx) {
		super("Statement");
		//setBNF("statement ::= statementAssign | statementInput | statementOutput"); //CV05~
		//setBNF("statement ::= statementAssign | statementInput | statementOutput | statementIf | statementWhile | statementBlock"); //CV07~
		setBNF("statement ::= statementAssign | statementInput | statementOutput | statementIf | statementWhile | statementBlock | statementCall | statementReturn"); //CV12~
	}

	public static boolean isFirst(CToken tk) {
		return StatementInput.isFirst(tk) 		//input
				|| StatementOutput.isFirst(tk) 	//output
				|| StatementIf.isFirst(tk) 		//if
				|| StatementWhile.isFirst(tk) 	//while
				|| StatementBlock.isFirst(tk) 	// {
				|| StatementCall.isFirst(tk) 	//call
				|| StatementReturn.isFirst(tk)	//return
				|| StatementAssign.isFirst(tk) ; //上記以外
	}

	public void parse(CParseContext pcx) throws FatalErrorException {
		// ここにやってくるときは、必ずisFirst()が満たされている
		CTokenizer ct = pcx.getTokenizer();
		CToken tk = ct.getCurrentToken(pcx);

		if(StatementInput.isFirst(tk)){
			statementXX = new StatementInput(pcx);

		}else if(StatementOutput.isFirst(tk)){
			statementXX = new StatementOutput(pcx);

		}else if(StatementIf.isFirst(tk)){ //if
			statementXX = new StatementIf(pcx);

		}else if(StatementWhile.isFirst(tk)){ //while
			statementXX = new StatementWhile(pcx);

		}else if(tk.getType() == CToken.TK_LCUR){ // {
			statementXX = new StatementBlock(pcx);
		
		}else if(StatementCall.isFirst(tk)){ //call
			statementXX = new StatementCall(pcx);
		
		}else if(StatementReturn.isFirst(tk)){ //return
			statementXX = new StatementReturn(pcx);
			
		}else{
			statementXX = new StatementAssign(pcx);
		}
		statementXX.parse(pcx);
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
		if (statementXX != null) {
			statementXX.semanticCheck(pcx);
			this.setCType(statementXX.getCType()); // statementXX の型をそのままコピー
			this.setConstant(statementXX.isConstant());
		}
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
		CodeGenCommon cgc = pcx.getCodeGenCommon();
		cgc.printStartComment(getBNF(getId()));
		if (statementXX != null) {
			statementXX.codeGen(pcx);
		}
		cgc.printCompleteComment(getBNF(getId()));
	}
}