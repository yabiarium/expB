package lang.c;

import lang.FatalErrorException;
import lang.IOContext;
import lang.c.parse.Program2;

public class MiniCompilerImpl {
    void compile(IOContext ioContext) {
        CTokenizer tknz = new CTokenizer(new CTokenRule());
		CParseContext pcx = new CParseContext(ioContext, tknz);
		try {
			CTokenizer ct = pcx.getTokenizer();
			CToken tk = ct.getNextToken(pcx);
			if (Program2.isFirst(tk)) {
				CParseRule parseTree = new Program2(pcx);
				parseTree.parse(pcx);									// 構文解析
				if (pcx.hasNoError()) parseTree.semanticCheck(pcx);		// 意味解析
				if (pcx.hasNoError()) parseTree.codeGen(pcx);			// コード生成
				pcx.errorReport();
			} else {
				pcx.fatalError(tk.toExplainString() + "プログラムの先頭にゴミがあります");
			}
		} catch (FatalErrorException e) {
			e.printStackTrace();
		}
    }
}
