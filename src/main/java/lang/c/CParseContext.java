package lang.c;

import java.util.HashMap;

import lang.*;

/*ParseContext を継承して作る．ver.00 ではたいしたことは何も
記述していないが，将来ここにいろいろなことを記述する必要が出てくる */
public class CParseContext extends ParseContext {
	CodeGenCommon cgc;
	HashMap<String,Integer> seqHashMap = new HashMap<String,Integer>();
	private CSymbolTable symbolTable;

	public CParseContext(IOContext ioCtx,  CTokenizer tknz) {
		super(ioCtx, tknz);
		this.cgc = new CodeGenCommon(ioCtx.getOutStream());
		symbolTable = new CSymbolTable();
	}

	@Override
	public CTokenizer getTokenizer(){
		return (CTokenizer) super.getTokenizer();
	}

	public int getSeqId(String name) {
		int seq = 1;
		if (seqHashMap.containsKey(name)) {
			seq = seqHashMap.get(name) + 1;
		}
		seqHashMap.put(name, seq);
		return seq; 
	}
	
	public CodeGenCommon getCodeGenCommon() {
		return cgc;
	}
	
	public CSymbolTable getSymbolTable(){
		return symbolTable;
	}
}
