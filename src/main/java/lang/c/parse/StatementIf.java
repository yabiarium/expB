package lang.c.parse;

import lang.*;
import lang.c.*;

public class StatementIf extends CParseRule {
	CParseRule conditionBlock, statement;

	public StatementIf(CParseContext pcx) {
		super("StatementIf");
		setBNF("statementIf ::= IF conditionBlock statement [ ELSE statement ]"); //CV07~
	}

	public static boolean isFirst(CToken tk) {
        return tk.getType() == CToken.TK_IF;
	}

	public void parse(CParseContext pcx) throws FatalErrorException {
        CTokenizer ct = pcx.getTokenizer();
		CToken tk = ct.getCurrentToken(pcx);

        // if の次のトークンを読む
        tk = ct.getNextToken(pcx);
		if(ConditionBlock.isFirst(tk)){
			conditionBlock = new ConditionBlock(pcx);
            conditionBlock.parse(pcx);
		}else{
            pcx.fatalError(tk + "StatementIf: parse(): ifの後ろはconditionBlockです");
        }

        // conditionBlock の次のトークンを読む
        tk = ct.getNextToken(pcx);
        if(Statement.isFirst(tk)){
            statement = new Statement(pcx);
            statement.parse(pcx);
        }else{
            pcx.fatalError(tk + "StatementIf: parse(): conditionBlockの後ろはstatementです");
        }

        // else がある場合
        tk = ct.getCurrentToken(pcx);
        if(tk.getType() == CToken.TK_ELSE){
            tk = ct.getNextToken(pcx);
            if(Statement.isFirst(tk)){
                statement = new Statement(pcx);
                statement.parse(pcx);
            }else{
                pcx.fatalError(tk + "StatementIf: parse(): elseの後ろはstatementです");
            }
        }
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
	}
}