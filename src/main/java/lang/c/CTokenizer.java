package lang.c;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;

import lang.*;

public class CTokenizer extends Tokenizer<CToken, CParseContext> {
	@SuppressWarnings("unused")
	private CTokenRule rule;
	private int lineNo, colNo;
	private char backCh;
	private boolean backChExist = false;

	private final int ST_NORMAL = 0;
	private final int ST_EOF = 1;
	private final int ST_ILL = 2;
	private final int ST_NUM = 3;
	private final int ST_PLUS = 4;
	private final int ST_MINUS = 5;
	private final int ST_SLASH = 6;
	private final int ST_COM = 7; //COM=COMMENT
	private final int ST_BLOCKCOM = 8;
	private final int ST_BLOCKCOMASTA = 9; //ASTA=*

	private final char __EOF__ = (char)-1;

	public CTokenizer(CTokenRule rule) {
		this.rule = rule;
		lineNo = 1;
		colNo = 1;
	}

	private InputStream in;
	private PrintStream err;

	private char readChar() {
		char ch;
		if (backChExist) {
			ch = backCh;
			backChExist = false;
		} else {
			try {
				ch = (char) in.read();
			} catch (IOException e) {
				e.printStackTrace(err);
				ch = (char) -1;
			}
		}
		++colNo;
		if (ch == '\n') {
			colNo = 1;
			++lineNo;
		}
		// System.out.print("'"+ch+"'("+(int)ch+")");
		return ch;
	}

	private void backChar(char c) {
		backCh = c;
		backChExist = true;
		--colNo;
		if (c == '\n') {
			--lineNo;
		}
	}

	// 現在読み込まれているトークンを返す
	private CToken currentTk = null;

	public CToken getCurrentToken(CParseContext pctx) {
		return currentTk;
	}

	// 次のトークンを読んで返す
	public CToken getNextToken(CParseContext pctx) {
		in = pctx.getIOContext().getInStream();
		err = pctx.getIOContext().getErrStream();
		currentTk = readToken();
		return currentTk;
	}

	private CToken readToken() {
		CToken tk = null; // 受理した字句 (Token)
		char ch; // 最後に読んだ文字
		int startCol = colNo;
		StringBuffer text = new StringBuffer();

		int state = ST_NORMAL; // 状態番号
		boolean accept = false;  // 受理状態 (true) かそうではない (false) か
		while (!accept) {
			switch (state) {
				case ST_NORMAL: // 初期状態
					ch = readChar(); // 一文字読み込む
					if (ch == ' ' || ch == '\t' || ch == '\n' || ch == '\r') {
					} else if (ch == __EOF__) { // EOF
						startCol = colNo - 1;
						state = ST_EOF;
					} else if (ch >= '0' && ch <= '9') {
						startCol = colNo - 1;
						text.append(ch);
						state = ST_NUM;
					} else if (ch == '+') {
						startCol = colNo - 1;
						text.append(ch);
						state = ST_PLUS;
					} else if (ch == '-') {
						startCol = colNo - 1;
						text.append(ch);
						state = ST_MINUS;
					} else if (ch == '/') {
						state = ST_SLASH;
					} else { // この時点で受理できない文字を読んだので，ST_ILL に遷移
						startCol = colNo - 1;
						text.append(ch);
						state = ST_ILL;
					}
					break;
				case ST_EOF: // EOFを読んだ
					tk = new CToken(CToken.TK_EOF, lineNo, startCol, "end_of_file");
					accept = true;
					break;
				case ST_ILL: // ヘンな文字を読んだ
					tk = new CToken(CToken.TK_ILL, lineNo, startCol, text.toString());
					accept = true;
					break;
				case ST_NUM: // 数（10進数）の開始
					ch = readChar();
					if (Character.isDigit(ch)) {
						text.append(ch);
					} else {
						// 数の終わり
						backChar(ch); // 数を表さない文字は戻す（読まなかったことにする）
						tk = new CToken(CToken.TK_NUM, lineNo, startCol, text.toString());
						accept = true;
					}
					break;
				case ST_PLUS: // +を読んだ
					tk = new CToken(CToken.TK_PLUS, lineNo, startCol, "+");
					accept = true;
					break;
				case ST_MINUS: // -を読んだ
					tk = new CToken(CToken.TK_MINUS, lineNo, startCol, "-");
					accept = true;
					break;
				case ST_SLASH:
					ch = readChar(); //もう1文字読む
					if(ch == '/'){ // //を読んだ
						state = ST_COM;
					} else if (ch == '*') {// /*を読んだ
						state = ST_BLOCKCOM;
					} else { // /の後が/か*以外ならST_ILLに遷移
						backChar(ch);
						text.append('/');
						startCol = colNo - 1;
						state = ST_ILL;
					}
					break;
				case ST_COM: // //を読んだ
					ch = readChar();
					if(ch == '\n'){
						state = ST_NORMAL;
					}else if(ch == __EOF__){
						startCol = colNo - 1;
						state = ST_EOF;
					}
					break;
				case ST_BLOCKCOM: // /*を読んだ
					ch = readChar();
					if(ch == '*'){
						state = ST_BLOCKCOMASTA;
					}else if(ch == __EOF__){
						System.err.println("block comment 中に EOF を検出しました");
						startCol = colNo - 1;
						state = ST_EOF;
					}
					break;
				case ST_BLOCKCOMASTA:
					ch = readChar();
					if(ch == '/'){
						state = ST_NORMAL;
					}else if(ch == '*'){
					}else if(ch == __EOF__){
						System.err.println("block comment 中に EOF を検出しました");
						startCol = colNo - 1;
						state = ST_EOF;
					}else{
						state = ST_BLOCKCOM;
					}
					break;
					
			}
		}
		return tk;
	}
}
