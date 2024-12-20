package lang.c.parse;

import lang.*;
import lang.c.*;

public class ConditionBlock  extends CParseRule {
	CParseRule conditionExpression;

	public ConditionBlock(CParseContext pcx) {
		super("ConditionBlock");
		setBNF("conditionBlock ::= LPAR conditionExpression RPAR"); //CV07~
	}

	public static boolean isFirst(CToken tk) {
        return tk.getType() == CToken.TK_LPAR;
	}

	public void parse(CParseContext pcx) throws FatalErrorException {
        CTokenizer ct = pcx.getTokenizer();
		CToken tk = ct.getCurrentToken(pcx);

        // ( の次のトークンを読む
        tk = ct.getNextToken(pcx);
		if(ConditionExpression.isFirst(tk)){
			conditionExpression = new ConditionExpression(pcx);
            conditionExpression.parse(pcx);
		}else{
            pcx.fatalError(tk + "ConditionBlock: parse(): (の後ろはconditionExpressionです");
        }

        // conditionExpression の次のトークンを読む
        tk = ct.getCurrentToken(pcx);
		if(tk.getType() != CToken.TK_RPAR){
            pcx.fatalError(tk + "ConditionBlock: parse(): )がありません");
        }
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
        if(conditionExpression != null){
            conditionExpression.semanticCheck(pcx);
			this.setCType(conditionExpression.getCType()); // conditionExpression の型をそのままコピー
			this.setConstant(conditionExpression.isConstant());
        }
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
		CodeGenCommon cgc = pcx.getCodeGenCommon();
		cgc.printStartComment(getBNF(getId()));
		if (conditionExpression != null) {
            conditionExpression.codeGen(pcx);
		}
		cgc.printCompleteComment(getBNF(getId()));
	}
}