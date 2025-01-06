package lang.c.parse;

import lang.*;
import lang.c.*;
import java.util.ArrayList;
import java.util.List;

public class StatementBlock extends CParseRule {
	CParseRule statement;
	List<CParseRule> statementList = new ArrayList<>();

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
			try {
				//文の途中で構文エラーになるかもしれないので...
				statement = new Statement(pcx);
				statement.parse(pcx);
				statementList.add(statement);

			} catch (RecoverableErrorException e) {
				//そのときにはここで例外を捕まえて，’;’か’}’が出るまで読み飛ばして回復する（立ち直る）
				ct.skipTo(pcx, CToken.TK_SEMI, CToken.TK_RCUR);
				tk = ct.getNextToken(pcx);
			}
			tk=ct.getCurrentToken(pcx);
		}

        if(tk.getType() == CToken.TK_RCUR){
            tk = ct.getNextToken(pcx);
        }else{
			//pcx.fatalError(tk + "statementBlock: parse(): }がありません");
			pcx.warning(tk + "} を補いました");
		}
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
        if (statementList != null) {
			for(int i=0; i < statementList.size(); i++){
				statement = statementList.get(i);
				statement.semanticCheck(pcx);
			}
		}
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
        CodeGenCommon cgc = pcx.getCodeGenCommon();
		cgc.printStartComment(getBNF(getId()));
		for(int i=0; i < statementList.size(); i++){
			statement = statementList.get(i);
			statement.codeGen(pcx);
		}
		cgc.printCompleteComment(getBNF(getId()));
	}
}