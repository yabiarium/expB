package lang.c.parse;

import lang.*;
import lang.c.*;

public class Program2 extends CParseRule {
	// program ::= expression EOF
	CParseRule condition;

	public Program2(CParseContext pcx) {
		super("Program2");
		setBNF("program2 ::= condition"); //CV06~
	}

	public static boolean isFirst(CToken tk) {
		return Condition.isFirst(tk);
	}

	public void parse(CParseContext pcx) throws FatalErrorException {
		CTokenizer ct = pcx.getTokenizer();
		CToken tk = ct.getCurrentToken(pcx);

		if(Condition.isFirst(tk)){
			condition = new Condition(pcx);
			condition.parse(pcx);
			
			tk = ct.getNextToken(pcx);
		}
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
		CodeGenCommon cgc = pcx.getCodeGenCommon();
	}
}
