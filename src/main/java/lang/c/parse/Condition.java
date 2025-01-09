package lang.c.parse;

import lang.*;
import lang.c.*;

public class Condition extends CParseRule {
	// program ::= expression EOF
	CParseRule expression, conditionXX;
	CToken conTrue, conFalse;

	public Condition(CParseContext pcx) {
		super("Condition");
		setBNF("condition ::= TRUE | FALSE | expression ( conditionLT | conditionLE | conditionGT | conditionGE | conditionEQ | conditionNE )"); //CV06~
	}

	public static boolean isFirst(CToken tk) {
		return tk.getType() == CToken.TK_TRUE || tk.getType() == CToken.TK_FALSE || Expression.isFirst(tk);
	}

	public void parse(CParseContext pcx) throws FatalErrorException {
		CTokenizer ct = pcx.getTokenizer();
		CToken tk = ct.getCurrentToken(pcx);
		
		if(tk.getType() == CToken.TK_TRUE){
			conTrue = tk;
			tk = ct.getNextToken(pcx);
		}if(tk.getType() == CToken.TK_FALSE){
			conFalse = tk;
			tk = ct.getNextToken(pcx);
		}
		
		if(Expression.isFirst(tk)){
			try {
				expression = new Expression(pcx);
				expression.parse(pcx);
				
				tk = ct.getCurrentToken(pcx);
				if(ConditionLT.isFirst(tk)) conditionXX = new ConditionLT(pcx, expression);
				if(ConditionLE.isFirst(tk)) conditionXX = new ConditionLE(pcx, expression);
				if(ConditionGT.isFirst(tk)) conditionXX = new ConditionGT(pcx, expression);
				if(ConditionGE.isFirst(tk)) conditionXX = new ConditionGE(pcx, expression);
				if(ConditionEQ.isFirst(tk)) conditionXX = new ConditionEQ(pcx, expression);
				if(ConditionNE.isFirst(tk)) conditionXX = new ConditionNE(pcx, expression);

				if(conditionXX == null){
					//pcx.fatalError(tk + "condition: parse(): expressionの後ろにはconditionXXが必要です");
					pcx.recoverableError(tk + "condition: expressionの後ろにはconditionXXが必要です");
				}else{
					conditionXX.parse(pcx);
				}
			} catch (RecoverableErrorException e) {
				// ; ) {まで読み飛ばす処理はconditionBlockに継ぐ
			}
			
		}
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
		if(conTrue != null || conFalse != null){
			this.setCType(CType.getCType(CType.T_bool));
			this.setConstant(true);
		}if (conditionXX != null) {
			conditionXX.semanticCheck(pcx);
			this.setCType(conditionXX.getCType()); // expression の型をそのままコピー
			this.setConstant(conditionXX.isConstant());
		}
	}

	
	public void codeGen(CParseContext pcx) throws FatalErrorException {
		CodeGenCommon cgc = pcx.getCodeGenCommon();
		cgc.printStartComment(getBNF(getId()));
		if(conTrue != null){
			cgc.printPushCodeGen("","#"+CToken.TRUE_NUM,"true をスタックに積む");
		}if(conFalse != null){
			cgc.printPushCodeGen("","#"+CToken.FALSE_NUM,"false をスタックに積む");
		}if (conditionXX != null) { //expression != null
			conditionXX.codeGen(pcx);
		}
		cgc.printCompleteComment(getBNF(getId()));
	}
}
