package lang.c.parse;

import java.util.ArrayList;
import java.util.List;

import lang.*;
import lang.c.*;

public class TypeList extends CParseRule {

    CParseRule typeItem;
    String functionName; //このTypeListが属する関数の名前
    List<CType> argTypeList = new ArrayList<CType>(); // 引数の型のリスト

    public TypeList(CParseContext pcx, String functionName) {
        super("TypeList");
        this.functionName = functionName;
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
                    argTypeList.add(typeItem.getCType()); // 引数の型を取得
                    tk = ct.getCurrentToken(pcx); // ,か,以外を読む
                }else{
                    pcx.recoverableError(tk + " typeList: ,の後ろに型がありません"); //,はあるのに引数が続いていない
                }
            }while(tk.getType() == CToken.TK_COMMA);

        } catch (RecoverableErrorException e) {
            //処理は上の節点に託す
        }

        //関数名に引数の型リストを紐づける
        if(argTypeList != null){
            pcx.getSymbolTable().searchGlobal(functionName).setArgTypeList(argTypeList);
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
