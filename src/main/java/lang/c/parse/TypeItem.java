package lang.c.parse;

import lang.*;
import lang.c.*;

public class TypeItem extends CParseRule {

    boolean isExistMult = false;
    boolean isArray = false;

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
            isExistMult = true;
            tk = ct.getNextToken(pcx); // *を読み飛ばす
        }

        if(tk.getType() == CToken.TK_LBRA){
            isArray = true;
            tk = ct.getNextToken(pcx); // [を読み飛ばす
            if(tk.getType() == CToken.TK_RBRA){
                tk = ct.getNextToken(pcx); // ]を読み飛ばす
            }else{
                pcx.warning(tk + " typeItem: ] を補いました");
            }
        }

        if(isExistMult && isArray){
            this.setCType(CType.getCType(CType.T_pint_array));
        }else if(isExistMult){
            this.setCType(CType.getCType(CType.T_pint));
        }else if(isArray){
            this.setCType(CType.getCType(CType.T_int_array));
        }else{
            this.setCType(CType.getCType(CType.T_int));
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
