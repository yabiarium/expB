package lang.c.parse;

import lang.*;
import lang.c.*;

public class PlusFactor extends CParseRule {
	// 新しく非終端記号に対応するクラスを作成する際は，必ず拡張BNF をコメントでつけること
	// また，更新する際は，拡張BNFの「履歴」を残すこと（例えば，実験３まで：．．．． と 実験４から：．．． のように）
	CToken op;
	CParseRule right;

	public PlusFactor(CParseContext pcx) {
		super("PlusFactor");
		setBNF("plusFactor ::= PLUS unsignedFactor"); //CV03~
	}

	public static boolean isFirst(CToken tk) {
        return tk.getType() == CToken.TK_PLUS;
	}

	public void parse(CParseContext pcx) throws FatalErrorException {
        // ここにやってくるときは、必ずisFirst()が満たされている
		CTokenizer ct = pcx.getTokenizer();
		op = ct.getCurrentToken(pcx);
		// +の次の字句を読む
		CToken tk = ct.getNextToken(pcx);
		if (UnsignedFactor.isFirst(tk)) {
			right = new UnsignedFactor(pcx);
			right.parse(pcx);
		} else {
			pcx.fatalError(tk + "PlusFactor: parse(): +の後ろはuFactorです");
		}
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
		if (right != null) {
			right.semanticCheck(pcx);
			int rt = right.getCType().getType(); // +の右辺の型
			String rts = right.getCType().toString();
			if (rt != CType.T_int) {
				pcx.fatalError(op + ": PlusFactor: semanticCheck(): +の後ろはT_intです[" + rts + "]");
			}
			this.setCType(CType.getCType(rt));
			this.setConstant(right.isConstant());
		}
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
		CodeGenCommon cgc = pcx.getCodeGenCommon();
		if (right != null) {
			cgc.printStartComment(getBNF(getId()));
			right.codeGen(pcx); // 右部分木のコード生成を頼む
			// + 符号は生成すべきコードなし
			cgc.printCompleteComment(getBNF(getId()));
		}
	}
}