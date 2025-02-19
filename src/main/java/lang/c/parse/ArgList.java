package lang.c.parse;

import java.util.ArrayList;
import java.util.List;

import lang.*;
import lang.c.*;

public class ArgList extends CParseRule {

    CParseRule argItem;
    List<CParseRule> argItemList = new ArrayList<>();
    String functionName;
    CToken sem; //意味解析でのエラー表示に使用

    public ArgList(CParseContext pcx, String functionName) {
        super("ArgList");
        this.functionName = functionName;
        sem = pcx.getTokenizer().getCurrentToken(pcx);
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
        //関数名に紐づいている引数の情報と一致するか確認する
        CSymbolTableEntry protFunction = pcx.getSymbolTable().searchGlobal(functionName);
        List<CType> protArgTypeList = protFunction.getArgTypeList(); //プロトタイプ宣言時の引数の型のリストを取得

        for(CParseRule argItem : argItemList){
            argItem.semanticCheck(pcx);
        }

        try {
            if(protArgTypeList == null){
                if(argItemList.size() != 0){
                    pcx.recoverableError(sem + " argList: 宣言時の引数の数と一致しません");
                }
            }else{
                if(protArgTypeList.size() != argItemList.size()){
                    pcx.recoverableError(sem + " argList: 宣言時の引数の数と一致しません");
                }
                for(int i=0; i<protArgTypeList.size(); i++){
                    if(protArgTypeList.get(i) != argItemList.get(i).getCType()){
                        pcx.recoverableError(sem + " argList: 宣言時の引数の型と順番が一致しません");
                    }
                }
            }

        } catch (RecoverableErrorException e) {
        }
    }

    public void codeGen(CParseContext pcx) throws FatalErrorException {
        CodeGenCommon cgc = pcx.getCodeGenCommon();
		cgc.printStartComment(getBNF(getId()));
        
        for(CParseRule argItem : argItemList){
            argItem.codeGen(pcx);
        }
        
		cgc.printCompleteComment(getBNF(getId()));
    }
}
