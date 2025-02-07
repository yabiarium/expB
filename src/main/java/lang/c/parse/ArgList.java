package lang.c.parse;

import lang.*;
import lang.c.*;

public class ArgList extends CParseRule {

    public ArgList(CParseContext pcx) {
        super("ArgList");
        setBNF("argList ::= argItem { COMMA argItem }"); //CV13~
    }

    public static boolean isFirst(CToken tk) {
        return ArgItem.isFirst(tk);
	}

    public void parse(CParseContext pcx) throws FatalErrorException {
        CTokenizer ct = pcx.getTokenizer();
        CToken tk = ct.getNextToken(pcx); // func を読み飛ばす
    }

    public void semanticCheck(CParseContext pcx) throws FatalErrorException {
    }

    public void codeGen(CParseContext pcx) throws FatalErrorException {
        CodeGenCommon cgc = pcx.getCodeGenCommon();
		cgc.printStartComment(getBNF(getId()));
		cgc.printCompleteComment(getBNF(getId()));
    }
}
