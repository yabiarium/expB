package lang.c.parse;

import lang.*;
import lang.c.*;

public class Factor extends CParseRule {
	// 新しく非終端記号に対応するクラスを作成する際は，必ず拡張BNF をコメントでつけること
	// また，更新する際は，拡張BNFの「履歴」を残すこと（例えば，実験３まで：．．．． と 実験４から：．．． のように）
	// factor ::= number
	CParseRule number, factorAmp;

	public Factor(CParseContext pcx) {
		super("Factor");
		//setBNF("Factor ::= Number"); //~CV01
		setBNF("factor ::= factorAmp | number"); //CV02~
	}

	public static boolean isFirst(CToken tk) {
		if(tk.getType() == CToken.TK_AMP){
			return FactorAmp.isFirst(tk);
		}else{
			return Number.isFirst(tk);
		}
	}

	public void parse(CParseContext pcx) throws FatalErrorException {
		// ここにやってくるときは、必ずisFirst()が満たされている
		CTokenizer ct = pcx.getTokenizer();
		CToken tk = ct.getCurrentToken(pcx);

		if(FactorAmp.isFirst(tk)){
			factorAmp = new FactorAmp(pcx);
			factorAmp.parse(pcx);
		}else{
			number = new Number(pcx);
			number.parse(pcx);
		}
		tk = ct.getCurrentToken(pcx);
	
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
		}
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
		CodeGenCommon cgc = pcx.getCodeGenCommon();
		cgc.printStartComment(getBNF(getId()));
		if (factorAmp != null) {
			factorAmp.codeGen(pcx);
		}else if (number != null) {
			number.codeGen(pcx);
		}
		cgc.printCompleteComment(getBNF(getId()));
	}
}