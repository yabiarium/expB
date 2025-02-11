package lang.c.parse;

import lang.*;
import lang.c.*;

public class TypeItem extends CParseRule {

    public TypeItem(CParseContext pcx) {
        super("TypeItem");
        setBNF("TypeItem ::= INT [ MULT ] [ LBRA RBRA ]"); //CV13~
    }

    public static boolean isFirst(CToken tk) {
        return tk.getType() == CToken.TK_INT;
	}

    public void parse(CParseContext pcx) throws FatalErrorException {
        CTokenizer ct = pcx.getTokenizer();
        CToken tk = ct.getNextToken(pcx); // int を読み飛ばす

        if(tk.getType() == CToken.TK_MULT){
            tk = ct.getNextToken(pcx); // *を読み飛ばす
        }

        if(tk.getType() == CToken.TK_LBRA){
            tk = ct.getNextToken(pcx); // [を読み飛ばす
            if(tk.getType() == CToken.TK_RBRA){
                tk = ct.getNextToken(pcx); // ]を読み飛ばす
            }else{
                pcx.warning(tk + " typeItem: ] を補いました");
            }
        }
    }

    public void semanticCheck(CParseContext pcx) throws FatalErrorException {
    }

    public void codeGen(CParseContext pcx) throws FatalErrorException {
        CodeGenCommon cgc = pcx.getCodeGenCommon();
		cgc.printStartComment(getBNF(getId()));
		cgc.printCompleteComment(getBNF(getId()));
    }
}
