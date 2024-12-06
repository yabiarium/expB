package lang.c.parse;

import lang.*;
import lang.c.*;

public class ConditionBlock  extends CParseRule {
	CParseRule expression;

	public ConditionBlock(CParseContext pcx) {
		super("ConditionBlock");
		setBNF("conditionBlock ::= LPAR condition RPAR"); //CV07~
	}

	public static boolean isFirst(CToken tk) {
        return tk.getType() == CToken.TK_LPAR;
	}

	public void parse(CParseContext pcx) throws FatalErrorException {
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
	}
}