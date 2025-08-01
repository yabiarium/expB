package lang.c;

import java.util.List;

import lang.SymbolTableEntry;

public class CSymbolTableEntry extends SymbolTableEntry {
    private CType type; // この識別子に対して宣言された型
    private int size; // メモリ上に確保すべきワード数
    private boolean constp; // 定数宣言か？

    //CV11~
    private boolean isGlobal; // 大域変数か？
    private int address; // 割り当て番地
    private boolean isFunction; // 関数か？

	//CV13~
	private List<CType> argTypeList; // 関数の引数の型のリスト
	private int address_arg;
	private boolean isArg = false;

	public CSymbolTableEntry(CType type, int size, boolean constp, boolean isFunction) {
		this.type = type;
		this.size = size;
		this.constp = constp;
		this.isFunction = isFunction;
	}

    public CSymbolTableEntry(CType type, int size, boolean constp) {
		this.type = type;
		this.size = size;
		this.constp = constp;
	}

    public String toExplainString() { // このエントリに関する情報を作り出す．記号表全体を出力するときに使う．
        return type.toString() + ", " + size + (constp ? "定数" : "変数");
    }

    public CType getCType() { return type; }
	public int getSize() { return size; }
	public boolean isConstant() { return constp; }
	public boolean isGlobal() { return isGlobal; }
	public void setAddress(int addr) { address = addr; }
	public void setIsGlobal(boolean isGlobal) { this.isGlobal = isGlobal; }
	public void setisFunction(boolean isFunction) { this.isFunction = isFunction; }
	public int getAddress() { return address; }
	public boolean isFunction() { return isFunction; }

	public boolean verificateFunction(CSymbolTableEntry e) {
		// すでに登録された関数と同じかどうかを確認する
		if (e.isFunction() && e.getCType().equals(e.getCType())) {
			return true;
		} else {
			return false;
		}
	}

	public List<CType> getArgTypeList() { return argTypeList; }
	public void setArgTypeList(List<CType> argTypeList) { this.argTypeList = argTypeList; }
	public void setAddress_arg(int address_arg) { this.address_arg = address_arg; }
	public int getAddress_arg() { return address_arg; }
	public void setIsArg(boolean isArg) { this.isArg = isArg; }
	public boolean isArg() { return isArg; }
}
