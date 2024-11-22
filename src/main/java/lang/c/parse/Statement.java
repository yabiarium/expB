package lang.c.parse;

import lang.*;
import lang.c.*;

public class Statement extends CParseRule {
	// 新しく非終端記号に対応するクラスを作成する際は，必ず拡張BNF をコメントでつけること
	// また，更新する際は，拡張BNFの「履歴」を残すこと（例えば，実験３まで：．．．． と 実験４から：．．． のように）
	CParseRule statementAssign, statementInput, statementOutput;

	public Statement(CParseContext pcx) {
		super("Statement");
		setBNF("statement ::= statementAssign | statementInput | statementOutput"); //CV05~
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
		if (factorAmp != null) {
			factorAmp.semanticCheck(pcx);
			this.setCType(factorAmp.getCType()); // factorAmp の型をそのままコピー
			this.setConstant(factorAmp.isConstant());
		}else if (number != null) {
			number.semanticCheck(pcx);
			this.setCType(number.getCType()); // number の型をそのままコピー
			this.setConstant(number.isConstant());
		}else if(expression != null){
			expression.semanticCheck(pcx);
			this.setCType(expression.getCType());
			this.setConstant(expression.isConstant());
		}else{
			addressToValue.semanticCheck(pcx);
			this.setCType(addressToValue.getCType());
			this.setConstant(addressToValue.isConstant());
		}
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
		CodeGenCommon cgc = pcx.getCodeGenCommon();
		cgc.printStartComment(getBNF(getId()));
		if (factorAmp != null) {
			factorAmp.codeGen(pcx);
		}else if (number != null) {
			number.codeGen(pcx);
		}else if (expression != null){
			expression.codeGen(pcx);
		}else if (addressToValue != null){
			addressToValue.codeGen(pcx);
		}
		cgc.printCompleteComment(getBNF(getId()));
	}
}