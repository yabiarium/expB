package lang.c.parse;

import lang.*;
import lang.c.*;

public class Declaration extends CParseRule {

	CParseRule intDecl, constDecl;

	public Declaration(CParseContext pcx) {
		super("Declaration");
		//setBNF("declaration ::= intDecl | constDecl"); //CV10~
		setBNF("declaration ::= intDecl | constDecl | voidDecl"); //CV12~
	}

	public static boolean isFirst(CToken tk) {
		return IntDecl.isFirst(tk) || ConstDecl.isFirst(tk) || VoidDecl.isFirst(tk);
	}

	public void parse(CParseContext pcx) throws FatalErrorException {
		CTokenizer ct = pcx.getTokenizer();
		CToken tk = ct.getCurrentToken(pcx);
		
		if (IntDecl.isFirst(tk)) {
			intDecl = new IntDecl(pcx);
			intDecl.parse(pcx);

		} else if(ConstDecl.isFirst(tk)){
			constDecl = new ConstDecl(pcx);
			constDecl.parse(pcx);
		}

		// 回復エラーはintDecl/constDecl内で処理されるので、この節点以上では考えなくてよい
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
		if (intDecl != null) {
			intDecl.semanticCheck(pcx);

		} else if (constDecl != null) {
			constDecl.semanticCheck(pcx);
		}
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
		CodeGenCommon cgc = pcx.getCodeGenCommon();
		
		cgc.printStartComment(getBNF(getId()));
		if (intDecl != null) {
			intDecl.codeGen(pcx);
		}
		if (constDecl != null) {
			constDecl.codeGen(pcx);
		}
		cgc.printCompleteComment(getBNF(getId()));
    }
}
