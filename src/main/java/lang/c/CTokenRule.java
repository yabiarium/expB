package lang.c;

import java.util.HashMap;

/*切り出すべき文字列が，
どの字句タイプになるのかを決めるルールを記述する */
public class CTokenRule extends HashMap<String, Object> {
	//private static final long serialVersionUID = 1139476411716798082L;

	public CTokenRule() {
		put("true",	Integer.valueOf(CToken.TK_TRUE));
		put("false",	Integer.valueOf(CToken.TK_FALSE));	
		put("input", Integer.valueOf(CToken.TK_INPUT));
		put("output", Integer.valueOf(CToken.TK_OUTPUT));
		put("if", Integer.valueOf(CToken.TK_IF));
		put("else", Integer.valueOf(CToken.TK_ELSE));
		put("while", Integer.valueOf(CToken.TK_WHILE));
		put("int", Integer.valueOf(CToken.TK_INT));
		put("const", Integer.valueOf(CToken.TK_CONST));
		put("void", Integer.valueOf(CToken.TK_VOID));
		put("return", Integer.valueOf(CToken.TK_RETURN));
		put("func", Integer.valueOf(CToken.TK_FUNC));
		put("call", Integer.valueOf(CToken.TK_CALL));
	}

}
