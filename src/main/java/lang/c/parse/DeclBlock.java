package lang.c.parse;

import lang.*;
import lang.c.*;

public class DeclBlock extends CParseRule {
    
    public DeclBlock(CParseContext pcx) {
        super("DeclBlock");
        setBNF("declBlock ::= LCUR { declaration } { statement } RCUR"); //CV11~
    }

    public static boolean isFirst(CToken tk) {
        return tk.getType() == CToken.TK_LCUR;
    }

    public void parse(CParseContext pcx) throws FatalErrorException {
		CTokenizer ct = pcx.getTokenizer();
		CToken tk = ct.getCurrentToken(pcx);
    }

    public void semanticCheck(CParseContext pcx) throws FatalErrorException {
    }

    public void codeGen(CParseContext pcx) throws FatalErrorException {
    }

}
