package lang.c.parse;

import lang.*;
import lang.c.*;

public class Call extends CParseRule {

    public Call(CParseContext pcx) {
        super("Call");
		setBNF("call ::= LPAR RPAR"); //CV12~
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
