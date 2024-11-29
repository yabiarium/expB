package lang.c.parse;

import lang.*;
import lang.c.*;

public class ConditionLT  extends CParseRule {
	// program ::= expression EOF
	CParseRule left, expression;

	public ConditionLT(CParseContext pcx, CParseRule left) {
		super("ConditionLT");
		this.left = left;
		setBNF("conditionLT ::= LT expression"); //CV06~
	}

	public static boolean isFirst(CToken tk) {
		return tk.getType() == CToken.TK_LT;
	}

	public void parse(CParseContext pcx) throws FatalErrorException {
		CTokenizer ct = pcx.getTokenizer();
		CToken tk = ct.getCurrentToken(pcx);

		// LT < の次の字句を読む
		tk = ct.getNextToken(pcx);
		if(Expression.isFirst(tk)){
			expression = new Expression(pcx);
			expression.parse(pcx);
			
			tk = ct.getNextToken(pcx);
		}else{
			pcx.fatalError(tk + "ConditionLT: <の後ろはexpressionです");
		}
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
		CodeGenCommon cgc = pcx.getCodeGenCommon();
	}
}
