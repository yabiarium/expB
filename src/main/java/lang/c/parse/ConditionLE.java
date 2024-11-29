package lang.c.parse;

import lang.*;
import lang.c.*;

public class ConditionLE  extends CParseRule {
	// program ::= expression EOF
	CParseRule statement;

	public ConditionLE(CParseContext pcx) {
		super("ConditionLE");
		setBNF("conditionLE ::= LE expression"); //CV06~
	}

	public static boolean isFirst(CToken tk) {
		return tk.getType() == CToken.TK_LE;
	}

	public void parse(CParseContext pcx) throws FatalErrorException {
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
		CodeGenCommon cgc = pcx.getCodeGenCommon();
	}
}
