# miniCompiler VersionXX
miniCV00 for expB on Faculty of Informatics, Shizuoka University.

## BNF
```
# ~CV07
program         ::= { statement } EOF  
statement       ::= statementAssign | statementInput | statementOutput | statementIf | statementWhile | statementBlock  
statementAssign ::= primary ASSIGN expression SEMI //左辺:primary=識別子(格納番地), 右辺:expression=値  
statementInput  ::= INPUT primary SEMI  
statementOutput ::= OUTPUT expression SEMI  
expression      ::= term { expressionAdd | expressionSub } //expression類の節点は値同士の演算を担う  
expressionAdd   ::= PLUS term  
expressionSub   ::= MINUS term  
term            ::= factor { termMult | termDiv }  
termMult        ::= MULT factor  
termDiv         ::= DIV factor  
factor          ::= plusFactor | minusFactor | unsignedFactor //factor類の節点は式の要素(符号+値)  
plusFactor      ::= PLUS unsignedFactor  
minusFactor     ::= MINUS unsignedFactor  
unsignedFactor  ::= factorAmp | number | LPAR expression RPAR | addressToValue //CV04: 値を表す節点　　
factorAmp       ::= AMP ( number | primary )  
primary         ::= primaryMult | variable  //識別子に関する節点  
primaryMult     ::= MULT variable  
variable        ::= ident [ array ]  
array           ::= LBRA expression RBRA  
ident           ::= IDENT  
addressToValue  ::= primary //識別子→値の変換  
number          ::= NUM  
condition       ::= TRUE | FALSE | expression ( conditionLT | conditionLE | conditionGT | conditionGE | conditionEQ | conditionNE )  
conditionLT     ::= LT expression  
conditionLE     ::= LE expression  
conditionGT     ::= GT expression  
conditionGE     ::= GE expression  
conditionEQ     ::= EQ expression  
conditionNE     ::= NE expression  
statementIf     ::= IF conditionBlock statement [ ELSE statement ]  
statementWhile  ::= WHILE conditionBlock statement  
statementBlock  ::= LCUR { statement } RCUR  
conditionBlock  ::= LPAR condition RPAR  

# CV08
conditionBlock  ::= LPAR conditionExpression RPAR　//変更
conditionExpression ::= conditionTerm { expressionOr }
expressionOr    ::= OR conditionTerm 
conditionTerm   ::= conditionFactor { termAnd }
termAnd         ::= AND conditionFactor
conditionFactor ::= notFactor | conditionUnsignedFactor
notFactor       ::= NOT conditionUnsignedFactor
conditionUnsignedFactor ::= condition | LBRA conditionExpression RBRA //条件式の優先度を示す括弧として[]を用いる

# CV10
program         ::= { declaration } { statement } EOF //変更
declaration     ::= intDecl | constDecl
intDecl         ::= INT declItem { COMMA declItem } SEMI
constDecl       ::= CONST INT constItem { COMMA constItem } SEMI
constItem       ::= [ MULT ] IDENT ASSIGN [ AMP ] NUM //初期値はNUMしか受け付けない
declItem        ::= [ MULT ] IDENT [ LBRA NUM RBRA ]

# CV11 {と}で囲まれた範囲の中でのみ有効な変数群を用意する
program         ::= { declaration } { declBlock } EOF　//変更
declBlock       ::= LCUR { declaration } { statement } RCUR //局所変数用のSymbolTableはここで作成と削除を行う

# CV12
program         ::= { declaraion } { function } EOF //変更
declaration     ::= intDecl | constDecl | voidDecl //変更
voidDecl        ::= VOID IDENT LPAR RPAR { COMMA IDENT LPAR RPAR } SEMI
declItem        ::= [ MULT ] IDENT [ LBRA NUM RBRA | LPAR RPAR ] //変更
function        ::= FUNC ( INT [ MULT ] | VOID ) IDENT LPAR RPAR declBlock
statement       ::= （長いので省略） | statementCall | statementReturn //変更
statementCall   ::= CALL ident LPAR RPAR SEMI
statementReturn ::= RETURN [ expression ] SEMI
variable        ::= ident [ array | call ]　 //変更
call            ::= LPAR RPAR

# CV13
function        ::= FUNC ( INT [ MULT ] | VOID ) IDENT LPAR [ argList ] RPAR declblock //変更
argList         ::= argItem { COMMA argItem } //関数名に紐づいている引数の情報と一致するか確認する
argItem         ::= INT [ MULT ] IDENT [ LBRA RBRA ] //局所変数用のSymbolTableの作成処理はこの節点に移す。削除はdeclBlockのまま
statementCall   ::= CALL ident LPAR [ expression { COMMA expression } ] RPAR SEMI //変更
call            ::= LPAR [ expressoin { COMMA expression } ] RPAR //変更
voidDecl        ::= VOID IDENT LPAR [ typeList ] RPAR { COMMA IDENT LPAR [ typeList ] RPAR } SEMI //変更
declItem        ::= [ MULT ] IDENT [ LBRA NUM RBRA | LPAR [ typeList ] RPAR ] //変更
typeList        ::= typeItem { COMMA typeItem } //関数名に引数の情報を紐づける
typeItem        ::= INT [ MULT ] [ LBRA RBRA ]
```

<details>
<summary>CV08以降について</summary>

### CV08のBNFの参考元  
```
expression      ::= term { expressionAdd | expressionSub }  
expressionAdd   ::= PLUS term  
term            ::= factor { termMult | termDiv }  
termMult        ::= MULT factor  
factor          ::= plusFactor | minusFactor | unsignedFactor  
plusFactor      ::= PLUS unsignedFactor  
unsignedFactor  ::= number | LPAR expression RPAR 
```

### CV09~のエラー仕様書
[ERROR.md](./ERROR.md)

### CV13
 - 局所変数用のSymbolTableの作成と削除はこれまでと変わらずdeclBlockで行う。  
 - 関数名(グローバル変数)の登録の際(プロトタイプ宣言時)に、関数名に引数情報(argTypeList)を紐づけておく。  
 - ↑関数名への引数情報の紐づけはTypeListで行う(関数名はVoidDeclとintDecl→DeclItemから持ってくる)
 - 実引数をローカル変数として登録するのはargItemだが、functionから呼ばれる順敵にdeclBlockよりも先にargItemの解析が行われるため、局所変数用のSymbolTableの作成処理はargItemに移す必要がある

</details>  

## メモ
 > [!NOTE]
 > プログラムの任意の行にブレークポイントを設置し、デバッグ実行することでプログラムの処理を一行ずつ追うことができる。

term ::= factor { (PLUS | MINUS) factor }  
小文字の名前は「非終端記号」Non Terminal を，大文字の名前は「終端記号」Terminal を表す

プログラムの入り口: src/main/java/lang/c/MiniCompiler.java  
これからMiniCompilerlmpl.javaがnewされて実行。  
そこからその他諸々の処理に広がっていく。  
　1: Token字句解析  
　2: Context文字解析?  
　3: parse構文解析  
　4: semanticCheck意味解析  
　5: CodeGenコード生成（parse/Program.javaにある）  


<details>
<summary>実験書のメモ</summary>

## メモ
テキスト p9  
* 字句解析部は、「単なる文字の列」である入力ファイルを読み、意味のあるまとまりごとに区切って「字句（トークン）の列」へと作り変える。  
* 構文解析部は、字句列を読み、それらが与えられた構文定義（文法規則）にしたがって並んでいるかどうかを確認し、構文木を作る。  
* 意味解析部は、構文木を深さ優先探索しながら、意味上の誤り（変数名の宣言がないとか、変数の使い方が違うとか. . . ）がないかどうかをチェックする2。このとき、プログラム中に出てくる名前（識別子と呼ぶこともよくある）がどのような意味を持つのかを「記号表」で管理していく（ただし、変数の宣言と記号表によるその管理についてのプログラミングは第 II 部に回す）。  
* コード生成部は、構文木を深さ優先探索しながら、行きがけ、通りがけ、帰りがけに、構文の意味するところを実施できるようなコードを作り出していく。  



テキストP13  
* 今後新規定義するクラスの codeGen() メソッドにおいて，コード生成の際にスタックを扱う場合は，CodeGenCommonクラスの，printPushCodeGen(), printPopCodeGen()メソッドを利用すること  

大きく分けて，「CTokenizer.java による字句解析のテスト」と「字句解析以降の処理のテスト」に分けられる．

## CTokenizer.java による字句解析のテスト
自動テストのコードは minicv00/test/java/lang/c/T00_21CTokenizerTest.java に記述してある
 > [!NOTE]
 > プログラムの任意の行にブレークポイントを設置し、デバッグ実行することでプログラムの処理を一行ずつ追うことができる。

## 字句解析以降の処理のテスト
### 構文解析その 1: isFirst() メソッドのテスト
自動テストのコードは minicv00/test/java/lang/c/parse/T00_31IsFirstTest.java に記述してある
中身で使用している IsFirstTestHelper クラスは，
minicv00/test/java/lang/c/testhelper/IsFirstTestHelper.java で宣言されている．重要なメソッドは，
一つの入力を単体でテストする trueTest() メソッド，falseTest() メソッドと，String 型配列に入れた複数の
入力を全部テストする trueListTest() メソッド，falseListTest() メソッドである．
### 構文解析その 2: parse() メソッドのテスト
自動テストのコードは minicv00/test/java/lang/c/parse/T00_32ParseTest.java に記述してある．
中身で使用している ParseTestHelper クラスは，
minicv00/test/java/lang/c/testhelper/ParseTestHelper.java で宣言されている．重要なメソッドは，
一つの入力を単体でテストする parseAcceptTest() メソッド，parseRejectTest() メソッドと，String 型配列
に入れた複数の入力を全部テストする parseAcceptTestList() メソッド，parseRejectTestList() メソッドで
ある．
### 意味チェック: semanticCheck() メソッドのテスト
自動テストのコードは minicv00/test/java/lang/c/parse/T00_41SemanticCheckTest.java に記述してある．
中身で使用している SemanticCheckTestHelper クラスは，
minicv00/test/java/lang/c/testhelper/SemanticCheckTestHelper.java で宣言されている．重要なメ
ソッドは，一つの入力について意味チェックが通るか NG かテストする acceptTest() メソッド，rejectTest()
メソッドと，String 型配列に入れた複数の入力を全部テストする acceptListTest() メソッド，入力文字列と予
想エラーメッセージの配列を全部テストする rejectListTest() メソッドである．また，SemanticCheck() にお
いては，特に掛け算割り算やポインタの実装後に，解析中のノードが，整数なのかポインタなのか配列なのか な
どのテストをする必要がある．そのために，typeTest() メソッドが実装されている．
### コード生成: codeGen() メソッドのテスト
自動テストのコードは minicv00/test/java/lang/c/parse/T00_51CodeGenTest.java に記述してある．
中身で使用している CodeGenTestHelper クラスは，
minicv00/test/java/lang/c/testhelper/CodeGenTestHelper.java で宣言されている．重要なメソッドは，一つの入力について，「指定したクラス」の codeGen() が出力する「アセンブリコード」と「予測するアセンブリ
コード」が一致するかテストする，checkCodeGen() である．また，; から始まるコメントは自動で削除される．
また，「ラベル」行は行先頭から，「通常コードおよび擬似コード」は先頭に 4 文字空白が入る形に自動で整形され
る（ただし，予測コードの「通常コードおよび擬似コード」の先頭に空白等が 1 文字もない場合はその自動整形
は行われないので注意のこと． 

\* 1 章以降，自分で codeGen() メソッドのテストを書く際に注意すべき重要な点を 2 つ示しておく．1 点目は，ア
センブリコードの文法として，行の先頭から書けるのはラベルのみで，通常コードおよび擬似コードは，必ず先
頭に空白が必要である点．codeGen() のテストにおいても，「予測するアセンブリコード」において，ラベルが先
頭から，それ以外が先頭に空白 1 文字以上あることを注意深く確認のこと．詳細は下記プログラムのコメントを
熟読のこと，．2 点目は，「予測するアセンブリコード」は，miniCompiler で実行した結果を貼り付けてはいけな
い点．自分でちゃんと考えて作成すること．ただし，これまでに配布された，ないしは自分か考えた予測コード
をコピーするのは OK とする．チェック時に，明らかに miniCompiler の出力結果をただ貼り付けただけ，なこと
が判明した場合チェックをやり直す指示を出すので注意すること．

</details>


