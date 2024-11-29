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
	//CV01
	private final int ST_MINUS = 5;
	private final int ST_SLASH = 6;
	private final int ST_COM = 7; //COM=COMMENT
	private final int ST_BLOCKCOM = 8;
	private final int ST_BLOCKCOMASTA = 9; //ASTA=*
	//CV02
	private final int ST_AMP = 10;
	private final int ST_ZERO = 11; //0と1~9を分ける
	private final int ST_OCTNUM = 12;
	private final int ST_HEXNUM = 13;
	//CV03
	private final int ST_ASTA = 14; //* 乗算
	private final int ST_LPAR = 15; // (
	private final int ST_RPAR = 16; // )
	//CV04
	private final int ST_IDENT = 17; //変数
	private final int ST_LBRA = 18; // [
	private final int ST_RBRA = 19; // ]
	//CV05
	private final int ST_ASSIGN = 20; // =
	private final int ST_SEMI = 21; // ; (inputとoutputなどの予約語はST_IDENT内で判断するため、新規状態は必要ない)

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
					} else if (ch >= '1' && ch <= '9') {
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
					} else if (ch == '*') {
						startCol = colNo - 1;
						text.append(ch);
						state = ST_ASTA;
					} else if (ch == '/') {
						state = ST_SLASH;
					} else if (ch == '&'){
						startCol = colNo - 1;
						text.append(ch);
						state = ST_AMP;
					} else if (ch == '0') {
						startCol = colNo - 1;
						text.append(ch);
						state = ST_ZERO;
					} else if (ch == '(') {
						startCol = colNo - 1;
						text.append(ch);
						state = ST_LPAR;
					} else if (ch == ')') {
						startCol = colNo - 1;
						text.append(ch);
						state = ST_RPAR;
					} else if (ch == '[') {
						startCol = colNo - 1;
						text.append(ch);
						state = ST_LBRA;
					} else if (ch == ']') {
						startCol = colNo - 1;
						text.append(ch);
						state = ST_RBRA;
					} else if (ch == '_' || Character.isAlphabetic(ch)) {
						//変数ident １文字目は，’_’と英字(a-z A-Z)
						startCol = colNo -1;
						text.append(ch);
						state = ST_IDENT;
					} else if (ch == '='){
						startCol = colNo - 1;
						text.append(ch);
						state = ST_ASSIGN;
					} else if (ch == ';'){
						startCol = colNo - 1;
						text.append(ch);
						state = ST_SEMI;
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
					} else { // 数の終わり
						backChar(ch); // 数を表さない文字は戻す（読まなかったことにする）
						int intText = Integer.parseUnsignedInt(text.toString());
						if(32768 < intText){ //(符号付きなので)15bitより大きいなら
							tk = new CToken(CToken.TK_ILL, lineNo, startCol, text.toString());
						}else{
							tk = new CToken(CToken.TK_NUM, lineNo, startCol, text.toString());
						}
						accept = true;
					}
					break;
				case ST_PLUS: // +を読んだ
					tk = new CToken(CToken.TK_PLUS, lineNo, startCol, "+"); //トークンの発行
					accept = true; //一つの字句解析を抜ける→次の字句解析へ
					break;
				case ST_MINUS: // -を読んだ
					tk = new CToken(CToken.TK_MINUS, lineNo, startCol, "-");
					accept = true;
					break;
				case ST_ASTA:
					tk = new CToken(CToken.TK_MULT, lineNo, startCol, "*");
					accept = true;
					break;
				case ST_SLASH:
					ch = readChar(); //もう1文字読む
					if(ch == '/'){ // //を読んだ
						state = ST_COM;
					} else if (ch == '*') {// /*を読んだ
						state = ST_BLOCKCOM;
					} else if (ch == ' '){ // "2 / 3"など、/の後にスペースがあって数式が続く場合
						ch = readChar(); //更にもう1文字読む
						if(ch == '+' || ch == '-' || ch == '(' || Character.isDigit(ch)){
							backChar(ch);// トークン発効前に2文字分戻る
							backChar(ch);
							startCol = colNo - 2;
							text.append('/');
							tk = new CToken(CToken.TK_DIV, lineNo, startCol, "/");
							accept = true;
						}else{ // '/'の後スペースが空いて数式以外が続く場合はILL
							backChar(ch);
							backChar(ch);
							text.append('/');
							startCol = colNo - 2;
							state = ST_ILL;
						}
					}else if(ch == '+' || ch == '-' || ch == '(' || Character.isDigit(ch)){
						backChar(ch);
						startCol = colNo - 1;
						text.append('/');
						tk = new CToken(CToken.TK_DIV, lineNo, startCol, "/");
						accept = true;
					} else { // /の後が「/」「*」、「数字」以外ならST_ILLに遷移
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
				case ST_AMP: // &を読んだ
					tk = new CToken(CToken.TK_AMP, lineNo, startCol, "&");
					accept = true;
					break;
				case ST_LPAR: // (を読んだ
					tk = new CToken(CToken.TK_LPAR, lineNo, startCol, "(");
					accept = true;
					break;
				case ST_RPAR: // )を読んだ
					tk = new CToken(CToken.TK_RPAR, lineNo, startCol, ")");
					accept = true;
					break;
				case ST_LBRA: // [を読んだ
					tk = new CToken(CToken.TK_LBRA, lineNo, startCol, "[");
					accept = true;
					break;
				case ST_RBRA: // ]を読んだ
					tk = new CToken(CToken.TK_RBRA, lineNo, startCol, "]");
					accept = true;
					break;
				case ST_IDENT: // 変数の開始
					ch = readChar();
					//２文字目以降は，’_’と英字(a-z A-Z) と数字(0-9)
					if (ch == '_' || Character.isAlphabetic(ch) || Character.isDigit(ch)) {
						text.append(ch);
					} else { // 変数の終わり
						backChar(ch);

						//識別子を切り出す仕事が終わったら
						String s = text.toString();
						Integer i = (Integer)rule.get(s);
						//切り出した字句が登録済みキーワードかどうかはiがnullかどうかで判定する
						tk = new CToken(((i==null)?CToken.TK_IDENT:i.intValue()), lineNo, startCol, s);
						accept=true;
					}
					break;
				case ST_ZERO:
					ch = readChar();
					if(ch >= '0' && ch <= '7'){
						text.append(ch);
						state = ST_OCTNUM;
					}else if(ch == 'x' || ch == 'X'){
						text.append(ch);
						state = ST_HEXNUM;
					}else{
						backChar(ch);
						tk = new CToken(CToken.TK_NUM, lineNo, startCol, text.toString());
						accept = true;
					}
					break;
				case ST_OCTNUM: // 8進数
					ch = readChar();
					if(ch >= '0' && ch <= '7'){
						text.append(ch);
					}else{
						backChar(ch);
						int intText = Integer.parseUnsignedInt(text.toString().substring(1), 8);
						if(65535 < intText){ //16bitより大きいなら
							tk = new CToken(CToken.TK_ILL, lineNo, startCol, text.toString());
						}else{
							tk = new CToken(CToken.TK_NUM, lineNo, startCol, text.toString());
						}
						accept = true;
					}
					break;
				case ST_HEXNUM: // 16進数
					ch = readChar();
					if(ch >= '0' && ch <= '9' ||ch >= 'a' && ch <= 'f'){
						text.append(ch);
					}else{ //16進数が終わったら
						backChar(ch); // 数を表さない文字は戻す（読まなかったことにする）
						int intText;
						if(text.toString().length() <= 2){ // 0xだけを読んだとき
							intText = 65536; //範囲外にする
						}else{
							intText = Integer.parseUnsignedInt(text.toString().substring(2), 16);
						}
						
						if(65535 < intText){ //16bitより大きいなら
							tk = new CToken(CToken.TK_ILL, lineNo, startCol, text.toString());
						}else{
							tk = new CToken(CToken.TK_NUM, lineNo, startCol, text.toString());
						}
						accept = true;
					}
					break;
				case ST_ASSIGN: // =を読んだ
					tk = new CToken(CToken.TK_ASSIGN, lineNo, startCol, "=");
					accept = true;
					break;
				case ST_SEMI: // ;を読んだ
					tk = new CToken(CToken.TK_SEMI, lineNo, startCol, ";");
					accept = true;
					break;
					
			}
		}
		return tk;
	}
}
