package lang.c.parse;

import lang.*;
import lang.c.*;

public class VoidDecl extends CParseRule {

    public VoidDecl(CParseContext pcx) {
        super("VoidDecl");
		setBNF("voidDecl ::= VOID IDENT LPAR RPAR { COMMA IDENT LPAR RPAR } SEMI"); //CV12~
    }

    public static boolean isFirst(CToken tk) {
        return tk.getType() == CToken.TK_VOID;
	}

    public void parse(CParseContext pcx) throws FatalErrorException {
    }

    public void semanticCheck(CParseContext pcx) throws FatalErrorException {
    }

    public void codeGen(CParseContext pcx) throws FatalErrorException {
    }
}
