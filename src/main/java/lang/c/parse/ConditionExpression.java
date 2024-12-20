package lang.c.parse;

import lang.*;
import lang.c.*;

public class ConditionExpression extends CParseRule {
	CParseRule conditionTerm, expressionOr;

	public ConditionExpression(CParseContext pcx) {
		super("ConditionExpression");
		setBNF("conditionExpression ::= conditionTerm { expressionOr }"); // CV08~
	}

	public static boolean isFirst(CToken tk) {
		return ConditionTerm.isFirst(tk);
	}

	public void parse(CParseContext pcx) throws FatalErrorException {

		conditionTerm = new ConditionTerm(pcx);
		conditionTerm.parse(pcx);

		CTokenizer ct = pcx.getTokenizer();
		CToken tk = ct.getCurrentToken(pcx);
		while (ExpressionOr.isFirst(tk)) {
			expressionOr = new ExpressionOr(pcx, conditionTerm);
			expressionOr.parse(pcx);
			tk = ct.getCurrentToken(pcx);
		}
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
		if (conditionTerm != null){
			conditionTerm.semanticCheck(pcx);
			this.setCType(conditionTerm.getCType()); // conditionTerm の型をそのままコピー
			this.setConstant(conditionTerm.isConstant());
		}
		if (expressionOr != null) {
			expressionOr.semanticCheck(pcx);
			this.setCType(expressionOr.getCType()); // expressionOr の型をそのままコピー
			this.setConstant(expressionOr.isConstant());
		}
	}


	public void codeGen(CParseContext pcx) throws FatalErrorException {
		CodeGenCommon cgc = pcx.getCodeGenCommon();
		cgc.printStartComment(getBNF(getId()));
		
		conditionTerm.codeGen(pcx);

		if (expressionOr != null) {
			expressionOr.codeGen(pcx);
		}
		cgc.printCompleteComment(getBNF(getId()));
	}
}
