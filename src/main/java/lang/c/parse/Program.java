package lang.c.parse;

import lang.*;
import lang.c.*;
import java.util.ArrayList;
import java.util.List;

public class Program extends CParseRule {
	// 新しく非終端記号に対応するクラスを作成する際は，必ず拡張BNF をコメントでつけること
	// また，更新する際は，拡張BNFの「履歴」を残すこと（例えば，実験３まで：．．．． と 実験４から：．．． のように）
	// program ::= expression EOF
	CParseRule statement;
	List<CParseRule> statements = new ArrayList<>();

	public Program(CParseContext pcx) {
		super("Program");
		//setBNF("Program ::= Expression EOF");
		//setBNF("program ::= { statement } EOF"); //CV05~
		setBNF("program ::= { declaration } { statement } EOF"); //CV10~
	}

	public static boolean isFirst(CToken tk) {
		return Declaration.isFirst(tk) || Statement.isFirst(tk) || tk.getType() == CToken.TK_EOF;
	}

	public void parse(CParseContext pcx) throws FatalErrorException {
		// ここにやってくるときは、必ずisFirst()が満たされている
		CTokenizer ct = pcx.getTokenizer();
		CToken tk = ct.getCurrentToken(pcx);

		while(Statement.isFirst(tk)){
			statement = new Statement(pcx);
			statement.parse(pcx);
			statements.add(statement);
			tk = ct.getCurrentToken(pcx);
		}
		
		if (tk.getType() != CToken.TK_EOF) {
			//pcx.fatalError(tk + "program: parse(): プログラムの最後にゴミがあります");
			pcx.warning(tk + "program: プログラムの最後にゴミがあります");
		}
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
		if (statements != null) {
			for(int i=0; i < statements.size(); i++){
				statement = statements.get(i);
				statement.semanticCheck(pcx);
			}
		}
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
		CodeGenCommon cgc = pcx.getCodeGenCommon();

		if (statements != null) {
			cgc.printStartComment(getBNF(getId()));
			cgc.printInstCodeGen("", ".= 0x0100", "Program: 開始番地");
			cgc.printInstCodeGen("", "JMP __START", "Program: __STARTに飛ぶ");
			// ここには将来、変数宣言に対するコード生成が必要 CV04~
			// cgc.printLabel("i_a:	.word 100", "通常変数(1word)割当と初期化");
			// cgc.printLabel("ia_a:	.blkw 10", "配列変数(10要素)の割当");
			cgc.printLabel("i_a:  .word 100", "整数型変数");
			cgc.printLabel("i_b:  .word 200", "整数型変数");
			cgc.printLabel("i_c:  .word 250", "整数型変数");
			cgc.printLabel("ip_d:  .word 0x0103", "ポインタ型変数");
			cgc.printLabel("ip_e:  .word 0x0109", "ポインタ型変数");
			cgc.printLabel("ia_f:  .blkw 4", "整数型配列 要素数4");
			cgc.printLabel("ipa_g:  .blkw 4", "ポインタ型変数 要素数4");
			cgc.printLabel("c_h:  .word 400", "定数");

			cgc.printLabel("__START:", "Program: ここから開始");
			cgc.printInstCodeGen("", "MOV #0x1000, R6", "Program: SP初期化");

			for(int i=0; i < statements.size(); i++){
				// program コード本体
				statement = statements.get(i);
				statement.codeGen(pcx);
			}
			
			// program 最後コード
			cgc.printPopCodeGen("", "R0", "Program: 計算結果をR0に取り出す(計算結果確認用)");
			cgc.printInstCodeGen("", "HLT\t", "Program:");
			cgc.printInstCodeGen("", ".end\t", "Program:");
			cgc.printCompleteComment(getBNF(getId()));
		}
	}
}
