package lang.c.parse;

import lang.*;
import lang.c.*;

public class UnsignedFactor extends CParseRule {
	// 新しく非終端記号に対応するクラスを作成する際は，必ず拡張BNF をコメントでつけること
	// また，更新する際は，拡張BNFの「履歴」を残すこと（例えば，実験３まで：．．．． と 実験４から：．．． のように）
	CParseRule number, factorAmp, expression, addressToValue;

	public UnsignedFactor(CParseContext pcx) {
		super("UnsignedFactor");
		//setBNF("unsignedFactor ::= factorAmp | number | LPAR expression RPAR"); //CV03
		setBNF("unsignedFactor ::= factorAmp | number | LPAR expression RPAR | addressToValue"); //CV04~
	}

	public static boolean isFirst(CToken tk) {
		if(tk.getType() == CToken.TK_AMP){ // &
			return FactorAmp.isFirst(tk);
		}else if(tk.getType() == CToken.TK_LPAR){ // (
			return true;
		}else if(tk.getType() == CToken.TK_NUM){ // 数字
			return Number.isFirst(tk);
		}else{ //上記以外
			return AddressToValue.isFirst(tk);
		}
	}

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