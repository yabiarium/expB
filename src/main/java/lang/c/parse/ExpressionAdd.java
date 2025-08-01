package lang.c.parse;

import lang.FatalErrorException;
import lang.RecoverableErrorException;
import lang.c.CParseContext;
import lang.c.CParseRule;
import lang.c.CToken;
import lang.c.CTokenizer;
import lang.c.CType;
import lang.c.CodeGenCommon;

public class ExpressionAdd extends CParseRule {
	// 新しく非終端記号に対応するクラスを作成する際は，必ず拡張BNF をコメントでつけること
	// また，更新する際は，拡張BNFの「履歴」を残すこと（例えば，実験３まで：．．．． と 実験４から：．．． のように）
	// expressionAdd ::= '+' term
	CToken op;
	CParseRule left, right;

	public ExpressionAdd(CParseContext pcx, CParseRule left) {
		super("ExpressionAdd");
		this.left = left;
		setBNF("expressionAdd ::= TK_PLUS term");
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
		try {
			if (Term.isFirst(tk)) {
				right = new Term(pcx);
				right.parse(pcx);
			} else {
				//pcx.fatalError(tk + "expressionAdd: parse(): +の後ろはtermです");
				pcx.recoverableError(tk + " expressionAdd: +の後ろはtermです");
			}

		} catch (RecoverableErrorException e) {
			// 回復エラーだけ出して処理はStatementXXに任せる
		}
		
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
		// 足し算の型計算規則
		final int s[][] = {
				// T_err       T_int        T_pint       T_int[]       T_pint[]
				{ CType.T_err, CType.T_err, CType.T_err, CType.T_err, CType.T_err}, // T_err
				{ CType.T_err, CType.T_int, CType.T_pint, CType.T_err, CType.T_err}, // T_int
				{ CType.T_err, CType.T_pint, CType.T_err, CType.T_err, CType.T_err}, // T_pint
				{ CType.T_err, CType.T_err, CType.T_err, CType.T_err, CType.T_err}, // T_int[]
				{ CType.T_err, CType.T_err, CType.T_err, CType.T_err, CType.T_err}, // T_pint[]
		};
		if (left != null && right != null) {
			left.semanticCheck(pcx);
			right.semanticCheck(pcx);
			int lt = left.getCType().getType(); // +の左辺の型
			int rt = right.getCType().getType(); // +の右辺の型
			int nt = s[lt][rt]; // 規則による型計算
			String lts = left.getCType().toString();
			String rts = right.getCType().toString();
			try {
				if (nt == CType.T_err) {
					//pcx.fatalError(op + ": expressionAdd: semanticCheck(): 左辺の型[" + lts + "]と右辺の型[" + rts + "]は足せません");
					pcx.recoverableError(op + " expressionAdd: 左辺の型[" + lts + "]と右辺の型[" + rts + "]は足せません");
				}
			} catch (Exception e) {
			}
			this.setCType(CType.getCType(nt));
			this.setConstant(left.isConstant() && right.isConstant()); // +の左右両方が定数のときだけ定数
		}
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
		CodeGenCommon cgc = pcx.getCodeGenCommon();
		if (left != null && right != null) {
			cgc.printStartComment(getBNF(getId()));
			left.codeGen(pcx); // 左部分木のコード生成を頼む Number.javaのcodeGen()が動作する
			right.codeGen(pcx); // 右部分木のコード生成を頼む
			String lt = left.getCType().toString();
			String rt = right.getCType().toString();
			String t = getCType().toString();
//			cgc.printInstCodeGen("", "MOV -(R6), R0", "ExpressionAdd: 右を取り出す:["+rt+"]");
//			cgc.printInstCodeGen("", "MOV -(R6), R1", "ExpressionAdd: 左を取り出す:["+lt+"]");
			cgc.printPopCodeGen("", "R0", "ExpressionAdd: 右を取り出す:["+rt+"]");
			cgc.printPopCodeGen("", "R1", "ExpressionAdd: 左を取り出す:["+lt+"]");
			cgc.printInstCodeGen("", "ADD R1, R0", "ExpressionAdd: R1["+lt+"]をR0["+rt+"] に足す:");
			cgc.printPushCodeGen("", "R0", "ExpressionAdd: 演算結果R0["+t+"]をスタックに積む");
			cgc.printCompleteComment(getBNF(getId()));
		}
	}
}
