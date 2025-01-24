package lang.c.parse;

import lang.*;
import lang.c.*;
import java.util.ArrayList;
import java.util.List;

public class IntDecl extends CParseRule {

	CParseRule declItem; 
	List<CParseRule> declItemList = new ArrayList<>();

	public IntDecl(CParseContext pcx) {
		super("IntDecl");
		setBNF("intDecl ::= INT declItem { COMMA declItem } SEMI"); //CV10~
	}

	public static boolean isFirst(CToken tk) {
		return tk.getType() == CToken.TK_INT;
	}

	public void parse(CParseContext pcx) throws FatalErrorException {
		CTokenizer ct = pcx.getTokenizer();
		CToken tk = ct.getCurrentToken(pcx);

		try {
			tk = ct.getNextToken(pcx); // INTを読み飛ばす
			if(DeclItem.isFirst(tk)){
				declItem = new DeclItem(pcx);
				declItem.parse(pcx);
				declItemList.add(declItem);

				tk = ct.getCurrentToken(pcx); // ,か;を読む
				while(tk.getType() == CToken.TK_COMMA){
					tk = ct.getNextToken(pcx); // ,を読み飛ばす
					if(DeclItem.isFirst(tk)){
						declItem = new DeclItem(pcx);
						declItem.parse(pcx);
						declItemList.add(declItem);
						tk = ct.getCurrentToken(pcx); // ,か;を読む
					}else{
						pcx.recoverableError(tk + " intDecl: IDENTがありません");
					}
				}
			}else{
				pcx.recoverableError(tk + " intDecl: IDENTがありません");
			}

			if(tk.getType() == CToken.TK_SEMI){
				tk = ct.getNextToken(pcx); // ;を読む, 正常終了
			}else{
				pcx.warning(tk + " intDecl: ; を補いました");
			}
			
		} catch (RecoverableErrorException e) {
			// ; まで読み飛ばす
			ct.skipTo(pcx, CToken.TK_SEMI);
			tk = ct.getNextToken(pcx);
		}
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
		if(declItemList != null){
			for(CParseRule item : declItemList){
				item.semanticCheck(pcx);
			}
		}
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
		CodeGenCommon cgc = pcx.getCodeGenCommon();

		cgc.printStartComment(getBNF());
		if(declItemList != null){
			for(CParseRule item : declItemList){
				item.codeGen(pcx);
			}
		}
		cgc.printCompleteComment(getBNF());

    }
}
