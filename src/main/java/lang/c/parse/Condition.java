package lang.c.parse;

import lang.*;
import lang.c.*;

public class Condition extends CParseRule {
	// program ::= expression EOF
	CParseRule statement;

	public Condition(CParseContext pcx) {
		super("Condition");
		setBNF("condition ::= TRUE | FALSE | expression ( conditionLT | conditionLE | conditionGT | conditionGE | conditionEQ | conditionNE )"); //CV06~
	}

	public static boolean isFirst(CToken tk) {
		return tk.getType() == CToken.TK_TRUE || tk.getType() == CToken.TK_FALSE || Expression.isFirst(tk);
	}

	public void parse(CParseContext pcx) throws FatalErrorException {
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
		CodeGenCommon cgc = pcx.getCodeGenCommon();
	}
}
