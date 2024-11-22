package lang.c.parse;

import lang.FatalErrorException;
import lang.c.CParseContext;
import lang.c.CParseRule;
import lang.c.CToken;
import lang.c.CTokenizer;
import lang.c.CType;
import lang.c.CodeGenCommon;

public class Variable extends CParseRule{
    CParseRule ident, array;

	public Variable(CParseContext pcx) {
		super("Variable");
		//変数名 配列要素 の格納番地を積めば良い
		setBNF("variable ::= ident [ array ]"); //CV04~ []は0か1回
	}

    public static boolean isFirst(CToken tk) {
		return Ident.isFirst(tk);
	}

	public void parse(CParseContext pcx) throws FatalErrorException {
		// ここにやってくるときは、必ずisFirst()が満たされている
		CTokenizer ct = pcx.getTokenizer();
		CToken tk = ct.getCurrentToken(pcx);
		
		ident = new Ident(pcx);
		ident.parse(pcx);

		// ident の解析後、今の字句を読む
		tk = ct.getCurrentToken(pcx);
		if(Array.isFirst(tk)){
			array = new Array(pcx);
			array.parse(pcx);
		}
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
		if(ident != null){
			ident.semanticCheck(pcx);
			int rt = ident.getCType().getType(); // identの型
			
			// 後ろに[]が存在しているのにidentがint,pintなのはおかしい
			if(array != null){
				if(rt == CType.T_int || rt == CType.T_pint){
					pcx.fatalError("配列変数は T_int_array か T_pint_array です");
				}
				array.semanticCheck(pcx); //arrayの型によってvariableの型が変わることはない
			}

			// 配列型なのに後ろに[]が無いのはおかしい
			if(rt == CType.T_int_array || rt == CType.T_pint_array){
				if(array == null){
					pcx.fatalError("配列型の後ろに[]がありません");
				}
				// variableより上の階層では配列型は存在しない
				if (rt == CType.T_int_array) {
					rt = CType.T_int;
				}else if(rt == CType.T_pint_array){
					rt = CType.T_pint;
				}
			}
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
