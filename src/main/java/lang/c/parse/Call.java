package lang.c.parse;

import java.util.ArrayList;
import java.util.List;

import lang.*;
import lang.c.*;

public class Call extends CParseRule {

    CParseRule expression;
    List<CParseRule> expressionList = new ArrayList<>();

    public Call(CParseContext pcx) {
        super("Call");
		//setBNF("call ::= LPAR RPAR"); //CV12~
        setBNF("call ::= LPAR [ expressoin { COMMA expression } ] RPAR"); //CV13~
    }

    public static boolean isFirst(CToken tk) {
        return tk.getType() == CToken.TK_LPAR;
	}

    public void parse(CParseContext pcx) throws FatalErrorException {
        CTokenizer ct = pcx.getTokenizer();
        CToken tk = ct.getNextToken(pcx); // (を読み飛ばす

        try {
            if(Expression.isFirst(tk)){
                do{
                    if(tk.getType() == CToken.TK_COMMA){
                        tk = ct.getNextToken(pcx); // ,を読み飛ばす
                    }
                    
                    if(Expression.isFirst(tk)){
                        expression = new Expression(pcx);
                        expression.parse(pcx);
                        expressionList.add(expression);
                        tk = ct.getCurrentToken(pcx); // ,か)(引数の終わり)を読む
                    }else{
                        pcx.recoverableError(tk + " call: ,の後ろに引数がありません"); //,はあるのに引数が続いていない
                    }
                }while(tk.getType() == CToken.TK_COMMA);
            }
        } catch (RecoverableErrorException e) {
            // 処理は上の節点に任せる
        }

        if(tk.getType() == CToken.TK_RPAR) {
            tk = ct.getNextToken(pcx); // )を読み飛ばす, 正常終了
        }else {
            pcx.warning(tk + " call: ) を補いました");
        }
    }

    public void semanticCheck(CParseContext pcx) throws FatalErrorException {
        for(CParseRule expression : expressionList) {
            expression.semanticCheck(pcx);
            this.setCType(expression.getCType());
            this.setConstant(expression.isConstant());
        }
    }

    public void codeGen(CParseContext pcx) throws FatalErrorException {
        CodeGenCommon cgc = pcx.getCodeGenCommon();
		cgc.printStartComment(getBNF(getId()));
        
        //引数を後ろから順にスタックに積む
        for(int i = expressionList.size() - 1; i >= 0; i--) {
            CParseRule exp =  expressionList.get(i);
            exp.codeGen(pcx);
            cgc.printInstCodeGen("", "","Call: 引数が積まれた");
            //cgc.printPushCodeGen("","R1", "Call: 引数を積む");//引数の計算結果をスタックに積む
        }
        
		cgc.printCompleteComment(getBNF(getId()));
    }
}
