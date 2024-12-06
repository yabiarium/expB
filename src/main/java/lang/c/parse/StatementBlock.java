package lang.c.parse;

import lang.*;
import lang.c.*;

public class StatementBlock extends CParseRule {
	CParseRule statement;

	public StatementBlock(CParseContext pcx) {
		super("StatementBlock");
		setBNF("statementBlock ::= LCUR { statement } RCUR"); //CV07~
	}

	public static boolean isFirst(CToken tk) {
        return tk.getType() == CToken.TK_LCUR;
	}

	public void parse(CParseContext pcx) throws FatalErrorException {
        CTokenizer ct = pcx.getTokenizer();
		CToken tk = ct.getCurrentToken(pcx);

        // { の次のトークンを読む
        tk = ct.getNextToken(pcx);
		while(Statement.isFirst(tk)){
			statement = new Statement(pcx);
            statement.parse(pcx);
            tk = ct.getCurrentToken(pcx);
		}
        if(tk.getType() != CToken.TK_RCUR){
            pcx.fatalError(tk + "StatementBlock: parse(): }がありません");
        }
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
	}
}