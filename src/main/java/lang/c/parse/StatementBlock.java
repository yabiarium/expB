package lang.c.parse;

import lang.*;
import lang.c.*;
import java.util.ArrayList;
import java.util.List;

public class StatementBlock extends CParseRule {
	CParseRule statement;
	List<CParseRule> statements = new ArrayList<>();

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
			statements.add(statement);
            tk = ct.getCurrentToken(pcx);
		}
        if(tk.getType() != CToken.TK_RCUR){
            pcx.fatalError(tk + "StatementBlock: parse(): }がありません");
        }
		
		tk = ct.getNextToken(pcx);
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
        if (statements != null) {
			for(int i=0; i < statements.size(); i++){
				statement = statements.get(i);
				statement.semanticCheck(pcx);
			}
		}
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
        CodeGenCommon cgc = pcx.getCodeGenCommon();
		cgc.printStartComment(getBNF(getId()));
		for(int i=0; i < statements.size(); i++){
			statement = statements.get(i);
			statement.codeGen(pcx);
		}
		cgc.printCompleteComment(getBNF(getId()));
	}
}