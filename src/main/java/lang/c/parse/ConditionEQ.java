package lang.c.parse;

import lang.*;
import lang.c.*;

public class ConditionEQ  extends CParseRule {
	// program ::= expression EOF
	CParseRule left, expression;

	public ConditionEQ(CParseContext pcx, CParseRule left) {
		super("ConditionEQ");
		this.left = left;
		setBNF("conditionEQ ::= EQ expression"); //CV06~
	}

	public static boolean isFirst(CToken tk) {
		return tk.getType() == CToken.TK_EQ;
	}

	public void parse(CParseContext pcx) throws FatalErrorException {
		CTokenizer ct = pcx.getTokenizer();
		CToken tk = ct.getCurrentToken(pcx);

		// LT == の次の字句を読む
		tk = ct.getNextToken(pcx);
		if(Expression.isFirst(tk)){
			expression = new Expression(pcx);
			expression.parse(pcx);
			
			tk = ct.getNextToken(pcx);
		}else{
			pcx.fatalError(tk + "ConditionEQ: ==の後ろはexpressionです");
		}
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
		CodeGenCommon cgc = pcx.getCodeGenCommon();
	}
}
