package lang.c.parse;

import lang.*;
import lang.c.*;

public class StatementReturn extends CParseRule {

    public StatementReturn(CParseContext pcx) {
        super("StatementReturn");
		setBNF("statementReturn ::= RETURN [ expression ] SEMI"); //CV12~
    }

    public static boolean isFirst(CToken tk) {
        return tk.getType() == CToken.TK_RETURN;
	}

    public void parse(CParseContext pcx) throws FatalErrorException {
    }

    public void semanticCheck(CParseContext pcx) throws FatalErrorException {
    }

    public void codeGen(CParseContext pcx) throws FatalErrorException {
    }
}
