package lang.c.parse;

import lang.*;
import lang.c.*;

public class ConditionNE  extends CParseRule {
	// program ::= expression EOF
	CToken op;
	CParseRule left, expression;

	public ConditionNE(CParseContext pcx, CParseRule left) {
		super("ConditionNE");
		this.left = left;
		setBNF("conditionNE ::= NE expression"); //CV06~
	}

	public static boolean isFirst(CToken tk) {
		return tk.getType() == CToken.TK_NE;
	}

	public void parse(CParseContext pcx) throws FatalErrorException {
		CTokenizer ct = pcx.getTokenizer();
		CToken tk = ct.getCurrentToken(pcx);
		op = ct.getCurrentToken(pcx);

		// NE != の次の字句を読む
		try {
			tk = ct.getNextToken(pcx);
			if(Expression.isFirst(tk)){
				expression = new Expression(pcx);
				expression.parse(pcx);
			}else{
				//pcx.fatalError(tk + "conditionNE: parse(): !=の後ろはexpressionです");
				pcx.recoverableError(tk + " conditionNE: !=の後ろはexpressionです");
			}
		} catch (RecoverableErrorException e) {
			// ; ) まで読み飛ばす処理はconditionBlockに継ぐ
		}
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
		if (left != null && expression != null){
			left.semanticCheck(pcx);
			expression.semanticCheck(pcx);
			
			int lt = left.getCType().getType();//<の左辺の型
			int rt = expression.getCType().getType();//<の右辺の型
			String lts = left.getCType().toString();
			String rts = expression.getCType().toString();

			try {
				if (lt != rt){
					//pcx.fatalError(op + " conditionNE: semanticCheck(): 左辺の型["+lts+"]と右辺の型["+rts+"]が一致しないので比較できません");
					pcx.recoverableError(op + " conditionNE: 左辺の型["+lts+"]と右辺の型["+rts+"]が一致しないので比較できません");
				}
			} catch (RecoverableErrorException e) {
			}
			this.setCType(CType.getCType(CType.T_bool));
			this.setConstant(true);
		}
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
		CodeGenCommon cgc = pcx.getCodeGenCommon();

		if (left != null && expression != null){
			cgc.printStartComment(getBNF(getId()));
			left.codeGen(pcx);//左部分木のコード生成を頼む
			expression.codeGen(pcx);//右部分木のコード生成を頼む

			int seq = pcx.getSeqId("ConditionNE");
			String seqLabel = "NE"+seq;
			String lt = left.getCType().toString();
			String rt = expression.getCType().toString();
			String t = getCType().toString();

			cgc.printPopCodeGen("","R0","ConditionNE: 右辺の値を取り出す:["+rt+"]");
			cgc.printPopCodeGen("","R1","ConditionNE: 左辺の値を取り出す:["+lt+"]");
			cgc.printInstCodeGen("","MOV #"+CToken.FALSE_NUM+",R2","ConditionNE: R2にfalse"+CToken.FALSE_NUM+"をセット");
			cgc.printInstCodeGen("","CMP R0,R1","ConditionNE: R1==R0 ==>>"+"R1-R0==0(zeroflag)?");
			cgc.printInstCodeGen("","BRZ "+seqLabel,"ConditionNE: zeroだったら"+seqLabel+"にジャンプ");
			cgc.printInstCodeGen("","MOV #0x0001, R2","ConditionNE: zeroでなかったらtrue"+CToken.TRUE_NUM+"をR2にセット");
			//もし，CToken.FALSE_NUMを"0xFFFF"で設定している場合は上の行は↓で書き換えること
			//"0x0000"の場合も以下のコードでもいいが，CLR R2のほうが1WORDで実現できる（下記コードは2WORD使う)
			//cgc.printInstCodeGen("","MOV #"+CToken.FALSE_NUM+",R2","ConditionLT:negativeじゃなかったら"+CToken.FALSE_NUM+"をR2にセット");
			cgc.printLabel(seqLabel+":","ConditionNE: zeroだったときのジャンプ先");
			cgc.printPushCodeGen("","R2","ConditionNE: 条件式評価結果R2["+t+"]をスタックに積む");
			cgc.printCompleteComment(getBNF(getId()));
		}
	}

}
