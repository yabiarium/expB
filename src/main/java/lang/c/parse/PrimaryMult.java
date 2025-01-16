package lang.c.parse;

import lang.FatalErrorException;
import lang.RecoverableErrorException;
import lang.c.CParseContext;
import lang.c.CParseRule;
import lang.c.CToken;
import lang.c.CTokenizer;
import lang.c.CType;
import lang.c.CodeGenCommon;

public class PrimaryMult extends CParseRule{
    CParseRule variable;
	CToken sem; //意味解析でエラー場所を表示する用

	public PrimaryMult(CParseContext pcx) {
		super("PrimaryMult");
		//variable番地情報を間接参照して参照番地に変換
		setBNF("primaryMult ::= MULT variable"); //CV04~
	}

    public static boolean isFirst(CToken tk) {
		return tk.getType() == CToken.TK_MULT;
	}

	public void parse(CParseContext pcx) throws FatalErrorException {
		// ここにやってくるときは、必ずisFirst()が満たされている
		CTokenizer ct = pcx.getTokenizer();
		CToken tk = ct.getCurrentToken(pcx);
		sem = tk;

		// *の次の字句を読む
		try {
			tk = ct.getNextToken(pcx);
			if (Variable.isFirst(tk)) {
				variable = new Variable(pcx);
				variable.parse(pcx);
			} else {
				//pcx.fatalError(tk + "primaryMult: parse(): *の後ろはvariableです");
				pcx.recoverableError(tk + " primaryMult: *の後ろはvariableです");
			}

		} catch (RecoverableErrorException e) {
			// 回復エラーだけ出して処理はstatementXXに任せる
		}
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
		if (variable != null) {
			variable.semanticCheck(pcx);
			
			try {
				if(variable.getCType().getType() != CType.T_pint){
					//pcx.fatalError("primaryMult: semanticCheck(): *の後ろは[int*]です");
					pcx.recoverableError(sem + " primaryMult: *の後ろは[int*]です");
				}
			} catch (Exception e) {
			}
			this.setCType(CType.getCType(CType.T_int));
			this.setConstant(variable.isConstant());
		}
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
		CodeGenCommon cgc = pcx.getCodeGenCommon();
		cgc.printStartComment(getBNF(getId()));
		if(variable != null){
			variable.codeGen(pcx);
			cgc.printPopCodeGen("", "R0", "PrimaryMult: variableのアドレス(番地)をpop");
			cgc.printPushCodeGen("", "(R0)", "PrimaryMult: R0番地に格納されている値(間接参照)をpush");
		}
		cgc.printCompleteComment(getBNF(getId()));
	}
}
