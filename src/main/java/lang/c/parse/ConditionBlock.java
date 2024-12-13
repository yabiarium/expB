package lang.c.parse;

import lang.*;
import lang.c.*;

public class ConditionBlock  extends CParseRule {
	CParseRule condition;

	public ConditionBlock(CParseContext pcx) {
		super("ConditionBlock");
		setBNF("conditionBlock ::= LPAR condition RPAR"); //CV07~
	}

	public static boolean isFirst(CToken tk) {
        return tk.getType() == CToken.TK_LPAR;
	}

	public void parse(CParseContext pcx) throws FatalErrorException {
        CTokenizer ct = pcx.getTokenizer();
		CToken tk = ct.getCurrentToken(pcx);

        // ( の次のトークンを読む
        tk = ct.getNextToken(pcx);
		if(Condition.isFirst(tk)){
			condition = new Condition(pcx);
            condition.parse(pcx);
		}else{
            pcx.fatalError(tk + "ConditionBlock: parse(): (の後ろはconditionです");
        }

        // condition の次のトークンを読む
        tk = ct.getCurrentToken(pcx);
		if(tk.getType() != CToken.TK_RPAR){
            pcx.fatalError(tk + "ConditionBlock: parse(): )がありません");
        }
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
        if(condition != null){
            condition.semanticCheck(pcx);
        }
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
		CodeGenCommon cgc = pcx.getCodeGenCommon();
		cgc.printStartComment(getBNF(getId()));
		if (condition != null) {
            condition.codeGen(pcx);
		}
		cgc.printCompleteComment(getBNF(getId()));
	}
}