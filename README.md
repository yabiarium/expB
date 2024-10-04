# miniCV00
miniCV00 for expB on Faculty of Informatics, Shizuoka University.

# memo
プログラムの入り口: src/main/java/lang/c/MiniCompiler.java
これからMiniCompilerlmpl.javaがnewされて実行。
そこからCodeGen()とかその他諸々の処理に広がっていく。
　1: Token字句解析
　2: Context文字解析?
　3: parse構文解析
　4: semanticCheck意味解析
　5: CodeGenコード生成（parse/Program.javaにある）


テキストP13
今後新規定義するクラスの codeGen() メソッドにおいて，コード生成の際にスタックを扱う場合は，
CodeGenCommonクラスの，printPushCodeGen(), printPopCodeGen()メソッドを利用すること

大きく分けて，「CTokenizer.java による字句解析のテスト」と「字句解析以降の処理のテスト」に分けられる．

## CTokenizer.java による字句解析のテスト
自動テストのコードは minicv00/test/java/lang/c/T00_21CTokenizerTest.java に記述してある

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
minicv00/test/java/lang/c/testhelper/CodeGenTestHelper.java で宣言されている．重要なメソッドは，
一つの入力について，「指定したクラス」の codeGen() が出力する「アセンブリコード」と「予測するアセンブリ
コード」が一致するかテストする，checkCodeGen() である．また，; から始まるコメントは自動で削除される．
また，「ラベル」行は行先頭から，「通常コードおよび擬似コード」は先頭に 4 文字空白が入る形に自動で整形され
る（ただし，予測コードの「通常コードおよび擬似コード」の先頭に空白等が 1 文字もない場合はその自動整形
は行われないので注意のこと．
* 1 章以降，自分で codeGen() メソッドのテストを書く際に注意すべき重要な点を 2 つ示しておく．1 点目は，ア
センブリコードの文法として，行の先頭から書けるのはラベルのみで，通常コードおよび擬似コードは，必ず先
頭に空白が必要である点．codeGen() のテストにおいても，「予測するアセンブリコード」において，ラベルが先
頭から，それ以外が先頭に空白 1 文字以上あることを注意深く確認のこと．詳細は下記プログラムのコメントを
熟読のこと，．2 点目は，「予測するアセンブリコード」は，miniCompiler で実行した結果を貼り付けてはいけな
い点．自分でちゃんと考えて作成すること．ただし，これまでに配布された，ないしは自分か考えた予測コード
をコピーするのは OK とする．チェック時に，明らかに miniCompiler の出力結果をただ貼り付けただけ，なこと
が判明した場合チェックをやり直す指示を出すので注意すること．




