package lang.c.parse;

import lang.*;
import lang.c.*;

public class StatementWhile extends CParseRule {
	CParseRule conditionBlock, statement;

	public StatementWhile(CParseContext pcx) {
		super("StatementWhile");
		setBNF("statementWhile ::= WHILE conditionBlock statement"); //CV07~
	}

	public static boolean isFirst(CToken tk) {
        return tk.getType() == CToken.TK_WHILE;
	}

	public void parse(CParseContext pcx) throws FatalErrorException {
        CTokenizer ct = pcx.getTokenizer();
		CToken tk = ct.getCurrentToken(pcx);

        // while の次のトークンを読む
        tk = ct.getNextToken(pcx);
		if(ConditionBlock.isFirst(tk)){
			conditionBlock = new ConditionBlock(pcx);
            conditionBlock.parse(pcx);
		}else{
            pcx.fatalError(tk + "StatementWhile: parse(): whileの後ろはconditionBlockです");
        }

        // conditionBlock の次のトークンを読む
        tk = ct.getNextToken(pcx);
        if(Statement.isFirst(tk)){
            statement = new Statement(pcx);
            statement.parse(pcx);
        }else{
            pcx.fatalError(tk + "StatementWhile: parse(): conditionBlockの後ろはstatementです");
        }
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
	}
}