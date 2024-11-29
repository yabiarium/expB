package lang.c.parse;

import lang.FatalErrorException;
import lang.c.CParseContext;
import lang.c.CParseRule;
import lang.c.CToken;
import lang.c.CTokenizer;
import lang.c.CType;
//import lang.c.CType;
import lang.c.CodeGenCommon;

public class StatementOutput extends CParseRule{

    CParseRule expression;

	public StatementOutput(CParseContext pcx) {
		super("StatementOutput");
		setBNF("statementOutput ::= OUTPUT expression SEMI"); //CV05~
	}

    public static boolean isFirst(CToken tk) {
		return tk.getType() == CToken.TK_OUTPUT;
	}

	public void parse(CParseContext pcx) throws FatalErrorException {
		// ここにやってくるときは、必ずisFirst()が満たされている
		CTokenizer ct = pcx.getTokenizer();
		CToken tk = ct.getCurrentToken(pcx);

		// output の次の字句を読む
		tk = ct.getNextToken(pcx);
		if(Expression.isFirst(tk)){
			expression = new Expression(pcx);
			expression.parse(pcx);
			// expression の解析後,現在の字句を読む
			tk = ct.getCurrentToken(pcx);
			if(tk.getType() != CToken.TK_SEMI){
				pcx.fatalError(tk + "StatementOutput: ;がありません");
			}
			tk = ct.getNextToken(pcx);
		}else{
			pcx.fatalError(tk + "StatementOutput: outputの後ろはexpressionです");
		}
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
		// if (expression != null) {
		// 	this.setCType(CType.getCType(expression.getCType().getType()));
		// 	this.setConstant(expression.isConstant());
		// }
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
		CodeGenCommon cgc = pcx.getCodeGenCommon();
		cgc.printStartComment(getBNF(getId()));
		if(expression != null){
			expression.codeGen(pcx);

			cgc.printPopCodeGen("", "R1", "expressionの結果をR0に取り出す");
			cgc.printInstCodeGen("", "MOV #0xFFE0, R0", "MappedIOをR0に");
			cgc.printInstCodeGen("", "MOV R1, (R0)", "LEDに出力");
		}
		cgc.printCompleteComment(getBNF(getId()));
	}
}
