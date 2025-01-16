package lang.c.parse;

//import javax.lang.model.type.NullType;

import lang.FatalErrorException;
import lang.RecoverableErrorException;
import lang.c.CParseContext;
import lang.c.CParseRule;
import lang.c.CToken;
import lang.c.CTokenizer;
import lang.c.CType;
import lang.c.CodeGenCommon;

public class Ident extends CParseRule{

    CToken ident;

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
		ident = tk;
		tk = ct.getNextToken(pcx);
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
		String var = ident.getText();
		try {
			if(var.contains("_")){
				var = var.substring(0, var.indexOf('_'));
			}else{
				this.setCType(CType.getCType(CType.T_err));
				this.setConstant(true);
				//pcx.fatalError("ident: semanticCheck(): 変数名規則に合っていません");
				pcx.recoverableError("ident: 変数名規則に合っていません。変数名は「型_x」としてください");
			}


			if(var.equals("i")){
				this.setCType(CType.getCType(CType.T_int));
				this.setConstant(false);
	
			}else if(var.equals("ip")){
				this.setCType(CType.getCType(CType.T_pint));
				this.setConstant(false);
	
			}else if(var.equals("ia")){
				this.setCType(CType.getCType(CType.T_int_array));
				this.setConstant(false);
	
			}else if(var.equals("ipa")){
				this.setCType(CType.getCType(CType.T_pint_array));
				this.setConstant(false);
	
			}else if(var.equals("c")){
				this.setCType(CType.getCType(CType.T_int));
				this.setConstant(true);
			}else{
				this.setCType(CType.getCType(CType.T_err));
				this.setConstant(true);
				//pcx.fatalError("ident: semanticCheck(): 変数名規則に合っていません");
				pcx.recoverableError("ident: 変数名規則に合っていません。使用できる型はi,ip,ia,ipa,cのどれかです");
			}

		} catch (RecoverableErrorException e) {
		}
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
		CodeGenCommon cgc =pcx.getCodeGenCommon();
		cgc.printStartComment(getBNF(getId()));
		if(ident != null){
			cgc.printPushCodeGen("","#"+ident.getText(),"Ident:変数の格納番地を積む");
		}
		cgc.printCompleteComment(getBNF(getId()));
	}
}
