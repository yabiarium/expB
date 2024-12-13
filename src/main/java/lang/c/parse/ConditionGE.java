package lang.c.parse;

import lang.*;
import lang.c.*;

public class ConditionGE  extends CParseRule {
	// program ::= expression EOF
	CToken op;
	CParseRule left, expression;

	public ConditionGE(CParseContext pcx, CParseRule left) {
		super("ConditionGE");
		this.left = left;
		setBNF("conditionGE ::= GE expression"); //CV06~
	}

	public static boolean isFirst(CToken tk) {
		return tk.getType() == CToken.TK_GE;
	}

	public void parse(CParseContext pcx) throws FatalErrorException {
		CTokenizer ct = pcx.getTokenizer();
		CToken tk = ct.getCurrentToken(pcx);
		op = ct.getCurrentToken(pcx);

		// LT >= の次の字句を読む
		tk = ct.getNextToken(pcx);
		if(Expression.isFirst(tk)){
			expression = new Expression(pcx);
			expression.parse(pcx);
		}else{
			pcx.fatalError(tk + "ConditionGE: >=の後ろはexpressionです");
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
				pcx.fatalError(op+":左辺の型["+lts+"]と右辺の型["+rts+"]が一致しないので比較できません");
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

			int seq = pcx.getSeqId("ConditionGE");
			String seqLabel = "GE"+seq;
			String lt = left.getCType().toString();
			String rt = expression.getCType().toString();
			String t = getCType().toString();

			cgc.printPopCodeGen("","R1","ConditionGE:右辺の値を取り出す:["+rt+"]");
			cgc.printPopCodeGen("","R0","ConditionGE:左辺の値を取り出す:["+lt+"]");
			cgc.printInstCodeGen("","MOV #"+CToken.TRUE_NUM+",R2","ConditionGE:R2にtrue"+CToken.TRUE_NUM+"をセット");
			cgc.printInstCodeGen("","CMP R0,R1","ConditionGE:R1<R0 ==>>"+"R1-R0<0(negativeflag)?");
			cgc.printInstCodeGen("","BRN "+seqLabel,"ConditionGE:negativeだったら"+seqLabel+"にジャンプ");
			cgc.printInstCodeGen("","BRZ "+seqLabel,"ConditionGE:zeroだったら"+seqLabel+"にジャンプ");
			cgc.printInstCodeGen("","CLR R2","ConditionGE:negativeでもzeroでもなかったらfalse"+CToken.FALSE_NUM+"をR2にセット");
			//もし，CToken.FALSE_NUMを"0xFFFF"で設定している場合は上の行は↓で書き換えること
			//"0x0000"の場合も以下のコードでもいいが，CLR R2のほうが1WORDで実現できる（下記コードは2WORD使う)
			//cgc.printInstCodeGen("","MOV #"+CToken.FALSE_NUM+",R2","ConditionLT:negativeじゃなかったら"+CToken.FALSE_NUM+"をR2にセット");
			cgc.printLabel(seqLabel+":","ConditionGE:negativeかzeroだったときのジャンプ先");
			cgc.printPushCodeGen("","R2","ConditionGE:条件式評価結果R2["+t+"]をスタックに積む");
			cgc.printCompleteComment(getBNF(getId()));
		}
	}
}
