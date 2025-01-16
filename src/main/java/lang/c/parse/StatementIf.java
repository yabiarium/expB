package lang.c.parse;

import lang.*;
import lang.c.*;

public class StatementIf extends CParseRule {
	CParseRule conditionBlock, statement1, statement2;

	public StatementIf(CParseContext pcx) {
		super("StatementIf");
		setBNF("statementIf ::= IF conditionBlock statement [ ELSE statement ]"); //CV07~
	}

	public static boolean isFirst(CToken tk) {
        return tk.getType() == CToken.TK_IF;
	}

	public void parse(CParseContext pcx) throws FatalErrorException {
        CTokenizer ct = pcx.getTokenizer();
		CToken tk = ct.getCurrentToken(pcx);

        // if の次のトークンを読む
        try {
            tk = ct.getNextToken(pcx);
            if(ConditionBlock.isFirst(tk)){
                conditionBlock = new ConditionBlock(pcx);
                conditionBlock.parse(pcx);
            }else{
                //pcx.fatalError(tk + "statementIf: parse(): ifの後ろはconditionBlockです");
                pcx.recoverableError(tk + " statementIf: ifの後ろは(です");
            }
            
        } catch (RecoverableErrorException e) {
            // ) { ; まで飛ばす
            ct.skipTo(pcx, CToken.TK_RPAR, CToken.TK_LCUR, CToken.TK_SEMI);
            if(ct.getCurrentToken(pcx).getType() != CToken.TK_LCUR){ // 現在のトークンが{ならStatement→StatementBlockのisFirstのためにNextTokenしない
                tk = ct.getNextToken(pcx);
            }
        }
		

        try {
            // conditionBlock の次のトークンを読む
            tk = ct.getCurrentToken(pcx);
            if(Statement.isFirst(tk)){
                statement1 = new Statement(pcx);
                statement1.parse(pcx);
            }else{
                //pcx.fatalError(tk + "statementIf: parse(): conditionBlockの後ろはstatementです");
                pcx.recoverableError(tk + " statementIf: conditionBlockの後ろはstatementです");
            }

            // else がある場合
            tk = ct.getCurrentToken(pcx);
            if(tk.getType() == CToken.TK_ELSE){
                tk = ct.getNextToken(pcx);
                if(Statement.isFirst(tk)){
                    statement2 = new Statement(pcx);
                    statement2.parse(pcx);
                }else{
                    //pcx.fatalError(tk + "statementIf: parse(): elseの後ろはstatementです");
                    pcx.recoverableError(tk + " statementIf: elseの後ろはstatementです");
                }
            }
        } catch (RecoverableErrorException e) {
            // ; まで飛ばす
            ct.skipTo(pcx, CToken.TK_SEMI);
            tk = ct.getNextToken(pcx);
        }
        
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
        if (conditionBlock != null) {
            conditionBlock.semanticCheck(pcx);
        }
        if (statement1 != null) {
			statement1.semanticCheck(pcx);
		}
        if (statement2 != null) {
            statement2.semanticCheck(pcx);
        }
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
        CodeGenCommon cgc = pcx.getCodeGenCommon();
		cgc.printStartComment(getBNF(getId()));

        int seq = pcx.getSeqId("StatementIf");
		String seqLabelElse = "ELSE"+seq;
        String seqLabelIfend = "IFEND"+seq;

		if (conditionBlock != null) {
            conditionBlock.codeGen(pcx);
			cgc.printPopCodeGen("", "R0", "StatementIf: condition()実行結果を取り出す"); //conditionの結果がfalseならZフラグが立つ
            cgc.printInstCodeGen("","BRZ "+seqLabelElse,"StatementIf: zeroだったら"+seqLabelElse+"にジャンプ"); //Zフラグが立っている場合にELSE1にジャンプ;;falseが0の場合
            statement1.codeGen(pcx); // trueの時の処理内容を生成
            cgc.printInstCodeGen("","JMP "+seqLabelIfend,"StatementIf: "+seqLabelIfend+"にジャンプ");//IFEND1へのジャンプ命令(ELSEの処理を飛ばして終了処理へ)
            
            cgc.printLabel(seqLabelElse+":","StatementIf: falseの時のジャンプ先"); //ELSE1:
            // elseの処理がある場合
            if (statement2 != null){
                statement2.codeGen(pcx);
            }
            
            cgc.printLabel(seqLabelIfend+":","StatementIf: if文の終了処理"); //IFEND1:
		}

		cgc.printCompleteComment(getBNF(getId()));
	}
}