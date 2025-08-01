package lang.c.parse;

import lang.*;
import lang.c.*;

public class Term extends CParseRule {
	// 新しく非終端記号に対応するクラスを作成する際は，必ず拡張BNF をコメントでつけること
	// また，更新する際は，拡張BNFの「履歴」を残すこと（例えば，実験３まで：．．．． と 実験４から：．．． のように）
	CParseRule term;

	public Term(CParseContext pcx) {
		super("Term");
		//setBNF("Term ::= Factor"); //~CV02
		setBNF("term ::= factor { termMult | termDiv }"); //CV03~
	}

	public static boolean isFirst(CToken tk) {
		return Factor.isFirst(tk);
	}

	public void parse(CParseContext pcx) throws FatalErrorException {
		// ここにやってくるときは、必ずisFirst()が満たされている
		CParseRule factor = null, list = null;
		factor = new Factor(pcx);
		factor.parse(pcx);
		CTokenizer ct = pcx.getTokenizer();
		CToken tk = ct.getCurrentToken(pcx);
		while (TermMult.isFirst(tk) | TermDiv.isFirst(tk)) {
			if(TermMult.isFirst(tk)){
				list = new TermMult(pcx, factor);
			}else if(TermDiv.isFirst(tk)){
				list = new TermDiv(pcx, factor);
			}
			list.parse(pcx);
			factor = list;
			tk = ct.getCurrentToken(pcx);
		}
		term = factor;
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
		if (term != null) {
			term.semanticCheck(pcx);
			this.setCType(term.getCType()); // factor の型をそのままコピー
			this.setConstant(term.isConstant());
		}
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
		CodeGenCommon cgc = pcx.getCodeGenCommon();
		cgc.printStartComment(getBNF(getId()));
		if (term != null) {
			term.codeGen(pcx);
		}
		cgc.printCompleteComment(getBNF(getId()));
	}
}
