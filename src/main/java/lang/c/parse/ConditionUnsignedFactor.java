package lang.c.parse;

import lang.*;
import lang.c.*;

public class ConditionUnsignedFactor extends CParseRule {
	CParseRule condition, conditionExpression;

	public ConditionUnsignedFactor(CParseContext pcx) {
		super("ConditionUnsignedFactor");
		setBNF("conditionUnsignedFactor ::= condition | LBRA conditionExpression RBRA"); //CV08~
	}

	public static boolean isFirst(CToken tk) {
		if(tk.getType() == CToken.TK_LBRA){ // [ ←条件式の優先度を表す括弧として用いる。(条件式内に数式を用いたとき、計算の優先順を表す()と区別するため)
			return true;
		}else{
			return Condition.isFirst(tk);
		}
	}

	public void parse(CParseContext pcx) throws FatalErrorException {
		CTokenizer ct = pcx.getTokenizer();
		CToken tk = ct.getCurrentToken(pcx);

		if(tk.getType() == CToken.TK_LBRA){ // [
			try {
				tk = ct.getNextToken(pcx); // [ の次のトークンを読む
				if(ConditionExpression.isFirst(tk)){
					conditionExpression = new ConditionExpression(pcx);
					conditionExpression.parse(pcx);
				}else{
					//pcx.fatalError(tk + "conditionUnsignedFactor: parse(): [の後ろはconditionExpressionです");
					pcx.recoverableError(tk + " conditionUnsignedFactor: [の後ろはconditionExpressionです");
				}
		
				// conditionExpression の次のトークンを読む
				tk = ct.getCurrentToken(pcx);
				if(tk.getType() == CToken.TK_RBRA){
					tk = ct.getNextToken(pcx); //正常終了
				}else{
					//pcx.fatalError(tk + "conditionUnsignedFactor: parse(): ]がありません");
					pcx.warning(tk + " conditionUnsignedFactor: ]を補いました");
				}

			} catch (Exception e) {
				// ] ; ) まで読み飛ばす
				ct.skipTo(pcx, CToken.TK_RBRA, CToken.TK_SEMI, CToken.TK_RPAR); //currentTokenが指定したトークンになっている
				tk = ct.getNextToken(pcx);
			}

		}else{
			condition = new Condition(pcx);
			condition.parse(pcx);
		}
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
		if(condition != null){
            condition.semanticCheck(pcx);
			this.setCType(condition.getCType()); // condition の型をそのままコピー
			this.setConstant(condition.isConstant());
        }

		if(conditionExpression != null){
            conditionExpression.semanticCheck(pcx);
			this.setCType(conditionExpression.getCType()); // conditionExpression の型をそのままコピー
			this.setConstant(conditionExpression.isConstant());
        }
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
		CodeGenCommon cgc = pcx.getCodeGenCommon();
		cgc.printStartComment(getBNF(getId()));

		if (condition != null) {
			condition.codeGen(pcx);

		}else if (conditionExpression != null){
			conditionExpression.codeGen(pcx);
		}
		
		cgc.printCompleteComment(getBNF(getId()));
	}
}