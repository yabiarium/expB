package lang.c.parse;

import lang.FatalErrorException;
import lang.RecoverableErrorException;
import lang.c.CParseContext;
import lang.c.CParseRule;
import lang.c.CToken;
import lang.c.CTokenizer;
import lang.c.CType;
import lang.c.CodeGenCommon;

public class Array extends CParseRule{

    CParseRule expression;

	public Array(CParseContext pcx) {
		super("Array");
		//[ expression ] の式評価値が積まれれば良い
		setBNF("array ::= LBRA expression RBRA"); //CV04~
	}

    public static boolean isFirst(CToken tk) {
		return tk.getType() == CToken.TK_LBRA;
	}

	public void parse(CParseContext pcx) throws FatalErrorException {
		// ここにやってくるときは、必ずisFirst()が満たされている
		CTokenizer ct = pcx.getTokenizer();
		CToken tk = ct.getCurrentToken(pcx);

		// [ の次の字句を読む
		tk = ct.getNextToken(pcx);

		try {
			if (Expression.isFirst(tk)) {
				expression = new Expression(pcx);
				expression.parse(pcx);
			} else {
				pcx.recoverableError(tk + "Array: parse(): [の後ろはexpressionです"); //→エラーへ
			}
			
			// expressionの解析後,現在の字句を読む
			tk = ct.getCurrentToken(pcx);
			if(tk.getType() != CToken.TK_RBRA){
				//pcx.fatalError(tk + "Array: parse(): ]がありません");
				pcx.warning(tk+"] を補いました");
			}
			tk = ct.getNextToken(pcx); //正常終了

		} catch (RecoverableErrorException e) {
			// ;か]まで読み飛ばす
			ct.skipTo(pcx, CToken.TK_SEMI, CToken.TK_RBRA); //currentTokenが指定したトークンになっている
			tk = ct.getNextToken(pcx);
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
