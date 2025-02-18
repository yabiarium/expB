package lang.c.parse;

import lang.FatalErrorException;
import lang.RecoverableErrorException;
import lang.c.CParseContext;
import lang.c.CParseRule;
import lang.c.CToken;
import lang.c.CTokenizer;
import lang.c.CType;
import lang.c.CodeGenCommon;

public class FactorAmp extends CParseRule {
	// 新しく非終端記号に対応するクラスを作成する際は，必ず拡張BNF をコメントでつけること
	// また，更新する際は，拡張BNFの「履歴」を残すこと（例えば，実験３まで：．．．． と 実験４から：．．． のように）
	CParseRule number, primary;
	CToken sem; //意味解析でエラー場所を表示する用

	public FactorAmp(CParseContext pcx) {
		super("FactorAmp");
		//setBNF("factorAmp ::= AMP number"); //AMP=& CV02~03
		setBNF("factorAmp ::= AMP ( number | primary )"); //AMP=& CV04~
	}

	public static boolean isFirst(CToken tk) {
		return tk.getType() == CToken.TK_AMP;
	}

	public void parse(CParseContext pcx) throws FatalErrorException {
		// ここにやってくるときは、必ずisFirst()が満たされている
		CTokenizer ct = pcx.getTokenizer();
		CToken tk = ct.getCurrentToken(pcx);
		sem = tk;

		// &の次の字句を読む
		try {
			tk = ct.getNextToken(pcx);
			if (Number.isFirst(tk)) {
				number = new Number(pcx);
				number.parse(pcx);
			} else if(Primary.isFirst(tk)) {
				if(PrimaryMult.isFirst(tk)){
					pcx.recoverableError(tk + " factorAmp: &の後ろに*は置けません");
				}
				primary = new Primary(pcx, true);
				primary.parse(pcx);
			} else {
				pcx.recoverableError(tk + " factorAmp: &の後ろはnumberまたはprimary(variableのみ)です");
			}

		} catch (RecoverableErrorException e) {
			// 回復エラーだけ出して処理はstatementXXに任せる
		}
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
		if (number != null) {
			number.semanticCheck(pcx);
			this.setCType(CType.getCType(CType.T_pint));
			this.setConstant(isConstant());

		} else if (primary != null){
			primary.semanticCheck(pcx);
			int t = primary.getCType().getType();
			String ts = primary.getCType().toString();

			try {
				if(primary.getCType().getType() != CType.T_int && primary.getCType().getType() != CType.T_int_array){
					pcx.recoverableError(sem + " factorAmp: &の後ろはT_intです["+ts+"]");
				}
				if(primary.getCType().getType() == CType.T_int_array){
					t = CType.T_pint_array;
				}
			} catch (RecoverableErrorException e) {
			}
			this.setCType(CType.getCType(t));
			this.setConstant(primary.isConstant());
		}
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
		CodeGenCommon cgc = pcx.getCodeGenCommon();
		if (number != null) {
			cgc.printStartComment(getBNF(getId()));
			number.codeGen(pcx); // &以降(右部分木?)のコード生成を頼む
			cgc.printCompleteComment(getBNF(getId()));
		} else if (primary != null) {
			cgc.printStartComment(getBNF(getId()));
			primary.codeGen(pcx); // &以降(右部分木?)のコード生成を頼む
			cgc.printCompleteComment(getBNF(getId()));
		}
	}
}
