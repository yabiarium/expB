package lang.c.parse;

import lang.FatalErrorException;
import lang.c.CParseContext;
import lang.c.CParseRule;
import lang.c.CToken;
import lang.c.CTokenizer;
import lang.c.CType;
//import lang.c.CType;
import lang.c.CodeGenCommon;

public class StatementAssign extends CParseRule{

    CParseRule expression, primary;

	public StatementAssign(CParseContext pcx) {
		super("StatementAssign");
		setBNF("statementAssign ::= primary ASSIGN expression SEMI"); //CV05~
	}

    public static boolean isFirst(CToken tk) {
		return Primary.isFirst(tk);
	}

	public void parse(CParseContext pcx) throws FatalErrorException {
		// ここにやってくるときは、必ずisFirst()が満たされている
		CTokenizer ct = pcx.getTokenizer();
		CToken tk = ct.getCurrentToken(pcx);

			primary = new Primary(pcx);
			primary.parse(pcx);
			// primaryの解析後,現在の字句を読む
			tk = ct.getCurrentToken(pcx);
			if(tk.getType() != CToken.TK_ASSIGN){
				pcx.fatalError(tk + "StatementAssign: =がありません");
			}

			tk = ct.getNextToken(pcx);
			if(Expression.isFirst(tk)){
				expression = new Expression(pcx);
				expression.parse(pcx);
				// expressionの解析後,現在の字句を読む
				tk = ct.getCurrentToken(pcx);
				if(tk.getType() != CToken.TK_SEMI){
					pcx.fatalError(tk + "StatementAssign: ;がありません");
				}
			}else{
				pcx.fatalError(tk + "StatementAssign: =の後ろはexpressionです");
			}
			
			tk = ct.getNextToken(pcx);
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
		if (primary != null) {
			primary.semanticCheck(pcx);
			expression.semanticCheck(pcx); //parse()が通ってるならprimaryとセットで存在するはず

			int lt = primary.getCType().getType(); //左辺の型
			int rt = expression.getCType().getType(); //右辺の型
			
			if(lt == CType.T_int && rt == CType.T_pint){
				pcx.fatalError("StatementAssign: [int]に[*int]は代入できません");
			}else if(lt == CType.T_pint && rt == CType.T_int){
				pcx.fatalError("StatementAssign: [*int]に[int]は代入できません");
			}else if(primary.isConstant()){
				pcx.fatalError("定数には代入できません");
			}
			this.setCType(CType.getCType(lt));
			this.setConstant(primary.isConstant());
		}
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
