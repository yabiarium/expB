package lang.c.parse;

import lang.*;
import lang.c.*;

public class StatementCall extends CParseRule {

    public StatementCall(CParseContext pcx) {
        super("StatementCall");
		setBNF("statementCall ::= CALL ident LPAR RPAR SEMI"); //CV12~
    }

    public static boolean isFirst(CToken tk) {
        return tk.getType() == CToken.TK_CALL;
	}

    public void parse(CParseContext pcx) throws FatalErrorException {
    }

    public void semanticCheck(CParseContext pcx) throws FatalErrorException {
    }

    public void codeGen(CParseContext pcx) throws FatalErrorException {
    }
}
