package lang.c.parse;

import lang.*;
import lang.c.*;

public class ConditionLT  extends CParseRule {
	// program ::= expression EOF
	CParseRule statement;

	public ConditionLT(CParseContext pcx) {
		super("ConditionLT");
		setBNF("conditionLT ::= LT expression"); //CV06~
	}

	public static boolean isFirst(CToken tk) {
		return tk.getType() == CToken.TK_LT;
	}

	public void parse(CParseContext pcx) throws FatalErrorException {
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
		CodeGenCommon cgc = pcx.getCodeGenCommon();
	}
}
