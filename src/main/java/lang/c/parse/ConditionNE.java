package lang.c.parse;

import lang.*;
import lang.c.*;

public class ConditionNE  extends CParseRule {
	// program ::= expression EOF
	CParseRule statement;

	public ConditionNE(CParseContext pcx) {
		super("ConditionNE");
		setBNF("conditionNE ::= NE expression"); //CV06~
	}

	public static boolean isFirst(CToken tk) {
		return Statement.isFirst(tk) || tk.getType() == CToken.TK_EOF;
	}

	public void parse(CParseContext pcx) throws FatalErrorException {
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
		CodeGenCommon cgc = pcx.getCodeGenCommon();
	}
}
