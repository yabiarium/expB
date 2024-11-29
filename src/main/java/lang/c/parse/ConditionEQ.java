package lang.c.parse;

import lang.*;
import lang.c.*;

public class ConditionEQ  extends CParseRule {
	// program ::= expression EOF
	CParseRule statement;

	public ConditionEQ(CParseContext pcx) {
		super("ConditionEQ");
		setBNF("conditionEQ ::= EQ expression"); //CV06~
	}

	public static boolean isFirst(CToken tk) {
		return tk.getType() == CToken.TK_EQ;
	}

	public void parse(CParseContext pcx) throws FatalErrorException {
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
		CodeGenCommon cgc = pcx.getCodeGenCommon();
	}
}
