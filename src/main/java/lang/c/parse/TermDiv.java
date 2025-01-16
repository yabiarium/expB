package lang.c.parse;

import lang.FatalErrorException;
import lang.RecoverableErrorException;
import lang.c.CParseContext;
import lang.c.CParseRule;
import lang.c.CToken;
import lang.c.CTokenizer;
import lang.c.CType;
import lang.c.CodeGenCommon;

public class TermDiv extends CParseRule {
	// 新しく非終端記号に対応するクラスを作成する際は，必ず拡張BNF をコメントでつけること
	// また，更新する際は，拡張BNFの「履歴」を残すこと（例えば，実験３まで：．．．． と 実験４から：．．． のように）
	CToken op;
	CParseRule left, right;

	public TermDiv(CParseContext pcx, CParseRule left) {
		super("TermDiv");
        this.left = left;
		setBNF("termDiv ::= DIV factor"); //CV03~
	}

    public static boolean isFirst(CToken tk) {
        return tk.getType() == CToken.TK_DIV;
	}

	public void parse(CParseContext pcx) throws FatalErrorException {
		// ここにやってくるときは、必ずisFirst()が満たされている
		CTokenizer ct = pcx.getTokenizer();
		op = ct.getCurrentToken(pcx);

		// /の次の字句を読む
		CToken tk = ct.getNextToken(pcx);
		try {
			if (Factor.isFirst(tk)) {
				right = new Factor(pcx);
				right.parse(pcx);
			} else {
				//pcx.fatalError(tk + "termDiv: parse(): /の後ろはfactorです");
				pcx.recoverableError(tk + "termDiv: /の後ろはfactorです");
			}

		} catch (RecoverableErrorException e) {
			// 回復エラーだけ出して処理はStatementXXに任せる
		}
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
		// 割り算の型計算規則
		final int s[][] = {
				// T_err T_int T_pint
				{ CType.T_err, CType.T_err, CType.T_err }, // T_err
				{ CType.T_err, CType.T_int, CType.T_err }, // T_int
				{ CType.T_err, CType.T_err, CType.T_err }, // T_pint
		};
		if (left != null && right != null) {
			left.semanticCheck(pcx);
			right.semanticCheck(pcx);
			int lt = left.getCType().getType(); // +の左辺の型
			int rt = right.getCType().getType(); // +の右辺の型
			int nt = s[lt][rt]; // 規則による型計算
			String lts = left.getCType().toString();
			String rts = right.getCType().toString();
			if (nt == CType.T_err) {
				pcx.fatalError(op + "termDiv: semanticCheck(): 左辺の型[" + lts + "]は右辺の型[" + rts + "]で割れません");
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

			cgc.printInstCodeGen("", "JSR DIV", "サブルーチン呼び出し(返り値はR0に)");
			cgc.printInstCodeGen("", "SUB #2, R6", "引数を消す");
			cgc.printPushCodeGen("", "R0", "演算結果R0["+t+"]をスタックに積む");

			// cgc.printLabel("DIV:", "");
			// cgc.printInstCodeGen("", "SUB #1, R6", "SPを引数の所へ");
			// cgc.printPopCodeGen("", "R2", "右(割る数)を取り出す:["+rt+"]");
			// cgc.printPopCodeGen("", "R1", "左(割られる数)を取り出す:["+lt+"]");
			// cgc.printInstCodeGen("", "MOV #0, R0", "R0を演算結果の一時保存用にリセット");
			// cgc.printInstCodeGen("", "CMP #0, R1", "割られる数と0を比較");
			// cgc.printInstCodeGen("", "BRZ DIV_ZERO1", "0なら");
			// cgc.printInstCodeGen("", "CMP #0, R2", "0でないなら、割る数と0を比較");
			// cgc.printInstCodeGen("", "BRZ DIV_ZERO1", "割る数が0ならエラー");
			// cgc.printLabel("DIV_CALC:", "両方0でなければ、計算する");
			// cgc.printInstCodeGen("", "CMP R2, R1", "割られる数-割る数");
			// cgc.printInstCodeGen("", "BRN DIV_END", "R2>R1(割られる数の方が小さい)なら");
			// cgc.printInstCodeGen("", "SUB R2, R1", "割られる数-割る数");
			// cgc.printInstCodeGen("", "ADD #1, R0", "商+1");
			// cgc.printInstCodeGen("", "JMP DIV_CALC ", "");
			// cgc.printLabel("DIV_END:", "");
			// cgc.printInstCodeGen("", "ADD #3, R6", "RET前にSPを戻り番地の所に戻す");
			// cgc.printInstCodeGen("", "RET", "呼出元へ");
			// cgc.printLabel("DIV_ZERO1:", "エラー処理用(式に0がある場合)");
			// cgc.printInstCodeGen("", "MOV #0, R0", "0を返す");
			// cgc.printInstCodeGen("", "JMP DIV_END", "");

			cgc.printCompleteComment(getBNF(getId()));
		}
	}
}
