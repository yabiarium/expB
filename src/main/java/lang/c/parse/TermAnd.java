package lang.c.parse;

import lang.FatalErrorException;
import lang.c.CParseContext;
import lang.c.CParseRule;
import lang.c.CToken;
import lang.c.CTokenizer;
import lang.c.CType;
import lang.c.CodeGenCommon;

public class TermAnd extends CParseRule {
	CToken op;
	CParseRule left, conditionFactor;

	public TermAnd(CParseContext pcx, CParseRule left) {
		super("TermAnd");
        this.left = left;
		setBNF("termAnd ::= AND conditionFactor"); //CV08~
	}

    public static boolean isFirst(CToken tk) {
        return tk.getType() == CToken.TK_AND;
	}

	public void parse(CParseContext pcx) throws FatalErrorException {
		CTokenizer ct = pcx.getTokenizer();
		op = ct.getCurrentToken(pcx);
		// &&の次の字句を読む
		CToken tk = ct.getNextToken(pcx);
		if (ConditionFactor.isFirst(tk)) {
			conditionFactor = new ConditionFactor(pcx);
			conditionFactor.parse(pcx);
		} else {
			pcx.fatalError(tk + "&&の後ろはconditionFactorです");
		}
	}

	// #######
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
				pcx.fatalError(op + ": 左辺の型[" + lts + "]は右辺の型[" + rts + "]で割れません");
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

			cgc.printCompleteComment(getBNF(getId()));
		}
	}
}
