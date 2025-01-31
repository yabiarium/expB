package lang.c.parse;

import lang.*;
import lang.c.*;

public class StatementReturn extends CParseRule {

    CParseRule expression;

    public StatementReturn(CParseContext pcx) {
        super("StatementReturn");
		setBNF("statementReturn ::= RETURN [ expression ] SEMI"); //CV12~
    }

    public static boolean isFirst(CToken tk) {
        return tk.getType() == CToken.TK_RETURN;
	}

    public void parse(CParseContext pcx) throws FatalErrorException {
        CTokenizer ct = pcx.getTokenizer();
        CToken tk = ct.getNextToken(pcx); // RETURNを読み飛ばす

        if(Expression.isFirst(tk)) {
            expression = new Expression(pcx);
            expression.parse(pcx);
            tk = ct.getCurrentToken(pcx); // expressionの後の;を読む
        }

        if(tk.getType() == CToken.TK_SEMI) {
            tk = ct.getNextToken(pcx); // ;を読み飛ばす, 正常終了
        }else {
            pcx.warning(tk + " statementReturn: ; を補いました");
        }

    }

    public void semanticCheck(CParseContext pcx) throws FatalErrorException {
        if(expression != null) {
            expression.semanticCheck(pcx);
            this.setCType(expression.getCType()); // expression の型をそのままコピー
			this.setConstant(expression.isConstant());
        }else{ //expression == null
            this.setCType(CType.getCType(CType.T_void));
			this.setConstant(false);
        }
    }

    public void codeGen(CParseContext pcx) throws FatalErrorException {
    }
}
