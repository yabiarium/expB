package lang.c.parse;

import lang.*;
import lang.c.*;

public class Call extends CParseRule {

    public Call(CParseContext pcx) {
        super("Call");
		setBNF("call ::= LPAR RPAR"); //CV12~
    }

    public static boolean isFirst(CToken tk) {
        return tk.getType() == CToken.TK_LPAR;
	}

    public void parse(CParseContext pcx) throws FatalErrorException {
        CTokenizer ct = pcx.getTokenizer();
        CToken tk = ct.getNextToken(pcx); // (を読み飛ばす

        if(tk.getType() == CToken.TK_RPAR) {
            tk = ct.getNextToken(pcx); // )を読み飛ばす, 正常終了
        }else {
            pcx.warning(tk + " call: ) を補いました");
        }
    }

    public void semanticCheck(CParseContext pcx) throws FatalErrorException {
    }

    public void codeGen(CParseContext pcx) throws FatalErrorException {
    }
}
