package lang.c.parse;

import lang.FatalErrorException;
import lang.c.CParseContext;
import lang.c.CParseRule;
import lang.c.CToken;
import lang.c.CTokenizer;
import lang.c.CType;
import lang.c.CodeGenCommon;

public class Primary extends CParseRule{
    CParseRule primaryMult, variable;

	public Primary(CParseContext pcx) {
		super("Primary");
		//このノードは実は何もしない：下の2つのいずれかである．コード生成はそれぞれのノードにお任せ
		setBNF("primary ::= primaryMult | variable"); //CV04~
	}

    public static boolean isFirst(CToken tk) {
		if(tk.getType() == CToken.TK_MULT){
			return PrimaryMult.isFirst(tk);
		}else{
			return Variable.isFirst(tk);
		}
	}

	public void parse(CParseContext pcx) throws FatalErrorException {
		// ここにやってくるときは、必ずisFirst()が満たされている
		CTokenizer ct = pcx.getTokenizer();
		CToken tk = ct.getCurrentToken(pcx);

		if(tk.getType() == CToken.TK_MULT){
			primaryMult = new PrimaryMult(pcx);
			primaryMult.parse(pcx);
		}else{
			variable = new Variable(pcx);
			variable.parse(pcx);
		}
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
	}
}
