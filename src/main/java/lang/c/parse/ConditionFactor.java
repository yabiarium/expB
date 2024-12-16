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

	// #######
	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
		if (plusfactor != null) {
			plusfactor.semanticCheck(pcx);
			this.setCType(plusfactor.getCType()); // plusfactor の型をそのままコピー
			this.setConstant(plusfactor.isConstant());
		}else if (minusfactor != null) {
			minusfactor.semanticCheck(pcx);
			this.setCType(minusfactor.getCType()); // minusfactor の型をそのままコピー
			this.setConstant(minusfactor.isConstant());
		}else if (ufactor != null) {
			ufactor.semanticCheck(pcx);
			this.setCType(ufactor.getCType()); // ufactor の型をそのままコピー
			this.setConstant(ufactor.isConstant());
		}
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
		CodeGenCommon cgc = pcx.getCodeGenCommon();
		cgc.printStartComment(getBNF(getId()));
		if (plusfactor != null) {
			plusfactor.codeGen(pcx);
		}else if (minusfactor != null) {
			minusfactor.codeGen(pcx);
		}else if (ufactor != null) {
			ufactor.codeGen(pcx);
		}
		cgc.printCompleteComment(getBNF(getId()));
	}
}