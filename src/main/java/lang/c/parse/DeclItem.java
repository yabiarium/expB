package lang.c.parse;

import lang.*;
import lang.c.*;

public class DeclItem extends CParseRule {

	public DeclItem(CParseContext pcx) {
		super("DeclItem");
		setBNF("declItem ::= [ MULT ] IDENT [ LBRA NUM RBRA ]"); //CV10~
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
