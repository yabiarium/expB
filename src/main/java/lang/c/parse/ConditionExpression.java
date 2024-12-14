package lang.c.parse;

import lang.*;
import lang.c.*;

public class ConditionExpression extends CParseRule {
	CParseRule conditionTerm;

	public ConditionExpression(CParseContext pcx) {
		super("ConditionExpression");
		setBNF("conditionExpression ::= conditionTerm { ExpressionOr }"); // CV08~
	}

	public static boolean isFirst(CToken tk) {
		return ConditionTerm.isFirst(tk);
	}

    // #######
	public void parse(CParseContext pcx) throws FatalErrorException {
		// ここにやってくるときは、必ずisFirst()が満たされている
		CParseRule term = null, list = null;
		term = new Term(pcx);
		term.parse(pcx);
		CTokenizer ct = pcx.getTokenizer();
		CToken tk = ct.getCurrentToken(pcx);
		while (ExpressionAdd.isFirst(tk) | ExpressionSub.isFirst(tk)) {
			if(ExpressionAdd.isFirst(tk)){
				list = new ExpressionAdd(pcx, term);
			}else if(ExpressionSub.isFirst(tk)){
				list = new ExpressionSub(pcx, term);
			}
			list.parse(pcx);
			term = list;
			tk = ct.getCurrentToken(pcx);
		}
		expression = term;
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
		if (expression != null) {
			expression.semanticCheck(pcx);
			this.setCType(expression.getCType()); // expression の型をそのままコピー
			this.setConstant(expression.isConstant());
		}
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
		CodeGenCommon cgc = pcx.getCodeGenCommon();
		cgc.printStartComment(getBNF(getId()));
		if (expression != null) {
			expression.codeGen(pcx);
		}
		cgc.printCompleteComment(getBNF(getId()));
	}
}
