package lang.c.parse;

import lang.*;
import lang.c.*;

public class NotFactor extends CParseRule {
	CToken op;
	CParseRule conditionUnsignedFactor;

	public NotFactor(CParseContext pcx) {
		super("NotFactor");
		setBNF("notFactor ::= NOT conditionUnsignedFactor"); //CV08~
	}

	public static boolean isFirst(CToken tk) {
        return tk.getType() == CToken.TK_NOT;
	}

	public void parse(CParseContext pcx) throws FatalErrorException {
		CTokenizer ct = pcx.getTokenizer();
		op = ct.getCurrentToken(pcx);
		// ! の次の字句を読む
		CToken tk = ct.getNextToken(pcx);
		if (ConditionUnsignedFactor.isFirst(tk)) {
			conditionUnsignedFactor = new ConditionUnsignedFactor(pcx);
			conditionUnsignedFactor.parse(pcx);
		} else {
			pcx.fatalError(tk + "notFactor: parse(): !の後ろはConditionUnsignedFactorです");
		}
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
		if (conditionUnsignedFactor != null) {
			conditionUnsignedFactor.semanticCheck(pcx);
			//parse()の時点で後ろに付くものが制限されてbool型以外は来ないのでここでの型チェックは必要ないが一応確認
			int rt = conditionUnsignedFactor.getCType().getType(); // !の右辺の型
			String rts = conditionUnsignedFactor.getCType().toString();
			if (rt != CType.T_bool) {
				pcx.fatalError(op + ": notFactor: semanticCheck(): !の後ろはT_boolです[" + rts + "]");
			}
			this.setCType(conditionUnsignedFactor.getCType());
			this.setConstant(conditionUnsignedFactor.isConstant());
		}
	}

	
	public void codeGen(CParseContext pcx) throws FatalErrorException {
		CodeGenCommon cgc = pcx.getCodeGenCommon();
		if (conditionUnsignedFactor != null) {
			cgc.printStartComment(getBNF(getId()));
			conditionUnsignedFactor.codeGen(pcx); // 右部分木のコード生成を頼む
			
			cgc.printPopCodeGen("", "R0", "NotFactor: condition()実行結果を取り出す");
			cgc.printInstCodeGen("", "XOR #0x0001, R0", "NotFactor: NOT演算");
			cgc.printPushCodeGen("", "R0", "NotFactor: 結果をスタックに積む");

			cgc.printCompleteComment(getBNF(getId()));
		}
	}
}