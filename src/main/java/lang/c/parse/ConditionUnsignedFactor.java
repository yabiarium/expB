package lang.c.parse;

import lang.*;
import lang.c.*;

public class ConditionUnsignedFactor extends CParseRule {
	CParseRule number, factorAmp, expression, addressToValue;

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

    // #######
	public void parse(CParseContext pcx) throws FatalErrorException {
		// ここにやってくるときは、必ずisFirst()が満たされている
		CTokenizer ct = pcx.getTokenizer();
		CToken tk = ct.getCurrentToken(pcx);

		if(FactorAmp.isFirst(tk)){
			factorAmp = new FactorAmp(pcx);
			factorAmp.parse(pcx);
		}else if(tk.getType() == CToken.TK_LPAR){
			// ( の次の字句を読む
			tk = ct.getNextToken(pcx);
			if(Expression.isFirst(tk)){
				expression = new Expression(pcx);
				expression.parse(pcx);
				// expressionの解析後,現在の字句を読む
				tk = ct.getCurrentToken(pcx);
				if(tk.getType() != CToken.TK_RPAR){
					pcx.fatalError(tk + ")がありません");
				}
				tk = ct.getNextToken(pcx);
			}else{
				pcx.fatalError(tk + "(の後ろはexpressionです");
			}
		}else if(Number.isFirst(tk)){
			number = new Number(pcx);
			number.parse(pcx);
		}else{
			addressToValue = new AddressToValue(pcx);
			addressToValue.parse(pcx);
		}
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
		if (factorAmp != null) {
			factorAmp.semanticCheck(pcx);
			this.setCType(factorAmp.getCType()); // factorAmp の型をそのままコピー
			this.setConstant(factorAmp.isConstant());
		}else if (number != null) {
			number.semanticCheck(pcx);
			this.setCType(number.getCType()); // number の型をそのままコピー
			this.setConstant(number.isConstant());
		}else if(expression != null){
			expression.semanticCheck(pcx);
			this.setCType(expression.getCType());
			this.setConstant(expression.isConstant());
		}else{
			addressToValue.semanticCheck(pcx);
			this.setCType(addressToValue.getCType());
			this.setConstant(addressToValue.isConstant());
		}
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
		CodeGenCommon cgc = pcx.getCodeGenCommon();
		cgc.printStartComment(getBNF(getId()));
		if (factorAmp != null) {
			factorAmp.codeGen(pcx);
		}else if (number != null) {
			number.codeGen(pcx);
		}else if (expression != null){
			expression.codeGen(pcx);
		}else if (addressToValue != null){
			addressToValue.codeGen(pcx);
		}
		cgc.printCompleteComment(getBNF(getId()));
	}
}