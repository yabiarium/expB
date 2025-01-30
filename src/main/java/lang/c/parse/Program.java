package lang.c.parse;

import lang.*;
import lang.c.*;

import java.util.ArrayList;
import java.util.List;

public class Program extends CParseRule {
	// 新しく非終端記号に対応するクラスを作成する際は，必ず拡張BNF をコメントでつけること
	// また，更新する際は，拡張BNFの「履歴」を残すこと（例えば，実験３まで：．．．． と 実験４から：．．． のように）
	// program ::= expression EOF
	CParseRule declaration, function;
	List<CParseRule> functionList = new ArrayList<>();
	List<CParseRule> declarationList = new ArrayList<>();

	public Program(CParseContext pcx) {
		super("Program");
		//setBNF("Program ::= Expression EOF");
		//setBNF("program ::= { statement } EOF"); //CV05~
		//setBNF("program ::= { declaration } { statement } EOF"); //CV10~
		//setBNF("program ::= { declaration } { declBlock } EOF"); //CV11~
		setBNF("program ::= { declaraion } { function } EOF"); //CV12~
	}

	public static boolean isFirst(CToken tk) {
		return Declaration.isFirst(tk) || Function.isFirst(tk) || tk.getType() == CToken.TK_EOF;
	}

	public void parse(CParseContext pcx) throws FatalErrorException {
		// ここにやってくるときは、必ずisFirst()が満たされている
		CTokenizer ct = pcx.getTokenizer();
		CToken tk = ct.getCurrentToken(pcx);

		while (Declaration.isFirst(tk)) {
			declaration = new Declaration(pcx);
			declaration.parse(pcx);
			declarationList.add(declaration);
			tk = ct.getCurrentToken(pcx);	
		}

		while(Function.isFirst(tk)){
			function = new Function(pcx);
			function.parse(pcx);
			functionList.add(function);
			tk = ct.getCurrentToken(pcx);
		}
		
		if (tk.getType() != CToken.TK_EOF) {
			//pcx.fatalError(tk + "program: parse(): プログラムの最後にゴミがあります");
			pcx.warning(tk + " program: プログラムの最後にゴミがあります");
		}
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
		if (declarationList != null) {
			for(CParseRule item : declarationList){
				item.semanticCheck(pcx);
			}
		}

		if (functionList != null) {
			for(CParseRule item : functionList){
				item.semanticCheck(pcx);
			}
		}
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
		CodeGenCommon cgc = pcx.getCodeGenCommon();

		if (declarationList != null || functionList != null) {
			cgc.printStartComment(getBNF(getId()));
			cgc.printInstCodeGen("", ".= 0x0100", "Program: 開始番地");
			cgc.printInstCodeGen("", "JMP __START", "Program: __STARTに飛ぶ");
			// ここには将来、変数宣言に対するコード生成が必要 CV10~
			if (declarationList != null) {
				for (CParseRule item : declarationList) {
					item.codeGen(pcx);
				}
			}

			cgc.printLabel("__START:", "Program: ここから開始");
			cgc.printInstCodeGen("", "MOV #0x1000, R6", "Program: SP初期化");

			for(CParseRule item: functionList){
				// program コード本体
				item.codeGen(pcx);
			}
			
			// program 最後コード
			cgc.printInstCodeGen("", "HLT\t", "Program:");
			cgc.printInstCodeGen("", ".end\t", "Program:");
			cgc.printCompleteComment(getBNF(getId()));
		}
	}
}
