package lang.c.parse;

import lang.FatalErrorException;
import lang.RecoverableErrorException;
import lang.c.CParseContext;
import lang.c.CParseRule;
import lang.c.CToken;
import lang.c.CTokenizer;
import lang.c.CType;
//import lang.c.CType;
import lang.c.CodeGenCommon;

public class StatementAssign extends CParseRule{

    CParseRule expression, primary;
	CToken sem; //意味解析でエラー場所を表示する用

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
		sem = tk;

		try {
			primary = new Primary(pcx);
			primary.parse(pcx);
			// primaryの解析後,現在の字句を読む
			tk = ct.getCurrentToken(pcx);
			if(tk.getType() == CToken.TK_ASSIGN){
				tk = ct.getNextToken(pcx); // =を読み飛ばす
			}else{
				if(Expression.isFirst(tk)){
					pcx.warning(tk + "statementAssign: = を補いました");
				}else{
					//pcx.fatalError(tk + "statementAssign: parse(): =がありません");
					pcx.recoverableError(tk + " statementAssign: =がありません");
				}
			}

			if(Expression.isFirst(tk)){
				expression = new Expression(pcx);
				expression.parse(pcx);
				// expressionの解析後,現在の字句を読む
				tk = ct.getCurrentToken(pcx);
				if(tk.getType() == CToken.TK_SEMI){
					tk = ct.getNextToken(pcx); //正常終了
				}else{
					//pcx.fatalError(tk + "statementAssign: parse(): ;がありません");
					pcx.warning(tk + "statementAssign: ; を補いました");
				}
			}else{
				//pcx.fatalError(tk + "statementAssign: parse(): =の後ろはexpressionです");
				pcx.recoverableError(tk + " statementAssign: =の後ろはexpressionです");
			}
		
		} catch (RecoverableErrorException e) {
			// ; まで飛ばす(primary内部/expression内部/isFirstのどこで回復エラーが出た場合もここに来る)
			ct.skipTo(pcx, CToken.TK_SEMI);
			tk = ct.getNextToken(pcx);
		}
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
		if (primary != null) {
			primary.semanticCheck(pcx);
			expression.semanticCheck(pcx); //parse()が通ってるならprimaryとセットで存在するはず

			int lt = primary.getCType().getType(); //左辺の型
			int rt = expression.getCType().getType(); //右辺の型
			String lts = primary.getCType().toString();
			String rts = expression.getCType().toString();
			
			try {
				if(lt != rt){
					//pcx.fatalError("statementAssign: semanticCheck(): 左辺の型["+lts+"]と右辺の型["+rts+"]が異なります");
					pcx.recoverableError(sem + "statementAssign: 左辺の型["+lts+"]と右辺の型["+rts+"]が異なります");
				}else if(primary.isConstant()){
					//pcx.fatalError("statementAssign: semanticCheck(): 定数には代入できません");
					pcx.recoverableError(sem + "statementAssign: 定数には代入できません");
				}
			} catch (RecoverableErrorException e) {
			}
			
			this.setCType(CType.getCType(lt));
			this.setConstant(primary.isConstant());
		}
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
		CodeGenCommon cgc = pcx.getCodeGenCommon();
		cgc.printStartComment(getBNF(getId()));
		if(primary != null){
			primary.codeGen(pcx); //左辺：番地をスタックに積む
			expression.codeGen(pcx); //右辺：代入するものをスタックに積む

			cgc.printPopCodeGen("", "R1", "StatementAssign: 右辺値取り出しす");
			cgc.printPopCodeGen("", "R0", "StatementAssign; 左辺番地取り出し");
			cgc.printInstCodeGen("", "MOV R1, (R0)", "StatementAssign; 代入実行");
		}
		cgc.printCompleteComment(getBNF(getId()));
	}
}
