package lang.c.parse;

import lang.*;
import lang.c.*;

public class Declaration extends CParseRule {

	public Declaration(CParseContext pcx) {
		super("Declaration");
		setBNF("declaration ::= intDecl | constDecl"); //CV10~
	}

	public static boolean isFirst(CToken tk) {
		return Statement.isFirst(tk) || tk.getType() == CToken.TK_EOF;
	}

	public void parse(CParseContext pcx) throws FatalErrorException {
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
		//CodeGenCommon cgc = pcx.getCodeGenCommon();
    }
}
