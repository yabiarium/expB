package lang.c.parse;

import java.util.ArrayList;
import java.util.List;

import lang.*;
import lang.c.*;

public class ArgList extends CParseRule {

    CParseRule argItem;
    List<CParseRule> argItemList = new ArrayList<>();

    public ArgList(CParseContext pcx) {
        super("ArgList");
        setBNF("argList ::= argItem { COMMA argItem }"); //CV13~
    }

    public static boolean isFirst(CToken tk) {
        return ArgItem.isFirst(tk);
	}

    public void parse(CParseContext pcx) throws FatalErrorException {
        CTokenizer ct = pcx.getTokenizer();
        CToken tk = ct.getCurrentToken(pcx);

        try {
            do{
                if(tk.getType() == CToken.TK_COMMA){
                    tk = ct.getNextToken(pcx); // ,を読み飛ばす
                }
                
                if(ArgItem.isFirst(tk)){
                    argItem = new ArgItem(pcx);
                    argItem.parse(pcx);
                    argItemList.add(argItem);
                    tk = ct.getCurrentToken(pcx); // ,か)(引数の終わり)を読む
                }else{
                    pcx.recoverableError(tk + " argList: ,の後ろに引数がありません"); //,はあるのに引数が続いていない
                }
            }while(tk.getType() == CToken.TK_COMMA);

        } catch (RecoverableErrorException e) {
            //処理はfunctionに託す
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
