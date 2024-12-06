package lang.c.parse;

import lang.*;
import lang.c.*;

public class Program2 extends CParseRule {
	// program ::= expression EOF
	CParseRule condition;

	public Program2(CParseContext pcx) {
		super("Program2");
		setBNF("program2 ::= condition"); //CV06~
	}

	public static boolean isFirst(CToken tk) {
		return Condition.isFirst(tk);
	}

	public void parse(CParseContext pcx) throws FatalErrorException {
		CTokenizer ct = pcx.getTokenizer();
		CToken tk = ct.getCurrentToken(pcx);

		if(Condition.isFirst(tk)){
			condition = new Condition(pcx);
			condition.parse(pcx);
			
			tk = ct.getNextToken(pcx);
		}
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
		if (condition != null) {
			condition.semanticCheck(pcx);
			this.setCType(condition.getCType()); // condition の型をそのままコピー
			this.setConstant(condition.isConstant());
		}
	}

	
	public void codeGen(CParseContext pcx) throws FatalErrorException{
			CodeGenCommon cgc = pcx.getCodeGenCommon();
			if (condition != null){
				cgc.printStartComment(getBNF(getId()));
				cgc.printInstCodeGen("",".= 0x0100","Program2:開始番地");
				cgc.printInstCodeGen("","JMP __START","Program2:__STARTに飛ぶ");
				//ここには将来、変数宣言に対するコード生成が必要
				cgc.printLabel("i_a:  .word 100", "整数型変数");
				cgc.printLabel("i_b:  .word 200", "整数型変数");
				cgc.printLabel("i_c:  .word 250", "整数型変数");
				cgc.printLabel("ip_d:  .word 0x0103", "ポインタ型変数");
				cgc.printLabel("ip_e:  .word 0x0109", "ポインタ型変数");
				cgc.printLabel("ia_f:  .blkw 4", "整数型配列 要素数4");
				cgc.printLabel("ipa_g:  .blkw 4", "ポインタ型変数 要素数4");
				cgc.printLabel("c_h:  .word 400", "定数");

				cgc.printLabel("__START:","Program:ここから開始");
				cgc.printInstCodeGen("","MOV #0x1000, R6","Program:SP初期化");
				cgc.printInstCodeGen("","MOV #0xFFE0, R1", ";;inputi_a");
				cgc.printInstCodeGen("","MOV #i_a, R0",";;inputi_a");
				cgc.printInstCodeGen("","MOV (R1), (R0)",";;inputi_a");

				//programコード本体
				condition.codeGen(pcx);
				//program最後コード
				cgc.printPopCodeGen("","R0","Program2:condition結果をR0に取り出す(計算結果確認用)");
				cgc.printInstCodeGen("","HLT\t","Program:");
				cgc.printInstCodeGen("",".end\t","Program:");
				cgc.printCompleteComment(getBNF(getId()));
			}
	}
}
