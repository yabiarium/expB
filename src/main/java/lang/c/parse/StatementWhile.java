package lang.c.parse;

import lang.*;
import lang.c.*;

public class StatementWhile extends CParseRule {
	CParseRule expression;

	public StatementWhile(CParseContext pcx) {
		super("StatementWhile");
		setBNF("statementWhile ::= WHILE conditionBlock statement"); //CV07~
	}

	public static boolean isFirst(CToken tk) {
        return tk.getType() == CToken.TK_WHILE;
	}

	public void parse(CParseContext pcx) throws FatalErrorException {
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
	}
}