package lang.c.parse;

import lang.*;
import lang.c.*;

public class MinusFactor extends CParseRule {
	// 新しく非終端記号に対応するクラスを作成する際は，必ず拡張BNF をコメントでつけること
	// また，更新する際は，拡張BNFの「履歴」を残すこと（例えば，実験３まで：．．．． と 実験４から：．．． のように）
	CToken op;
	CParseRule right;

	public MinusFactor(CParseContext pcx) {
		super("MinusFactor");
		setBNF("minusFactor ::= MINUS unsignedFactor"); //CV03~
	}

	public static boolean isFirst(CToken tk) {
        return tk.getType() == CToken.TK_MINUS;
	}

	public void parse(CParseContext pcx) throws FatalErrorException {
        // ここにやってくるときは、必ずisFirst()が満たされている
		CTokenizer ct = pcx.getTokenizer();
		op = ct.getCurrentToken(pcx);

		// -の次の字句を読む
		CToken tk = ct.getNextToken(pcx);
		try {
			if (UnsignedFactor.isFirst(tk)) {
				right = new UnsignedFactor(pcx);
				right.parse(pcx);
			} else {
				//pcx.fatalError(tk + "minusFactor: parse(): -の後ろはunsignedFactorです");
				pcx.recoverableError(tk + "minusFactor: -の後ろはunsignedFactorです");
			}

		} catch (RecoverableErrorException e) {
			// 回復エラーだけ出して処理はStatementXXに任せる
		}
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
		if (right != null) {
			right.semanticCheck(pcx);
			int rt = right.getCType().getType(); // -の右辺の型
			String rts = right.getCType().toString();
			try {
				if (rt != CType.T_int) {
					//pcx.fatalError(op + ": minusFactor: semanticCheck(): -の後ろはT_intです[" + rts + "]");
					pcx.recoverableError(op + ": minusFactor: semanticCheck(): -の後ろはT_intです[" + rts + "]");
				}
			} catch (RecoverableErrorException e) {
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
			// - 符号のコード生成(オーバーフローは一旦考えない)
			String rt = right.getCType().toString();
			String t = getCType().toString();
			cgc.printPopCodeGen("", "R1", "MinusFactor: 右を取り出す:["+rt+"]");
			cgc.printInstCodeGen("", "MOV #0, R0", "MinusFactor: 左に0を入れる");
			cgc.printInstCodeGen("", "SUB R1, R0", "MinusFactor: R0[int]からR1["+rt+"] を引く");
			cgc.printPushCodeGen("", "R0", "ExpressionAdd: 演算結果R0["+t+"]をスタックに積む");
			cgc.printCompleteComment(getBNF(getId()));
		}
	}
}