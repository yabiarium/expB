package lang.c.parse;

import lang.*;
import lang.c.*;

public class ConditionTerm extends CParseRule {
	CParseRule conditionFactor, termAnd;

	public ConditionTerm(CParseContext pcx) {
		super("ConditionTerm");
		setBNF("conditionTerm ::= conditionFactor { termAnd }"); //CV08~
	}

	public static boolean isFirst(CToken tk) {
		return ConditionFactor.isFirst(tk);
	}

	public void parse(CParseContext pcx) throws FatalErrorException {
		conditionFactor = new ConditionFactor(pcx);
		conditionFactor.parse(pcx);

		CTokenizer ct = pcx.getTokenizer();
		CToken tk = ct.getCurrentToken(pcx);
		while (TermAnd.isFirst(tk)) {
			termAnd = new TermAnd(pcx, conditionFactor);
			termAnd.parse(pcx);
			tk = ct.getCurrentToken(pcx);
		}
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
		if (conditionFactor != null){
			conditionFactor.semanticCheck(pcx);
			this.setCType(conditionFactor.getCType()); // conditionFactor の型をそのままコピー
			this.setConstant(conditionFactor.isConstant());
		}
		if (termAnd != null) {
			termAnd.semanticCheck(pcx);
			this.setCType(termAnd.getCType()); // termAnd の型をそのままコピー
			this.setConstant(termAnd.isConstant());
		}
	}


	public void codeGen(CParseContext pcx) throws FatalErrorException {
		CodeGenCommon cgc = pcx.getCodeGenCommon();
		cgc.printStartComment(getBNF(getId()));

		conditionFactor.codeGen(pcx);

		if (termAnd != null) {
			termAnd.codeGen(pcx);
		}
		cgc.printCompleteComment(getBNF(getId()));
	}
}
