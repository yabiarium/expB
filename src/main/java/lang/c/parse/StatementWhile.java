package lang.c.parse;

import lang.*;
import lang.c.*;

public class StatementWhile extends CParseRule {
	CParseRule conditionBlock, statement;

	public StatementWhile(CParseContext pcx) {
		super("StatementWhile");
		setBNF("statementWhile ::= WHILE conditionBlock statement"); //CV07~
	}

	public static boolean isFirst(CToken tk) {
        return tk.getType() == CToken.TK_WHILE;
	}

	public void parse(CParseContext pcx) throws FatalErrorException {
        CTokenizer ct = pcx.getTokenizer();
		CToken tk = ct.getCurrentToken(pcx);

        // while の次のトークンを読む
        try {
            tk = ct.getNextToken(pcx);
            if(ConditionBlock.isFirst(tk)){
                conditionBlock = new ConditionBlock(pcx);
                conditionBlock.parse(pcx);
            }else{
                //pcx.fatalError(tk + "statementWhile: parse(): whileの後ろはconditionBlockです");
                pcx.recoverableError(tk + " statementWhile: whileの後ろは(です");
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
                statement = new Statement(pcx);
                statement.parse(pcx);
            }else{
                //pcx.fatalError(tk + "statementWhile: parse(): conditionBlockの後ろはstatementです");
                pcx.recoverableError(tk + " statementWhile: conditionBlockの後ろはstatementです");
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
        if (statement != null) {
			statement.semanticCheck(pcx);
		}
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
        CodeGenCommon cgc = pcx.getCodeGenCommon();
		cgc.printStartComment(getBNF(getId()));

        int seq = pcx.getSeqId("StatementWhile");
		String seqLabelWhileBegin = "WHILEBEGIN"+seq;
        String seqLabelWhileEnd = "WHILEEND"+seq;

		if (conditionBlock != null) {
            cgc.printLabel(seqLabelWhileBegin+":","StatementWhile: While条件式判定前"); //WHILEBEGIN:
            conditionBlock.codeGen(pcx); //条件式の判定
			cgc.printPopCodeGen("", "R0", "StatementWhile: condition()実行結果を取り出す"); //conditionの結果がfalseならZフラグが立つ
            cgc.printInstCodeGen("","BRZ "+seqLabelWhileEnd,"StatementWhile: zeroだったら"+seqLabelWhileEnd+"にジャンプ"); //Zフラグが立っている場合にWHILEENDにジャンプ
            statement.codeGen(pcx); // trueの時(While内)の処理内容を生成
            cgc.printInstCodeGen("","JMP "+seqLabelWhileBegin,"StatementWhile: "+seqLabelWhileBegin+"にジャンプ");//無条件にWhileの先頭へジャンプ
            
            cgc.printLabel(seqLabelWhileEnd+":","StatementWhile: While文の終了処理"); //WHILEEND:
		}

		cgc.printCompleteComment(getBNF(getId()));
	}
}