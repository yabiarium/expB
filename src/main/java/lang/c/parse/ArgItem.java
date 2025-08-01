package lang.c.parse;

import lang.*;
import lang.c.*;

public class ArgItem extends CParseRule {

    boolean isExistMult = false;
    boolean isArray = false;
    String identName;
    int size = 0;

    public ArgItem(CParseContext pcx) {
        super("ArgItem");
        setBNF("ArgItem ::= INT [ MULT ] IDENT [ LBRA RBRA ]"); //CV13~
    }

    public static boolean isFirst(CToken tk) {
        return tk.getType() == CToken.TK_INT;
	}

    public void parse(CParseContext pcx) throws FatalErrorException {
        CTokenizer ct = pcx.getTokenizer();
        CToken tk = ct.getNextToken(pcx); // int を読み飛ばす

        if(tk.getType() == CToken.TK_MULT){
            isExistMult = true;
            tk = ct.getNextToken(pcx); // *を読み飛ばす
        }

        try {
            if(tk.getType() == CToken.TK_IDENT){
                identName = tk.getText();
                tk = ct.getNextToken(pcx); //IDENTを読み飛ばす, 正常終了
            }else{
                pcx.recoverableError(tk + " argItem: IDENTがありません");
            }
        } catch (RecoverableErrorException e) {
            //処理はfunctionに託す
        }

        //要素数なしの配列
        if(tk.getType() == CToken.TK_LBRA){
            isArray = true;
            tk = ct.getNextToken(pcx); // [を読み飛ばす
            if(tk.getType() == CToken.TK_RBRA){
                tk = ct.getNextToken(pcx); // ]を読み飛ばす, 配列だった場合はここが正常終了
            }else{
                pcx.warning(tk + " argItem: ] を補いました");
            }
        }

        registerName(pcx, tk); //実引数をローカル変数として登録する
    }

    private void registerName(CParseContext pcx, CToken tk) throws FatalErrorException {
		// 変数登録
		CSymbolTableEntry entry;
		int argItemType;
		if (isArray) {
            //配列型の実引数=配列の先頭アドレス
            size = 1; //あとで変えるかも これを割り当てた所に、配列の先頭アドレスを入れる
			if (isExistMult) {
				argItemType = CType.T_pint_array;
			} else {
				argItemType = CType.T_int_array;
			}
		} else {
			size = 1;
			if (isExistMult) {
				argItemType = CType.T_pint;
			} else {
				argItemType = CType.T_int;
			}
		}
		entry = new CSymbolTableEntry(CType.getCType(argItemType), size, false, false);
        pcx.getSymbolTable().registerLocal_arg(identName, entry);
	}

    public void semanticCheck(CParseContext pcx) throws FatalErrorException {
        if(isExistMult && isArray){
            this.setCType(CType.getCType(CType.T_pint_array));
        }else if(isExistMult){
            this.setCType(CType.getCType(CType.T_pint));
        }else if(isArray){
            this.setCType(CType.getCType(CType.T_int_array));
        }else{
            this.setCType(CType.getCType(CType.T_int));
        }
    }

    public void codeGen(CParseContext pcx) throws FatalErrorException {
        CodeGenCommon cgc = pcx.getCodeGenCommon();
		cgc.printStartComment(getBNF(getId()));

        //call類で引数が積まれてからJSRで関数に飛ぶ
        
		cgc.printCompleteComment(getBNF(getId()));
    }
}
