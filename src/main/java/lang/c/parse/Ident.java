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
	boolean isFunction = false;
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

		//declaration側では終端記号のIDENTしか扱わないので、この節点IdentにはStatementからしか到達しない。
		//つまり、ここの解析をするときには変数が全て登録された状態となっている
		if (pcx.getSymbolTable().searchLocal(identName) != null) {
			entry = pcx.getSymbolTable().searchLocal(identName);

		} else if (pcx.getSymbolTable().searchGlobal(identName) != null) {
			entry = pcx.getSymbolTable().searchGlobal(identName);

		} else {
			pcx.warning(tk + " ident: 宣言されていません");
		}

		if (entry != null) {
			isFunction = entry.isFunction();
		}
		if (isFunction) {
			seqId = pcx.getSeqId(identName);
			declBlockLabel = identName + seqId;
			pcx.getSymbolTable().registerLocal(declBlockLabel, entry); // 関数を局所変数として登録
		}

		tk = ct.getNextToken(pcx);
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
		if (ident != null) {
			if (entry == null) {
				this.setCType(CType.getCType(CType.T_err));
				return;
			}

			if(entry.getCType().getType() == CType.T_void){
				this.setCType(CType.getCType(CType.T_err));
				this.setConstant(true);
			}else if(entry.isFunction()){
				this.setCType(entry.getCType());
				this.setConstant(true); //関数の返り値を入れたアドレスは定数とし、代入できないようにする
			}else{
				this.setCType(entry.getCType());
				this.setConstant(entry.isConstant());
			}
		}
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
		CodeGenCommon cgc =pcx.getCodeGenCommon();
		cgc.printStartComment(getBNF(getId()));

		if (ident != null && entry != null) {
			if(isFunction){ //関数はプログラマには見えない局所変数として扱う
				cgc.printInstCodeGen("", "JSR " + identName, "Ident: 関数へジャンプする");
				cgc.printInstCodeGen("", "MOV #" + entry.getAddress() + ", R1", "Ident: 局所変数のフレームポインタからの相対位置を取得 " + ident); //R0には返り値が入っているのでR1を使用する
				cgc.printInstCodeGen("", "ADD R4, R1", "Ident: 局所変数の格納番地を計算する " + ident);
				cgc.printInstCodeGen("", "MOV R0, (R1)", "Ident: 変数アドレスに返り値を代入する"); //else{}の局所変数の宣言だけの場合は、代入式が出現したときにアドレスを取り出して代入実行するが、関数呼び出しの場合は格納場所(アドレス)の用意と代入を同時に行う
				cgc.printPushCodeGen("", "R1", "Ident: 格納番地を積む " + ident); //これで返り"値"が番地の中に収まり、番地として扱えるようになった

			}else if (entry.isGlobal()) {
				cgc.printPushCodeGen("","#"+identName, "Ident: 変数の格納番地を積む " + ident);

			} else {
				cgc.printInstCodeGen("", "MOV #" + entry.getAddress() + ", R0", "Ident: 局所変数のフレームポインタからの相対位置を取得 " + ident);
				cgc.printInstCodeGen("", "ADD R4, R0", "Ident: 局所変数の格納番地を計算する " + ident);
				cgc.printPushCodeGen("", "R0", "Ident: 格納番地を積む " + ident);
			}
		}

		cgc.printCompleteComment(getBNF(getId()));
	}
}
