package lang.c.parse;

import lang.*;
import lang.c.*;

public class Factor extends CParseRule {
	// 新しく非終端記号に対応するクラスを作成する際は，必ず拡張BNF をコメントでつけること
	// また，更新する際は，拡張BNFの「履歴」を残すこと（例えば，実験３まで：．．．． と 実験４から：．．． のように）
	CParseRule plusfactor, minusfactor, ufactor;

	public Factor(CParseContext pcx) {
		super("Factor");
		//setBNF("Factor ::= Number"); //~CV01
		//setBNF("factor ::= factorAmp | number"); //CV02
		setBNF("factor ::= plusFactor | minusFactor | unsignedFactor"); //CV03~
	}

	public static boolean isFirst(CToken tk) {
		if(tk.getType() == CToken.TK_PLUS){
			return PlusFactor.isFirst(tk);
		}else if(tk.getType() == CToken.TK_MINUS){
			return MinusFactor.isFirst(tk);
		}else{
			return UnsignedFactor.isFirst(tk);
		}
	}

	public void parse(CParseContext pcx) throws FatalErrorException {
		// ここにやってくるときは、必ずisFirst()が満たされている
		CTokenizer ct = pcx.getTokenizer();
		CToken tk = ct.getCurrentToken(pcx);

		if(PlusFactor.isFirst(tk)){
			plusfactor = new PlusFactor(pcx);
			plusfactor.parse(pcx);
		}else if(MinusFactor.isFirst(tk)){
			minusfactor = new MinusFactor(pcx);
			minusfactor.parse(pcx);
		}else{
			ufactor = new UnsignedFactor(pcx);
			ufactor.parse(pcx);
		}
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
		if (plusfactor != null) {
			plusfactor.semanticCheck(pcx);
			this.setCType(plusfactor.getCType()); // plusfactor の型をそのままコピー
			this.setConstant(plusfactor.isConstant());
		}else if (minusfactor != null) {
			minusfactor.semanticCheck(pcx);
			this.setCType(minusfactor.getCType()); // minusfactor の型をそのままコピー
			this.setConstant(minusfactor.isConstant());
		}else if (ufactor != null) {
			ufactor.semanticCheck(pcx);
			this.setCType(ufactor.getCType()); // ufactor の型をそのままコピー
			this.setConstant(ufactor.isConstant());
		}
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
		CodeGenCommon cgc = pcx.getCodeGenCommon();
		cgc.printStartComment(getBNF(getId()));
		if (plusfactor != null) {
			plusfactor.codeGen(pcx);
		}else if (minusfactor != null) {
			minusfactor.codeGen(pcx);
		}else if (ufactor != null) {
			ufactor.codeGen(pcx);
		}
		cgc.printCompleteComment(getBNF(getId()));
	}
}