package lang.c.parse;

import lang.*;
import lang.c.*;
import java.util.ArrayList;
import java.util.List;

public class ConstDecl extends CParseRule {

	CParseRule constItem;
	List<CParseRule> constItemList = new ArrayList<>();

	public ConstDecl(CParseContext pcx) {
		super("ConstDecl");
		setBNF("constDecl ::= CONST INT constItem { COMMA constItem } SEMI"); //CV10~
	}

	public static boolean isFirst(CToken tk) {
		return tk.getType() == CToken.TK_CONST;
	}

	public void parse(CParseContext pcx) throws FatalErrorException {
		CTokenizer ct = pcx.getTokenizer();
		CToken tk = ct.getCurrentToken(pcx);

		try {
			tk = ct.getNextToken(pcx); // CONSTを読む
			if(tk.getType() == CToken.TK_INT){
				tk = ct.getNextToken(pcx); // INTを読む
			}else{
				if(ConstItem.isFirst(tk)){
					pcx.warning(tk + "constDecl: INT を補いました"); //他の型を作るなら、型不明で回復エラーにする
				}else{
					pcx.recoverableError(tk + "constDecl: INTがありません");
				}
			}
			
			if(ConstItem.isFirst(tk)){
				constItem = new ConstItem(pcx);
				constItem.parse(pcx);
				constItemList.add(constItem);

				tk = ct.getCurrentToken(pcx);
				while(tk.getType() == CToken.TK_COMMA){
					tk = ct.getNextToken(pcx); // ,を読む
					if(ConstItem.isFirst(tk)){
						constItem = new ConstItem(pcx);
						constItem.parse(pcx);
						constItemList.add(constItem);
						tk = ct.getCurrentToken(pcx);
					}else{
						pcx.recoverableError(tk + "constDecl: IDENTがありません");
					}
				}
			}else{
				pcx.recoverableError(tk + "constDecl: IDENTがありません");
			}

			if(tk.getType() == CToken.TK_SEMI){
				tk = ct.getNextToken(pcx); // ;を読む, 正常終了
			}else{
				pcx.warning(tk + "constDecl: ; を補いました");
			}
			
		} catch (RecoverableErrorException e) {
			// ; まで読み飛ばす
			ct.skipTo(pcx, CToken.TK_SEMI);
			tk = ct.getNextToken(pcx);
		}
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
		//CodeGenCommon cgc = pcx.getCodeGenCommon();
    }
}
