package lang.c.parse;

//import javax.lang.model.type.NullType;

import lang.FatalErrorException;
import lang.c.CParseContext;
import lang.c.CParseRule;
import lang.c.CSymbolTableEntry;
import lang.c.CToken;
import lang.c.CTokenizer;
import lang.c.CType;
import lang.c.CodeGenCommon;

public class Ident extends CParseRule{

    CToken ident;
	private String identName;
	CSymbolTableEntry entry;
	boolean isFunction = false;

	public Ident(CParseContext pcx) {
		super("Ident");
		setBNF("ident ::= IDENT"); //CV04~
	}

    public static boolean isFirst(CToken tk) {
		return tk.getType() == CToken.TK_IDENT;
	}

	public void parse(CParseContext pcx) throws FatalErrorException {
		CTokenizer ct = pcx.getTokenizer();
		CToken tk = ct.getCurrentToken(pcx);
		identName = tk.getText();
		ident = tk;

		if (pcx.getSymbolTable().searchLocal(identName) != null) {
			entry = pcx.getSymbolTable().searchLocal(identName);

		} else if (pcx.getSymbolTable().searchGlobal(identName) != null) {
			entry = pcx.getSymbolTable().searchGlobal(identName);

		} else {
			pcx.warning(tk + " ident: 宣言されていません");
		}

		tk = ct.getNextToken(pcx);
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
		if (ident != null) {
			if (entry == null) {
				setCType(CType.getCType(CType.T_err));
				return;
			}

			int setType = entry.GetCType().getType();
			boolean isConstant = entry.isConstant();
			this.setCType(CType.getCType(setType));
			this.setConstant(isConstant);
		}
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
		CodeGenCommon cgc =pcx.getCodeGenCommon();
		cgc.printStartComment(getBNF(getId()));
		if(ident != null){
			if(entry != null){
				cgc.printPushCodeGen("","#"+ident.getText(),"Ident: 変数の格納番地を積む");
			}
		}
		cgc.printCompleteComment(getBNF(getId()));
	}
}
