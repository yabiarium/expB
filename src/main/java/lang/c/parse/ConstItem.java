package lang.c.parse;

import lang.*;
import lang.c.*;

public class ConstItem extends CParseRule {

	public ConstItem(CParseContext pcx) {
		super("ConstItem");
		setBNF("constItem ::= [ MULT ] IDENT ASSIGN [ AMP ] NUM"); //CV10~
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
