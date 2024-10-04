package lang.c;

import lang.*;

/*コンパイラの（いわゆる）メインメソッドと，
字句切り出しがうまくできるかどうかのテスト用プログラム */
public class MiniCompiler {
	public static void main(String[] args) {
		String inFile = args[0]; // 適切なファイルを絶対パスで与えること
		IOContext ioCtx = new IOContext(inFile, System.out, System.err);
		MiniCompilerImpl compiler = new MiniCompilerImpl();
		compiler.compile(ioCtx);
	}
}
