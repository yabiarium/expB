package lang.c.parse;

import java.util.ArrayList;
import java.util.List;

import lang.*;
import lang.c.*;

public class StatementCall extends CParseRule {
    //返り値を取得しないcall

    CParseRule ident, expression;
    List<CParseRule> expressionList = new ArrayList<>();
    String functionName;
    CToken sem;

    public StatementCall(CParseContext pcx) {
        super("StatementCall");
		//setBNF("statementCall ::= CALL ident LPAR RPAR SEMI"); //CV12~
        setBNF("statementCall ::= CALL ident LPAR [ expression { COMMA expression } ] RPAR SEMI"); //CV13~
    }

    public static boolean isFirst(CToken tk) {
        return tk.getType() == CToken.TK_CALL;
	}

    public void parse(CParseContext pcx) throws FatalErrorException {
        CTokenizer ct = pcx.getTokenizer();
        CToken tk = ct.getNextToken(pcx); // call を読み飛ばす

        try {
            if(Ident.isFirst(tk)) {
                functionName = tk.getText();
                sem = tk;
                ident = new Ident(pcx);
                ident.parse(pcx);
                tk = ct.getCurrentToken(pcx); // identの後の(を読む
            }else {
                pcx.recoverableError(tk + " statementCall: 識別子(ident)がありません");
            }
        } catch (RecoverableErrorException e) {
            // (か ; まで読み飛ばす　(expression内部の(まで飛んでしまうかもしれないが気にしないことにする)
            ct.skipTo(pcx, CToken.TK_LPAR, CToken.TK_SEMI); // ;まで飛んでしまってもこの後の処理でNextTokenされてから次の節点へ行くのでこのままで問題ない
        }

        if(tk.getType() == CToken.TK_LPAR) {
            tk = ct.getNextToken(pcx); // (を読み飛ばす
        }else {
            pcx.warning(tk + " statementCall: ( を補いました");
        }

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
                        pcx.recoverableError(tk + " statementCall: ,の後ろに引数がありません"); //,はあるのに引数が続いていない
                    }
                }while(tk.getType() == CToken.TK_COMMA);
            }
        } catch (RecoverableErrorException e) {
            //  ; まで読み飛ばす ( )までだとexpression内部で止まってしまう可能性がある )
            ct.skipTo(pcx, CToken.TK_RPAR, CToken.TK_SEMI); // ;に飛んでもこの後の処理でNextTokenされてから次の節点へ行くのでこのままで問題ない
        }

        if(tk.getType() == CToken.TK_RPAR) {
            tk = ct.getNextToken(pcx); // )を読み飛ばす
        }else {
            pcx.warning(tk + " statementCall: ) を補いました");
        }

        if(tk.getType() == CToken.TK_SEMI) {
            tk = ct.getNextToken(pcx); // ;を読み飛ばす, 正常終了
        }else {
            pcx.warning(tk + " statementCall: ; を補いました");
        }
    }

    public void semanticCheck(CParseContext pcx) throws FatalErrorException {
        try {
            if(ident != null){
                ident.semanticCheck(pcx);
                CSymbolTableEntry function = pcx.getSymbolTable().searchGlobal(functionName);
                if(function == null || !function.isFunction()){ //グローバル変数として登録されていないか、登録されているが関数でない場合
                    pcx.recoverableError(sem + " statementCall: この変数は関数ではありません");
                }
            }
        } catch (RecoverableErrorException e) {
        }
        
    }

    public void codeGen(CParseContext pcx) throws FatalErrorException {
        CodeGenCommon cgc = pcx.getCodeGenCommon();
		cgc.printStartComment(getBNF(getId()));

        if(ident != null){
            ident.codeGen(pcx);
        }

		cgc.printCompleteComment(getBNF(getId()));
    }
}
