package lang.c.parse;

import lang.FatalErrorException;
import lang.c.CParseContext;
import lang.c.CParseRule;
import lang.c.CSymbolTableEntry;
import lang.c.CToken;
import lang.c.CTokenizer;
import lang.c.CType;
import lang.c.CodeGenCommon;

public class Variable extends CParseRule{
    CParseRule ident, array, call;
	CToken sem; //意味解析でエラー場所を表示する用
	String identName;

	public Variable(CParseContext pcx) {
		super("Variable");
		//変数名 配列要素 の格納番地を積めば良い
		//setBNF("variable ::= ident [ array ]"); //CV04~ []は0か1回
		setBNF("variable ::= ident [ array | call ]"); //CV12~
	}

    public static boolean isFirst(CToken tk) {
		return Ident.isFirst(tk);
	}

	public void parse(CParseContext pcx) throws FatalErrorException {
		// ここにやってくるときは、必ずisFirst()が満たされている
		CTokenizer ct = pcx.getTokenizer();
		CToken tk = ct.getCurrentToken(pcx);
		sem = tk;
		
		identName = tk.getText();
		ident = new Ident(pcx);
		ident.parse(pcx);

		// ident の解析後、今の字句を読む
		tk = ct.getCurrentToken(pcx);
		if(Array.isFirst(tk)){
			array = new Array(pcx);
			array.parse(pcx);

		}else if(Call.isFirst(tk)){
			call = new Call(pcx);
			call.parse(pcx);
		}
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
		if(ident != null){
			ident.semanticCheck(pcx);
			int rt = ident.getCType().getType(); // identの型

			//エラーになる場合
			//identが関数→()がない
			//identが配列→[]がない
			//identが↑以外→(),[]がある
			
			CSymbolTableEntry functionEntry = pcx.getSymbolTable().searchGlobal(identName);
			boolean isFunction = false;
			if(functionEntry != null){
				isFunction = functionEntry.isFunction();
			}
			
			//identが↑以外→(),[]がある
			if(array != null){ // 後ろに[]があるのにidentがint,pintでないのはエラー
				if(rt != CType.T_int_array && rt != CType.T_pint_array){
					pcx.recoverableError(sem + " variable: 配列型でない識別子に[]はつけられません");
				}
				array.semanticCheck(pcx); //arrayの型によってvariableの型が変わることはない
			}
			if(call != null){ // 後ろに()があるのにidentが関数でないのはおかしい
				if(!isFunction){
					pcx.recoverableError(sem + " variable: 関数でない識別子に()はつけられません");
				}
			}

			//identが関数→()がない
			if(isFunction && call == null){
				pcx.recoverableError(sem + " variable: 関数識別子の後ろは()です");
			}

			//CV13: 「call funcA(&b);(bは配列型)」の記述を許容するため変更。
			//[]がついていて中にNUMが無い場合はエラーとすればいいので、配列型の後ろの[]の有無は判定しない

			// &付きの配列型は配列のポインタ型にするため、FactorAmpで処理する
			// primaryからの呼び出しでの型変換はprimaryで行う

			this.setCType(CType.getCType(rt));
			this.setConstant(ident.isConstant());
		}
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
		CodeGenCommon cgc = pcx.getCodeGenCommon();
		cgc.printStartComment(getBNF(getId()));
		if(ident != null){
			ident.codeGen(pcx);
		}
		if(array != null){
			array.codeGen(pcx);
		}
		cgc.printCompleteComment(getBNF(getId()));
	}
}
