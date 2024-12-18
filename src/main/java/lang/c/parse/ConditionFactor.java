package lang.c.parse;

import lang.*;
import lang.c.*;

public class ConditionFactor extends CParseRule {
	CParseRule notFactor, conditionUnsignedFactor;

	public ConditionFactor(CParseContext pcx) {
		super("ConditionFactor");
		setBNF("conditionFactor ::= notFactor | conditionUnsignedFactor"); //CV08~
	}

	public static boolean isFirst(CToken tk) {
		if(tk.getType() == CToken.TK_NOT){ // !
			return NotFactor.isFirst(tk);
		}else{
			return ConditionUnsignedFactor.isFirst(tk);
		}
	}

	public void parse(CParseContext pcx) throws FatalErrorException {
		CTokenizer ct = pcx.getTokenizer();
		CToken tk = ct.getCurrentToken(pcx);

		if(tk.getType() == CToken.TK_NOT){
			notFactor = new NotFactor(pcx);
			notFactor.parse(pcx);
		}else{
			conditionUnsignedFactor = new ConditionUnsignedFactor(pcx);
			conditionUnsignedFactor.parse(pcx);
		}
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
		if (notFactor != null) {
			notFactor.semanticCheck(pcx);
			this.setCType(notFactor.getCType()); // notFactor の型をそのままコピー
			this.setConstant(notFactor.isConstant());

		}else if (conditionUnsignedFactor != null) {
			conditionUnsignedFactor.semanticCheck(pcx);
			this.setCType(conditionUnsignedFactor.getCType()); // conditionUnsignedFactor の型をそのままコピー
			this.setConstant(conditionUnsignedFactor.isConstant());
		}
	}

	
	public void codeGen(CParseContext pcx) throws FatalErrorException {
		CodeGenCommon cgc = pcx.getCodeGenCommon();
		cgc.printStartComment(getBNF(getId()));
		if (notFactor != null) {
			notFactor.codeGen(pcx);

		}else if (conditionUnsignedFactor != null) {
			conditionUnsignedFactor.codeGen(pcx);
		}
		cgc.printCompleteComment(getBNF(getId()));
	}
}