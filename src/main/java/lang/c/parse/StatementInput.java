package lang.c.parse;

import lang.FatalErrorException;
import lang.RecoverableErrorException;
import lang.c.CParseContext;
import lang.c.CParseRule;
import lang.c.CToken;
import lang.c.CTokenizer;
import lang.c.CType;
import lang.c.CodeGenCommon;

public class StatementInput extends CParseRule{

    CParseRule primary;
	CToken sem; //意味解析でエラー場所を表示する用

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
		sem = tk;

		// input の次の字句を読む
		try {
			tk = ct.getNextToken(pcx);
			if(Primary.isFirst(tk)){
				primary = new Primary(pcx);
				primary.parse(pcx);
				// primary の解析後,現在の字句を読む
				tk = ct.getCurrentToken(pcx);
				if(tk.getType() == CToken.TK_SEMI){
					tk = ct.getNextToken(pcx); //正常終了
				}else{
					//pcx.fatalError(tk + "statementInput: parse(): ;がありません");
					pcx.warning(tk + " statementInput: ; を補いました");
				}
			}else{
				//pcx.fatalError(tk + "statementInput: parse(): inputの後ろはprimaryです");
				pcx.recoverableError(tk + " statementInput: inputの後ろはprimaryです");
			}

		} catch (RecoverableErrorException e) {
			// ; まで飛ばす(primary内部/isFirstのどちらで回復エラーが出た場合もここに来る)
			ct.skipTo(pcx, CToken.TK_SEMI);
			tk = ct.getNextToken(pcx);
		}
		
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
		if (primary != null) {
			primary.semanticCheck(pcx);
			
			try {
				if(primary.isConstant()){
					//pcx.fatalError("statementInput: semanticCheck(): 定数には代入できません");
					pcx.recoverableError(sem + " statementInput: 定数には代入できません");
				}
			} catch (RecoverableErrorException e) {
			}
			this.setCType(CType.getCType(primary.getCType().getType()));
			this.setConstant(primary.isConstant());
		}
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
		CodeGenCommon cgc = pcx.getCodeGenCommon();
		cgc.printStartComment(getBNF(getId()));
		if(primary != null){
			primary.codeGen(pcx);

			cgc.printInstCodeGen("", "MOV #0xFFE0, R1", "statementInput: MappedIOをR1に");
			cgc.printPopCodeGen("", "R0", "statementInput: primaryの結果をR0に取り出す");
			cgc.printInstCodeGen("", "MOV (R1), (R0)", "statementInput: LEDから入力");
		}
		cgc.printCompleteComment(getBNF(getId()));
	}
}
