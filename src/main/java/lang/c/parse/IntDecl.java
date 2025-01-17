package lang.c.parse;

import lang.*;
import lang.c.*;

public class IntDecl extends CParseRule {

	public IntDecl(CParseContext pcx) {
		super("IntDecl");
		setBNF("intDecl ::= INT declItem { COMMA declItem } SEMI"); //CV10~
	}

	public static boolean isFirst(CToken tk) {
		return tk.getType() == CToken.TK_INT;
	}

	public void parse(CParseContext pcx) throws FatalErrorException {
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
		//CodeGenCommon cgc = pcx.getCodeGenCommon();
    }
}
