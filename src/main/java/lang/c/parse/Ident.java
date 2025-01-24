package lang.c.parse;

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
	private String identName, declBlockLabel;
	CSymbolTableEntry entry;
	boolean isDeclBlock = false;
	private int seqId;

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

		if (entry != null) {
			isDeclBlock = entry.isDeclBlock();
		}
		if (isDeclBlock) {
			seqId = pcx.getSeqId(identName);
			declBlockLabel = identName + seqId;
			pcx.getSymbolTable().registerLocal(declBlockLabel, entry); // 関数を局所変数として登録
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

		if (ident != null && entry != null) {
			if (entry.isGlobal()) {
				cgc.printPushCodeGen("","#"+identName,"Ident: 変数の格納番地を積む " + ident.toExplainString());
			} else {
				cgc.printInstCodeGen("", "MOV #" + entry.getAddress() + ", R0", "Ident: 局所変数のフレームポインタからの相対位置を取得 " + ident.toExplainString());
				cgc.printInstCodeGen("", "ADD R4, R0", "Ident: 局所変数の格納番地を計算する " + ident.toExplainString());
				cgc.printPushCodeGen("", "R0", "Ident: 格納番地を積む " + ident.toExplainString());
			}
		}

		cgc.printCompleteComment(getBNF(getId()));
	}
}
