package lang.c.parse;

import lang.FatalErrorException;
import lang.c.CParseContext;
import lang.c.CParseRule;
import lang.c.CToken;
// import lang.c.CTokenizer;
// import lang.c.CType;
import lang.c.CodeGenCommon;

public class AddressToValue extends CParseRule{

    CParseRule primary;

	public AddressToValue(CParseContext pcx) {
		super("AddressToValue");
		//primary番地情報を間接参照して値に変換
		setBNF("addressToValue ::= primary"); //CV04~
	}

    public static boolean isFirst(CToken tk) {
		return Primary.isFirst(tk);
	}

	public void parse(CParseContext pcx) throws FatalErrorException {
		// ここにやってくるときは、必ずisFirst()が満たされている
		primary = new Primary(pcx);
		primary.parse(pcx);
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
		if (primary != null) {
			primary.semanticCheck(pcx);
			this.setCType(primary.getCType());
			this.setConstant(primary.isConstant());
		}
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
		CodeGenCommon cgc = pcx.getCodeGenCommon();
		cgc.printStartComment(getBNF(getId()));
		if (primary != null) {
			primary.codeGen(pcx);
			cgc.printPopCodeGen("","R0","AddressToValue: アドレスを取り出す");
			cgc.printPushCodeGen("","(R0)","AddressToValue: 参照した値を積む");
		}
		cgc.printCompleteComment(getBNF(getId()));
	}
}
