package lang.c.parse;

import lang.*;
import lang.c.*;

public class StatementIf extends CParseRule {
	CParseRule expression;

	public StatementIf(CParseContext pcx) {
		super("StatementIf");
		setBNF("statementIf ::= IF conditionBlock statementBlock [ ELSE ( statementIf | LCUR { statement } RCUR )]"); //CV07~
	}

	public static boolean isFirst(CToken tk) {
        return true;
	}

	public void parse(CParseContext pcx) throws FatalErrorException {
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
	}
}