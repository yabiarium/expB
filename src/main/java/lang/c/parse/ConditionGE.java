package lang.c.parse;

import lang.*;
import lang.c.*;

public class ConditionGE  extends CParseRule {
	// program ::= expression EOF
	CParseRule statement;

	public ConditionGE(CParseContext pcx) {
		super("ConditionGE");
		setBNF("conditionGE ::= GE expression"); //CV06~
	}

	public static boolean isFirst(CToken tk) {
		return tk.getType() == CToken.TK_GE;
	}

	public void parse(CParseContext pcx) throws FatalErrorException {
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
		CodeGenCommon cgc = pcx.getCodeGenCommon();
	}
}
