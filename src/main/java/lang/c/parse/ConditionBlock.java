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
		try {
			if(ConditionExpression.isFirst(tk)){
				conditionExpression = new ConditionExpression(pcx);
				conditionExpression.parse(pcx);
			}else{
				//pcx.fatalError(tk + "conditionBlock: parse(): (の後ろはconditionExpressionです");
				pcx.recoverableError(tk + " conditionBlock: (の後ろはconditionExpressionです");
			}

			// conditionExpression の次のトークンを読む
			tk = ct.getCurrentToken(pcx);
			if(tk.getType() == CToken.TK_RPAR){
				tk = ct.getNextToken(pcx); //正常終了
			}else{
				//pcx.fatalError(tk + "conditionBlock: parse(): )がありません");
				pcx.warning(tk + " conditionBlock: )を補いました");
			}

		} catch (RecoverableErrorException e) {
			// ; ) まで読み飛ばす
			ct.skipTo(pcx, CToken.TK_SEMI, CToken.TK_RPAR); //currentTokenが指定したトークンになっている
			tk = ct.getNextToken(pcx);
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