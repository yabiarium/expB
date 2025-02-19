package lang.c.parse;

import lang.*;
import lang.c.*;

public class Declaration extends CParseRule {

	CParseRule XXDecl;

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
			XXDecl = new IntDecl(pcx);
		} else if(ConstDecl.isFirst(tk)){
			XXDecl = new ConstDecl(pcx);
		} else if(VoidDecl.isFirst(tk)){
			XXDecl = new VoidDecl(pcx);
		}

		XXDecl.parse(pcx);
		// 回復エラーはintDecl/constDecl/voidDecl内で処理されるので、この節点以上では考えなくてよい
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
		if (XXDecl != null) {
			XXDecl.semanticCheck(pcx);
		}
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
		CodeGenCommon cgc = pcx.getCodeGenCommon();
		
		cgc.printStartComment(getBNF(getId()));
		if (XXDecl != null) {
			XXDecl.codeGen(pcx);
		}
		cgc.printCompleteComment(getBNF(getId()));
    }
}
