package lang.c.parse;

import lang.FatalErrorException;
import lang.c.CParseContext;
import lang.c.CParseRule;
import lang.c.CToken;
import lang.c.CTokenizer;
//import lang.c.CType;
import lang.c.CodeGenCommon;

public class StatementInput extends CParseRule{

    CParseRule primary;

	public StatementInput(CParseContext pcx) {
		super("StatementInput");
		setBNF("statementInput ::= INPUT primary SEMI"); //CV05~
	}

    public static boolean isFirst(CToken tk) {
		return tk.getType() == CToken.TK_INPUT;
	}

	public void parse(CParseContext pcx) throws FatalErrorException {
		// ここにやってくるときは、必ずisFirst()が満たされている
		CTokenizer ct = pcx.getTokenizer();
		CToken tk = ct.getCurrentToken(pcx);

		// input の次の字句を読む
		tk = ct.getNextToken(pcx);
		if(Primary.isFirst(tk)){
			primary = new Primary(pcx);
			primary.parse(pcx);
			// primary の解析後,現在の字句を読む
			tk = ct.getCurrentToken(pcx);
			if(tk.getType() != CToken.TK_SEMI){
				pcx.fatalError(tk + "StatementInput: ;がありません");
			}
			tk = ct.getNextToken(pcx);
		}else{
			pcx.fatalError(tk + "StatementInput: inputの後ろはprimaryです");
		}
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
		//左辺の型は、T_int_array か T_pint_arrayのみ
		//↑この判定はvariableで行っているのでここでは何もする必要がない
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
		CodeGenCommon cgc = pcx.getCodeGenCommon();
		cgc.printStartComment(getBNF(getId()));
		if(expression != null){
			expression.codeGen(pcx);
			cgc.printPopCodeGen("", "R0", "expressionの結果をR0に取り出す");
			cgc.printPopCodeGen("", "R1", "配列の先頭アドレスをR1に取り出す");
			cgc.printInstCodeGen("", "ADD R1, R0", "相対アドレスを求める");
			cgc.printPushCodeGen("", "R0", "");
		}
		cgc.printCompleteComment(getBNF(getId()));
	}
}
