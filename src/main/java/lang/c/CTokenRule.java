package lang.c;

import java.util.HashMap;

/*切り出すべき文字列が，
どの字句タイプになるのかを決めるルールを記述する */
public class CTokenRule extends HashMap<String, Object> {
	//private static final long serialVersionUID = 1139476411716798082L;

	public CTokenRule() {
		// put("true",	Integer.valueOf(CToken.TK_TRUE));
		// put("false",	Integer.valueOf(CToken.TK_FALSE));		
	}
}
