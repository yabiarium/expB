package lang.c.parse;

import lang.FatalErrorException;
import lang.c.CParseContext;
import lang.c.CParseRule;
import lang.c.CToken;
import lang.c.CTokenizer;
import lang.c.CType;
import lang.c.CodeGenCommon;

public class ExpressionOr extends CParseRule {
	CToken op;
	CParseRule left, conditionTerm;

	public ExpressionOr(CParseContext pcx, CParseRule left) {
		super("ExpressionOr");
		this.left = left;
		setBNF("expressionOr ::= OR conditionTerm "); //CV08~
	}

	public static boolean isFirst(CToken tk) {
		return tk.getType() == CToken.TK_OR;
	}

	public void parse(CParseContext pcx) throws FatalErrorException {
		CTokenizer ct = pcx.getTokenizer();
		op = ct.getCurrentToken(pcx);
		// ||の次の字句を読む
		CToken tk = ct.getNextToken(pcx);
		if (ConditionTerm.isFirst(tk)) {
			conditionTerm = new ConditionTerm(pcx);
			conditionTerm.parse(pcx);
		} else {
			pcx.fatalError(tk + "ExpressionOr: parse(): ||の後ろはconditionTermです");
		}
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
		//T_bool=5(CTypeで定義)

		if (left != null && conditionTerm != null) {
			left.semanticCheck(pcx);
			conditionTerm.semanticCheck(pcx);
			//parse()の時点で左右に付くものが制限されてbool型以外は来ないのでここでの型チェックは必要ないが一応確認
			int lt = left.getCType().getType(); // ||の左辺の型
			int rt = conditionTerm.getCType().getType(); // ||の右辺の型
			
			if (lt != CType.T_bool || rt != CType.T_bool) {
				String lts = left.getCType().toString();
				String rts = conditionTerm.getCType().toString();
				pcx.fatalError(op + ": 左辺の型[" + lts + "]と右辺の型[" + rts + "]はT_boolである必要があります");
			}
			this.setCType(CType.getCType(CType.T_bool));
			this.setConstant(left.isConstant() && conditionTerm.isConstant()); // ||の左右両方が定数のときだけ定数
		}
	}

	// #######
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
