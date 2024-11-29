package lang.c.parse;

import lang.*;
import lang.c.*;

public class ConditionLE  extends CParseRule {
	// program ::= expression EOF
	CParseRule left, expression;

	public ConditionLE(CParseContext pcx, CParseRule left) {
		super("ConditionLE");
		this.left = left;
		setBNF("conditionLE ::= LE expression"); //CV06~
	}

	public static boolean isFirst(CToken tk) {
		return tk.getType() == CToken.TK_LE;
	}

	public void parse(CParseContext pcx) throws FatalErrorException {
		CTokenizer ct = pcx.getTokenizer();
		CToken tk = ct.getCurrentToken(pcx);

		// LT <= の次の字句を読む
		tk = ct.getNextToken(pcx);
		if(Expression.isFirst(tk)){
			expression = new Expression(pcx);
			expression.parse(pcx);
			
			tk = ct.getNextToken(pcx);
		}else{
			pcx.fatalError(tk + "ConditionLE: <=の後ろはexpressionです");
		}
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
		CodeGenCommon cgc = pcx.getCodeGenCommon();
	}
}
