package lang.c.parse;

import lang.*;
import lang.c.*;

public class TypeList extends CParseRule {

    CParseRule typeItem;

    public TypeList(CParseContext pcx) {
        super("TypeList");
        setBNF("TypeList ::= typeItem { COMMA typeItem }"); //CV13~
    }

    public static boolean isFirst(CToken tk) {
        return TypeItem.isFirst(tk);
	}

    public void parse(CParseContext pcx) throws FatalErrorException {
        CTokenizer ct = pcx.getTokenizer();
        CToken tk = ct.getCurrentToken(pcx);

        try{
            do{
                if(tk.getType() == CToken.TK_COMMA){
                    tk = ct.getNextToken(pcx); // ,を読み飛ばす
                }
                
                if(TypeItem.isFirst(tk)){
                    typeItem = new TypeItem(pcx);
                    typeItem.parse(pcx);
                    tk = ct.getCurrentToken(pcx); // ,か,以外を読む
                }else{
                    pcx.recoverableError(tk + " typeList: 型がありません"); //,はあるのに引数が続いていない
                }
            }while(tk.getType() == CToken.TK_COMMA);

        } catch (RecoverableErrorException e) {
            //処理は上の節点に託す
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
