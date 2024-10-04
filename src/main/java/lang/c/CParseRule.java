package lang.c;

import java.util.HashMap;

import lang.*;

/*ParseRule を継承 (extends) して作り，Compiler インタフェースと 
LL1 インタフェースを実装（implements）する．このクラスでは，
文法規則にしたがって構文を解析するためのプログラムを parse() に
記述する．そしてこれを継承する各クラスは，構文解析の結果として
作られる「構文木」の各節点を表すことになる．さらに，その節点における
意味解析の作業を semanticCheck() に，コード生成の作業を codeGen() に
記述する．意味解析の際には，この節点が表しているのが，C のどの型の
情報であるのかを推測し管理する必要がある．そのためのメソッド群が
ここに定義してある（後述）．
 */
public abstract class CParseRule extends ParseRule<CParseContext> implements lang.Compiler<CParseContext>, LL1<CToken> {
	static private int ID = 0;  // 全 CParseRule の連番ID管理
	// クラスごとの連番ID管理
	static private HashMap<String,Integer> IDMAP = new HashMap<String,Integer>();    
	
	private int id;             // 新規作成の CParseRule インスタンスの番号
	private String BNF_LEFT;    // この接点の非終端記号名(BFN左)
	private String BNF_RIGHT;   // この接点の変換規則(BFN右)
	private CType ctype;        // この節点の（推測される）型
	private boolean isConstant; // この節点は定数を表しているか？

	public CParseRule() {
		ID++;
		id = ID;
	}

	public CParseRule(String name) {
		int r=1;
		if (IDMAP.containsKey(name)) {
			r=IDMAP.get(name)+1;
		}
		IDMAP.put(name,r);
		id = r;
	}

	public String getBNF() {
		if (BNF_LEFT != null && BNF_RIGHT != null)
			return BNF_LEFT + " ::= " + BNF_RIGHT;
		if (BNF_LEFT != null)
			return BNF_LEFT;
		if (BNF_RIGHT != null)
			return BNF_RIGHT;
		return "";
	}

	public String getBNF(int id) {
		return BNF_LEFT + id + " ::= " + BNF_RIGHT;
	}

	public void setBNF(String left, String right) {
		this.BNF_LEFT  = left;
		this.BNF_RIGHT = right;
	}

	public void setBNF(String bnf) {
		String[] b = bnf.split("\s*::=\s*");
		if (b.length > 1) {
			this.BNF_LEFT  = b[0];
			this.BNF_RIGHT = b[1];
		} else {
			this.BNF_LEFT = bnf;
		}
	}

	public int getId() {
		return id;
	}

	public CType getCType() {
		return ctype;
	}

	public void setCType(CType ctype) {
		this.ctype = ctype;
	}

	public void setConstant(boolean isConstant) {
		this.isConstant = isConstant;
	}

	public boolean isConstant() {
		return isConstant;
	}
}
