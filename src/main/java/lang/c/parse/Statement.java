package lang.c.parse;

import lang.*;
import lang.c.*;

public class Statement extends CParseRule {
	// 新しく非終端記号に対応するクラスを作成する際は，必ず拡張BNF をコメントでつけること
	// また，更新する際は，拡張BNFの「履歴」を残すこと（例えば，実験３まで：．．．． と 実験４から：．．． のように）
	CParseRule statementAssign, statementInput, statementOutput;

	public Statement(CParseContext pcx) {
		super("Statement");
		//setBNF("statement ::= statementAssign | statementInput | statementOutput"); //CV05~
		setBNF("statement ::= statementAssign | statementInput | statementOutput | statementIf | statementWhile | statementBlock"); //CV07~
	}

	public static boolean isFirst(CToken tk) {
		if(tk.getType() == CToken.TK_INPUT){ // input
			return StatementInput.isFirst(tk);
		}else if(tk.getType() == CToken.TK_OUTPUT){ // output
			return StatementOutput.isFirst(tk);
		}else{ //上記以外
			return StatementAssign.isFirst(tk);
		}
	}

	public void parse(CParseContext pcx) throws FatalErrorException {
		// ここにやってくるときは、必ずisFirst()が満たされている
		CTokenizer ct = pcx.getTokenizer();
		CToken tk = ct.getCurrentToken(pcx);

		if(StatementInput.isFirst(tk)){
			statementInput = new StatementInput(pcx);
			statementInput.parse(pcx);
		}else if(StatementOutput.isFirst(tk)){
			statementOutput = new StatementOutput(pcx);
			statementOutput.parse(pcx);
		}else{
			statementAssign = new StatementAssign(pcx);
			statementAssign.parse(pcx);
		}
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
		if (statementInput != null) {
			statementInput.semanticCheck(pcx);
			this.setCType(statementInput.getCType()); // statementInput の型をそのままコピー
			this.setConstant(statementInput.isConstant());
		}
		if (statementOutput != null) {
			statementOutput.semanticCheck(pcx);
			this.setCType(statementOutput.getCType()); // statementOutput の型をそのままコピー
			this.setConstant(statementOutput.isConstant());
		}
		if(statementAssign != null){
			statementAssign.semanticCheck(pcx);
			this.setCType(statementAssign.getCType());
			this.setConstant(statementAssign.isConstant());
		}
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
		CodeGenCommon cgc = pcx.getCodeGenCommon();
		cgc.printStartComment(getBNF(getId()));
		if (statementInput != null) {
			statementInput.codeGen(pcx);
		}
		if (statementOutput != null) {
			statementOutput.codeGen(pcx);
		}
		if (statementAssign != null){
			statementAssign.codeGen(pcx);
		}
		cgc.printCompleteComment(getBNF(getId()));
	}
}