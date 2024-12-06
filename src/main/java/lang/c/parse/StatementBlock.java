package lang.c.parse;

import lang.*;
import lang.c.*;

public class StatementBlock extends CParseRule {
	CParseRule expression;

	public StatementBlock(CParseContext pcx) {
		super("StatementBlock");
		setBNF("statementBlock ::= LCUR { statement } RCUR"); //CV07~
	}

	public static boolean isFirst(CToken tk) {
        return tk.getType() == CToken.TK_LCUR;
	}

	public void parse(CParseContext pcx) throws FatalErrorException {
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
	}
}