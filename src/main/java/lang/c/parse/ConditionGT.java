package lang.c.parse;

import lang.*;
import lang.c.*;

public class ConditionGT  extends CParseRule {
	// program ::= expression EOF
	CToken op;
	CParseRule left, expression;

	public ConditionGT(CParseContext pcx, CParseRule left) {
		super("ConditionGT");
		this.left = left;
		setBNF("conditionGT ::= GT expression"); //CV06~
	}

	public static boolean isFirst(CToken tk) {
		return tk.getType() == CToken.TK_GT;
	}

	public void parse(CParseContext pcx) throws FatalErrorException {
		CTokenizer ct = pcx.getTokenizer();
		CToken tk = ct.getCurrentToken(pcx);
		op = ct.getCurrentToken(pcx);

		// LT > の次の字句を読む
		tk = ct.getNextToken(pcx);
		if(Expression.isFirst(tk)){
			expression = new Expression(pcx);
			expression.parse(pcx);
		}else{
			pcx.fatalError(tk + "conditionGT: parse(): >の後ろはexpressionです");
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

			if (lt != rt){
				pcx.fatalError(op+"conditionGT: semanticCheck(): 左辺の型["+lts+"]と右辺の型["+rts+"]が一致しないので比較できません");
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

			int seq = pcx.getSeqId("ConditionGT");
			String seqLabel = "GT"+seq;
			String lt = left.getCType().toString();
			String rt = expression.getCType().toString();
			String t = getCType().toString();

			cgc.printPopCodeGen("","R1","ConditionGT:右辺の値を取り出す:["+rt+"]");
			cgc.printPopCodeGen("","R0","ConditionGT:左辺の値を取り出す:["+lt+"]");
			cgc.printInstCodeGen("","MOV #"+CToken.TRUE_NUM+",R2","ConditionGT:R2にtrue"+CToken.TRUE_NUM+"をセット");
			cgc.printInstCodeGen("","CMP R0,R1","ConditionGT:R1<R0 ==>>"+"R1-R0<0(negativeflag)?");
			cgc.printInstCodeGen("","BRN "+seqLabel,"ConditionGT:negativeだったら"+seqLabel+"にジャンプ");
			cgc.printInstCodeGen("","CLR R2","ConditionGT:negativeじゃなかったらfalse"+CToken.FALSE_NUM+"をR2にセット");
			//もし，CToken.FALSE_NUMを"0xFFFF"で設定している場合は上の行は↓で書き換えること
			//"0x0000"の場合も以下のコードでもいいが，CLR R2のほうが1WORDで実現できる（下記コードは2WORD使う)
			//cgc.printInstCodeGen("","MOV #"+CToken.FALSE_NUM+",R2","ConditionLT:negativeじゃなかったら"+CToken.FALSE_NUM+"をR2にセット");
			cgc.printLabel(seqLabel+":","ConditionGT:negativeだったときのジャンプ先");
			cgc.printPushCodeGen("","R2","ConditionGT:条件式評価結果R2["+t+"]をスタックに積む");
			cgc.printCompleteComment(getBNF(getId()));
		}
	}
}
