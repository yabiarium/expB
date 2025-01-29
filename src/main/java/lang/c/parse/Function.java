package lang.c.parse;

import lang.*;
import lang.c.*;

public class Function extends CParseRule {

    public Function(CParseContext pcx) {
        super("Function");
		setBNF("function ::= FUNC ( INT [ MULT ] | VOID ) IDENT LPAR RPAR declblock"); //CV12~
    }

    public static boolean isFirst(CToken tk) {
        return tk.getType() == CToken.TK_FUNC;
	}

    public void parse(CParseContext pcx) throws FatalErrorException {
    }

    public void semanticCheck(CParseContext pcx) throws FatalErrorException {
    }

    public void codeGen(CParseContext pcx) throws FatalErrorException {
    }
}
