package lang.c.parse;

import lang.FatalErrorException;
import lang.RecoverableErrorException;
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
		try {
			CToken tk = ct.getNextToken(pcx);
			if (ConditionFactor.isFirst(tk)) {
				conditionFactor = new ConditionFactor(pcx);
				conditionFactor.parse(pcx);
			} else {
				//pcx.fatalError(tk + "termAnd: parse(): &&の後ろはconditionFactorです");
				pcx.recoverableError(tk + " termAnd: &&の後ろはconditionFactorです");
			}

		} catch (RecoverableErrorException e) {
			// ; ) まで読み飛ばす処理はconditionBlockに継ぐ
		}
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
		//T_bool=5(CTypeで定義)

		if (left != null && conditionFactor != null) {
			left.semanticCheck(pcx);
			conditionFactor.semanticCheck(pcx);
			//parse()の時点で左右に付くものが制限されてbool型以外は来ないのでここでの型チェックは必要ないが一応確認
			int lt = left.getCType().getType(); // &&の左辺の型
			int rt = conditionFactor.getCType().getType(); // &&の右辺の型
			
			if (lt != CType.T_bool || rt != CType.T_bool) {
				String lts = left.getCType().toString();
				String rts = conditionFactor.getCType().toString();
				pcx.fatalError(op + "termAnd: semanticCheck(): 左辺の型[" + lts + "]と右辺の型[" + rts + "]はT_boolである必要があります");
			}
			this.setCType(CType.getCType(CType.T_bool));
			this.setConstant(true);
		}
	}


	public void codeGen(CParseContext pcx) throws FatalErrorException {
		CodeGenCommon cgc = pcx.getCodeGenCommon();
		if (left != null && conditionFactor != null) {
			cgc.printStartComment(getBNF(getId()));
			//左のコード生成はConditionTermで実行済み
			//left.codeGen(pcx); // 左部分木のコード生成を頼む ConditionFactor.javaのcodeGen()が動作する
			conditionFactor.codeGen(pcx); // 右部分木のコード生成を頼む

			cgc.printPopCodeGen("", "R0", "TermAnd: スタックから右辺の結果を取り出す");
			cgc.printPopCodeGen("", "R1", "TermAnd: スタックから左辺の結果を取り出す");
			cgc.printInstCodeGen("", "AND R1, R0", "TermAnd: AND演算");
			cgc.printPushCodeGen("", "R0", "TermAnd: 演算結果をスタックに積む");

			cgc.printCompleteComment(getBNF(getId()));
		}
	}
}
