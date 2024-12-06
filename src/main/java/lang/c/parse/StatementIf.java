package lang.c.parse;

import lang.*;
import lang.c.*;

public class StatementIf extends CParseRule {
	CParseRule expression;

	public StatementIf(CParseContext pcx) {
		super("StatementIf");
		setBNF("statementIf ::= IF conditionBlock statement [ ELSE ( statementIf | statement )]"); //CV07~
	}

	public static boolean isFirst(CToken tk) {
        return tk.getType() == CToken.TK_IF;
	}

	public void parse(CParseContext pcx) throws FatalErrorException {
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
	}
}