package lang.c.parse;

import lang.*;
import lang.c.*;

public class ConditionNE  extends CParseRule {
	// program ::= expression EOF
	CToken op;
	CParseRule left, expression;

	public ConditionNE(CParseContext pcx, CParseRule left) {
		super("ConditionNE");
		this.left = left;
		setBNF("conditionNE ::= NE expression"); //CV06~
	}

	public static boolean isFirst(CToken tk) {
		return tk.getType() == CToken.TK_NE;
	}

	public void parse(CParseContext pcx) throws FatalErrorException {
		CTokenizer ct = pcx.getTokenizer();
		CToken tk = ct.getCurrentToken(pcx);
		op = ct.getCurrentToken(pcx);

		// NE != の次の字句を読む
		tk = ct.getNextToken(pcx);
		if(Expression.isFirst(tk)){
			expression = new Expression(pcx);
			expression.parse(pcx);
			
			tk = ct.getNextToken(pcx);
		}else{
			pcx.fatalError(tk + "ConditionNE: !=の後ろはexpressionです");
		}
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
		if (left != null && expression != null){
			left.semanticCheck(pcx);
			expression.semanticCheck(pcx);
			
			int lt = left.getCType().getType();//<の左辺の型
			int rt = expression.getCType().getType();//<の右辺の型
			String lts = left.getCType().toString();
			String rts = expression.getCType().toString();

			if (lt != rt){
				pcx.fatalError(op+":左辺の型["+lts+"]と右辺の型["+rts+"]が一致しないので比較できません");
			}
			this.setCType(CType.getCType(CType.T_bool));
			this.setConstant(true);
		}
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
		CodeGenCommon cgc = pcx.getCodeGenCommon();
	}
}
