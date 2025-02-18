package lang.c.parse;

import lang.FatalErrorException;
import lang.c.CParseContext;
import lang.c.CParseRule;
import lang.c.CToken;
import lang.c.CTokenizer;
import lang.c.CType;
import lang.c.CodeGenCommon;

public class Primary extends CParseRule{
    CParseRule primaryMult, variable;
	boolean fromFactorAmp = false;

	public Primary(CParseContext pcx) {
		super("Primary");
		//このノードは実は何もしない：下の2つのいずれかである．コード生成はそれぞれのノードにお任せ
		//primaryは「番地」を表すもの，入れ物を特定する「名札」の機能をちゃんと持っている
		setBNF("primary ::= primaryMult | variable"); //CV04~
	}

	public Primary(CParseContext pcx, boolean fromFactorAmp) {
		super("Primary");
		this.fromFactorAmp = fromFactorAmp;
		setBNF("primary ::= primaryMult | variable"); //CV04~
	}

    public static boolean isFirst(CToken tk) {
		if(tk.getType() == CToken.TK_MULT){
			return PrimaryMult.isFirst(tk);
		}else{
			return Variable.isFirst(tk);
		}
	}

	public void parse(CParseContext pcx) throws FatalErrorException {
		// ここにやってくるときは、必ずisFirst()が満たされている
		CTokenizer ct = pcx.getTokenizer();
		CToken tk = ct.getCurrentToken(pcx);

		if(tk.getType() == CToken.TK_MULT){
			if(fromFactorAmp){
				pcx.recoverableError(tk + " primary: *の後ろに&は置けません");
			}
			primaryMult = new PrimaryMult(pcx);
			primaryMult.parse(pcx);
		}else{
			variable = new Variable(pcx, fromFactorAmp);
			variable.parse(pcx);
		}
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
		int rt;
		boolean isConst = false;

		if (primaryMult != null) {
			primaryMult.semanticCheck(pcx);
			isConst = primaryMult.isConstant();
			rt = primaryMult.getCType().getType();
		}else{ //variable != null
			variable.semanticCheck(pcx);
			isConst = variable.isConstant();
			rt = variable.getCType().getType();
		}

		this.setCType(CType.getCType(rt));
		this.setConstant(isConst);
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
		CodeGenCommon cgc = pcx.getCodeGenCommon();
		cgc.printStartComment(getBNF(getId()));
		if(primaryMult != null){
			primaryMult.codeGen(pcx);
		}else if(variable != null){
			variable.codeGen(pcx);
		}
		cgc.printCompleteComment(getBNF(getId()));
	}
}
