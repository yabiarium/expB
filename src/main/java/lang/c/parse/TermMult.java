package lang.c.parse;

import lang.FatalErrorException;
import lang.RecoverableErrorException;
import lang.c.CParseContext;
import lang.c.CParseRule;
import lang.c.CToken;
import lang.c.CTokenizer;
import lang.c.CType;
import lang.c.CodeGenCommon;

public class TermMult extends CParseRule {
	// 新しく非終端記号に対応するクラスを作成する際は，必ず拡張BNF をコメントでつけること
	// また，更新する際は，拡張BNFの「履歴」を残すこと（例えば，実験３まで：．．．． と 実験４から：．．． のように）
	CToken op;
	CParseRule left, right;

	public TermMult(CParseContext pcx, CParseRule left) {
		super("TermMult");
        this.left = left;
		setBNF("termMult ::= MULT factor"); //CV03~
	}

    public static boolean isFirst(CToken tk) {
        return tk.getType() == CToken.TK_MULT;
	}

	public void parse(CParseContext pcx) throws FatalErrorException {
		// ここにやってくるときは、必ずisFirst()が満たされている
		CTokenizer ct = pcx.getTokenizer();
		op = ct.getCurrentToken(pcx);

		// *の次の字句を読む
		CToken tk = ct.getNextToken(pcx);
		try {
			if (Factor.isFirst(tk)) {
				right = new Factor(pcx);
				right.parse(pcx);
			} else {
				//pcx.fatalError(tk + "termMult: parse(): *の後ろはfactorです");
				pcx.recoverableError(tk + " termMult: *の後ろはfactorです");
			}

		} catch (RecoverableErrorException e) {
			// 回復エラーだけ出して処理はStatementXXに任せる
		}
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
		// 掛け算の型計算規則
		final int s[][] = {
				// T_err       T_int        T_pint
				{ CType.T_err, CType.T_err, CType.T_err }, // T_err
				{ CType.T_err, CType.T_int, CType.T_err }, // T_int
				{ CType.T_err, CType.T_err, CType.T_err }, // T_pint
		};
		if (left != null && right != null) {
			left.semanticCheck(pcx);
			right.semanticCheck(pcx);
			int lt = left.getCType().getType(); // *の左辺の型
			int rt = right.getCType().getType(); // *の右辺の型
			int nt = s[lt][rt]; // 規則による型計算
			String lts = left.getCType().toString();
			String rts = right.getCType().toString();
			try {
				if (nt == CType.T_err) {
					//pcx.fatalError(op + "termMult: semanticCheck(): 左辺の型[" + lts + "]と右辺の型[" + rts + "]は掛けられません");
					pcx.recoverableError(op + " termMult: 左辺の型[" + lts + "]と右辺の型[" + rts + "]は掛けられません");
				}
			} catch (RecoverableErrorException e) {
			}
			this.setCType(CType.getCType(nt));
			this.setConstant(left.isConstant() && right.isConstant()); // *の左右両方が定数のときだけ定数
		}
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
		CodeGenCommon cgc = pcx.getCodeGenCommon();
		if (left != null && right != null) {
			cgc.printStartComment(getBNF(getId()));
			left.codeGen(pcx); // 左部分木のコード生成を頼む Number.javaのcodeGen()が動作する
			right.codeGen(pcx); // 右部分木のコード生成を頼む
			//String lt = left.getCType().toString();
			//String rt = right.getCType().toString();
			String t = getCType().toString();
			cgc.printInstCodeGen("", "JSR MUL", "サブルーチン呼び出し(返り値はR0に)");
			cgc.printInstCodeGen("", "SUB #2, R6", "引数を消す");
			cgc.printPushCodeGen("", "R0", "演算結果R0["+t+"]をスタックに積む");

			// cgc.printLabel("MUL:", "");
			// cgc.printInstCodeGen("", "SUB #1, R6", "SPを引数の所へ");
			// cgc.printPopCodeGen("", "R2", "右(かける数)を取り出す:["+rt+"]");
			// cgc.printPopCodeGen("", "R1", "左(かけられる数)を取り出す:["+lt+"]");
			// cgc.printInstCodeGen("", "MOV #0, R0", "R0を演算結果の一時保存用にリセット");
			// cgc.printLabel("MUL_CALC:", "");
			// cgc.printInstCodeGen("", "ADD R1, R0", "");
			// cgc.printInstCodeGen("", "SUB #1, R2", "かける数-1");
			// cgc.printInstCodeGen("", "CMP #0, R2", "");
			// cgc.printInstCodeGen("", "BRZ MUL_END", "かける数が0なら繰り返しを抜ける");
			// cgc.printInstCodeGen("", "JMP MUL_CALC ", "かける数が0でない場合");
			// cgc.printLabel("MUL_END:", "");
			// cgc.printInstCodeGen("", "ADD #3, R6", "RET前にSPを戻り番地の所に戻す");
			// cgc.printInstCodeGen("", "RET", "呼出元へ");

			cgc.printCompleteComment(getBNF(getId()));
		}
	}
}
