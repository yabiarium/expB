# エラー仕様書（CV09）

<details>
<summary>エラー処理があるBNF一覧</summary>

o →エラー処理がある  
x →エラー処理がない

```
o program         ::= { statement } EOF  
x statement       ::= statementAssign | statementInput | statementOutput  
o statementAssign ::= primary ASSIGN expression SEMI  
o statementInput  ::= INPUT primary SEMI  
o statementOutput ::= OUTPUT expression SEMI  
x expression      ::= term { expressionAdd | expressionSub }  
o expressionAdd   ::= PLUS term  
o expressionSub   ::= MINUS term  
x term            ::= factor { termMult | termDiv }  
o termMult        ::= MULT factor  
o termDiv         ::= DIV factor  
x factor          ::= plusFactor | minusFactor | unsignedFactor  
o plusFactor      ::= PLUS unsignedFactor  
o minusFactor     ::= MINUS unsignedFactor  
o unsignedFactor  ::= factorAmp | number | LPAR expression RPAR | addressToValue  
o factorAmp       ::= AMP ( number | primary )  
x primary         ::= primaryMult | variable  
o primaryMult     ::= MULT variable  
o variable        ::= ident [ array ]  
o array           ::= LBRA expression RBRA  
o ident           ::= IDENT  
x addressToValue  ::= primary
x number          ::= NUM  
o condition       ::= TRUE | FALSE | expression ( conditionLT | conditionLE | conditionGT | conditionGE | conditionEQ | conditionNE )  
o conditionLT     ::= LT expression  
o conditionLE     ::= LE expression  
o conditionGT     ::= GT expression  
o conditionGE     ::= GE expression  
o conditionEQ     ::= EQ expression  
o conditionNE     ::= NE expression  
x statement       ::= statementAssign | statementInput | statementOutput | statementIf | statementWhile | statementBlock  
o statementIf     ::= IF conditionBlock statement [ ELSE statement ]  
o statementWhile  ::= WHILE conditionBlock statement  
o statementBlock  ::= LCUR { statement } RCUR  
o conditionBlock  ::= LPAR condition RPAR  

# CV08
o conditionBlock  ::= LPAR conditionExpression RPAR　//変更
x conditionExpression ::= conditionTerm { expressionOr }
o expressionOr    ::= OR conditionTerm 
x conditionTerm   ::= conditionFactor { termAnd }
o termAnd         ::= AND conditionFactor
x conditionFactor ::= notFactor | conditionUnsignedFactor
o notFactor       ::= NOT conditionUnsignedFactor
o conditionUnsignedFactor ::= condition | LBRA conditionExpression RBRA //条件式の優先度を示す括弧として[]を用いる
```

</details>

## 各非終端記号でのエラー処理一覧
💫 warning          → コード生成可能  
🍀 recoverableError → ひとつでもあったらコード生成しない、parseと意味チェックはどうにか進めてエラーを出す  
❌ fatalerror       → 0個の可能性もある。自分が致命的と思ったら使ってもいい    
  
すべての行で;が抜けていたり、(){}, 予約語のミスなど、ユーザのミスがかなり多い場合はコンパイラはどうにもできない。全行;が無くてコンパイルすっ飛ばす可能性も全然ある。  
エラーの分類基準が一貫していればよい。こだわりだすとずっとこだわれる部分なので、実装のやりやすい範囲で作ればよい。  

 > [!NOTE]
 > <details>
 > <summary>全体の方針</summary>  
 >
 > - StatementXX 以下に他のほとんどの節点が続く。  
 > Conditionが無い → Assign, Input, Output, Block（すべてStatementと他のトークンの組み合わせ）  
 > Conditionが有る → If, While（ConditionBlockの後にStatementがある）
 > - 構文木はstatement以下と、condition以下に続くもので2分される。
 > - よって、🍀があった場合、 <b>処理（どのトークンまで読み飛ばすか）</b> はStatementXXで行い、それ以下に続く節点では🍀の発行のみ行い、処理は構文木上の上の節点にあたるStatementXXに託す造りとした。
 > </details>  
 > <br>
 >
 > <details>
 > <summary>意味チェックのエラーを🍀とした根拠について（<ins>デバッグが面倒になりそう</ins>の詳細）</summary>
 >
 > - OSやSEP3のハード保護により、書き込んではいけないメモリ領域に書き込むことは起きない。
 > - 構文解析が正しく終了しているなら、BNFが正しければ変なコード(実行できないコード)が生成されることはない。  
 >   → 上の2点より、❌にする必要はない   
 >  
 > - 意味チェックでのエラーは型が合っていない場合である。SEP3ではポインタも整数型も同じ"数値"として扱い、違いが無い。    
 > → コンパイラの次点で型判定しないと、SEP3での実行の際には区別されないので問題なく実行できてしまう。  
 > - 想定していない型同士での演算（pint*intなど）~~や比較~~により、ユーザが自身で作成したスタックやリストなどの領域を意図せず書き換えてしまう可能性がある。（比較は左右にbool型以外来ることがない造りになっているので除外）  
 > → 実行時、エラーは出ないが想定外の動作をする、といった場合が考え得る。（<ins>デバッグが面倒</ins>）なので、変に実行できないようにコード生成されないようにしたかった。🍀を使用しているが、ニュアンスとしては "コード生成しない💫" である。  
 > </details>

### program:
 - [x] 💫 parse(): プログラムの最後にゴミがあります  
        → 読み飛ばす  
        ` i_a = 0 ;; ` **←test.cに貼り付けてテストする**

### statementAssign:
 - [x] 🍀 parse(): =がありません  
        → 今のトークンがexpressionなら=を補って💫にする  
        ` i_a  0; `  
        ` i_a ; `  
 - [x] 🍀 parse(): =の後ろはexpressionです  
        → 次の;まで飛ばす（expressionが不定）  
        ` i_a = ; `  
 - [x] 💫 parse(): ;がありません  
        → expressionの解析後なので;を補う（「i_a=1 2;」のようにexpressionの途中であろう位置で抜けてしまう場合は1で解析が止まる）  
        ` i_a = 0 `  
        ` i_a = 7  1; //7までで止まる `  
 - [x] 🍀 semanticCheck(): 左辺の型["+lts+"]と右辺の型["+rts+"]が異なります  
        → 書き換えてはいけないアドレスに書き込むようになっているといけないのでコード生成しない  
        ` i_a = i_a + ip_a; `
 - [x] 🍀 semanticCheck(): 定数には代入できません  
        ` c_a = i_a; `

### statementInput:
 - [x] 🍀 parse(): inputの後ろはprimaryです  
        → 次の;まで飛ばす（primaryが不定）  
        ` input ; `  
 - [x] 💫 parse(): ;がありません  
        → primaryの解析後なので、;を補う  
        ` input i_a `  
 - [x] 🍀 semanticCheck(): 定数には代入できません  
        ` input c_a; `

### statementOutput:
 - [x] 🍀 parse(): outputの後ろはexpressionです  
        → 次の;まで飛ばす  
        ` output ; `  
 - [x] 💫 parse(): ;がありません  
        → primaryの解析後なので、;を補う  
        ` output c_a `  

### expressionAdd:
 - [x] 🍀 parse(): +の後ろはtermです  
        → 回復エラーだけ出して処理はstatementAssign/Input/Output/Blockに任せる  
        ` i_a = 7 + ; ` 
 - [x] 🍀 semanticCheck(): 左辺の型[" + lts + "]と右辺の型[" + rts + "]は足せません  
        → ~~演算は結果をスタックに積むだけでメモリの変更ないのでコード生成してもよさそう~~  
        → 実行できてしまって想定通りの動作をしなかった場合の<ins>**デバッグが面倒になりそう**</ins>なのでコンパイルしない（詳細は冒頭のNoteに記述）  
        ` ip_a = ip_a + ip_a; `

### expressionSub:
 - [x] 🍀 parse(): -の後ろはtermです  
        → 回復エラーだけ出して処理はstatementAssign/Input/Output/Blockに任せる  
        ` i_a = 7 - ; `
 - [x] 🍀 semanticCheck(): 左辺の型[" + lts + "]から右辺の型[" + rts + "]は引けません   
        → 実行できてしまって想定通りの動作をしなかった場合のデバッグが面倒になりそうなのでコンパイルしない  
        ` ip_a = i_a - ip_a; `

### termMult:
 - [x] 🍀 parse(): *の後ろはfactorです  
        → 回復エラーだけ出して処理はstatementAssign/Input/Output/Blockに任せる  
        ` i_a = 7 * ; `
 - [x] 🍀 semanticCheck(): 左辺の型[" + lts + "]と右辺の型[" + rts + "]は掛けられません  
        → 実行できてしまって想定通りの動作をしなかった場合のデバッグが面倒になりそうなのでコンパイルしない  
        ` ip_a = i_a * ip_a; `

### termDiv:
 - [x] 🍀 parse(): /の後ろはfactorです  
        → 回復エラーだけ出して処理はstatementAssign/Input/Output/Blockに任せる  
        ` i_a = 7 / ; ` ~~// 単体/の後ろが数式(+,-,(,数字)以外の場合は字句解析で/=ILLとなる~~  
        ~~↑ なのでこのエラーが出ることはない~~  
        CTokenizerを"/"の後に変数(a~z,A~Z)を許可するよう変更。
 - [x] 🍀 semanticCheck(): 左辺の型[" + lts + "]は右辺の型[" + rts + "]で割れません  
        → 実行できてしまって想定通りの動作をしなかった場合のデバッグが面倒になりそうなのでコンパイルしない  
        ` i_a = ip_a / i_a; `

### plusFactor:
 - [x] 🍀 parse(): +の後ろはunsignedFactorです  
        → 回復エラーだけ出して処理はstatementAssign/Input/Output/Blockに任せる  
        ` i_a = 7 + +; `
 - [x] 🍀 semanticCheck(): +の後ろはT_intです[" + rts + "]  
        → 想定以外の型がくると生成コードがめちゃくちゃになりそう  
        ` ip_a = i_a + +ip_a; `

### minusFactor:
 - [x] 🍀 parse(): -の後ろはunsignedFactorです  
        → 回復エラーだけ出して処理はstatementAssign/Input/Output/Blockに任せる  
        ` i_a = 7 + -; `
 - [x] 🍀 semanticCheck(): -の後ろはT_intです[" + rts + "]  
        → 想定以外の型がくると生成コードがめちゃくちゃになりそう  
        ` ip_a = i_a + -ip_a; `

### unsignedFactor:
 - [x] 💫 parse(): )がありません  
        → expressionの解析後なので)を補う。（「(3+2 4)だと、2の後に)を補うことになる。4)はprogramのisFirst()でエラーになる」）  
        ↑ isFirst()でエラーになる場合ってコンパイルの経過どうなるの？  
        `i_a = (7 + 0 ; `
 - [x] 🍀 parse(): (の後ろはexpressionです  
        → 回復エラーだけ出して処理はstatementAssign/Input/Output/Blockに任せる  
        ` i_a = ( ; `

### factorAmp:
 - [x] 🍀 parse(): &の後ろに*は置けません  
         → 回復エラーだけ出して処理はstatementAssign/Input/Output/Blockに任せる  
         ` i_a = &* ; `
 - [x] 🍀 parse(): &の後ろはnumberまたはprimaryです  
         → 回復エラーだけ出して処理はstatementAssign/Input/Output/Blockに任せる  
         ` i_a = &[] ; `
 - [x] 🍀 semanticCheck(): &の後ろはT_intです["+ts+"]  
        → 想定以外の型がくると生成コードがめちゃくちゃになりそう  
        ` ip_a = i_a + &ip_a; `

### primaryMult:
 - [x] 🍀 parse(): *の後ろはvariableです  
        → ~~]まで飛ばす。なければ次の;まで飛ばす~~  
        ~~（*が使われるのはAddressToValueか代入先の変数の前のどちらか。前者なら後ろに;があるはず、後者でも;まで行って一行まるっと飛ばすか、配列に使われていたなら]で止められる）~~  
        → 回復エラーだけ出して処理はstatementAssign/Input/Output/Blockに任せる  
        ` i_a = * ; `
 - [x] 🍀 semanticCheck(): \*の後ろは[int*]です  
        → 想定以外の型がくると生成コードがめちゃくちゃになりそう  
        ` input *i_a; `

### variable:
 - [x] 🍀 semanticCheck(): 配列変数は T_int_array か T_pint_array です  
        → 想定以外の型がくると生成コードがめちゃくちゃになりそう  
        ` i_a[0] = 0; `
 - [x] 🍀 semanticCheck(): 配列型の後ろに[]がありません  
        ` ia_a = 0; `

### array: 
 - [x] 💫 parse(): ]がありません  
        → expression解析後のエラーなので、expressinはそこで終了とみなして]を補う  
        ` ia_a[0 = 0; `
 - [x] 🍀 parse(): [の後ろはexpressionです  
        → expression内でのエラー。expressionが不明となる。]か;まで飛ばす  
          （]はarray自身の範囲内の終了を示す。;は外側の（例えばStatementAssign）の終わりを表す。そこも飛んだら次の行の;を読む。「if(){ i_a[0 = 1 } i_a=0; 」の文だと、次の;まで飛ぶのでifの}を飛ばしてしまうが、if側でどうにかする。間違いまみれならどうしようもないのでまともなコンパイルエラーは諦める）  
        ` ia_a[] = 0; `

### ident:
 - [x] 🍀 semanticCheck(): 変数名規則に合っていません  
       → ~~意味解析でのエラー。変数の型が不明だと以降の意味解析に支障が出る。~~  
         ~~一時的にint型として、以降で出る意味解析でのエラーは構文木の上の階層の意味解析に任せる~~  
        → 実行できてしまって想定通りの動作をしなかった場合のデバッグが面倒になりそうなのでコンパイルしない  
        ` ib = 0; `  
        ` ib_ = 0; `

### condition:
 - [x] 🍀 parse(): expressionの後ろにはconditionXXが必要です  
        → ~~)まで飛ばす→{からstatementBlock~~  
        → ) ; まで飛ばす処理はcondithionBlockに継ぐ  
        ` if(i_a )i_a=0; `

### conditionLT:
 - [x] 🍀 parse(): <の後ろはexpressionです  
        → ~~)まで飛ばす→{からstatementBlock（他のconditionXXも同様）~~   
        → conditionに引き継ぐために、conditionXX内では回復エラーを出すだけで何もしない  
        ` if(i_a < )i_a=0; `
 - [x] 🍀 semanticCheck(): 左辺の型["+lts+"]と右辺の型["+rts+"]が一致しないので比較できません  
        ` if(i_a < ip_a){} `

### conditionLE:
 - [x] 🍀 parse(): <=の後ろはexpressionです  
        ` if(i_a <= )i_a=0; `
 - [x] 🍀 semanticCheck(): 左辺の型["+lts+"]と右辺の型["+rts+"]が一致しないので比較できません  
        ` if(i_a <= ip_a){} `

### conditionGT:
 - [x] 🍀 parse(): >の後ろはexpressionです  
        ` if(i_a > )i_a=0; `
 - [x] 🍀 semanticCheck(): 左辺の型["+lts+"]と右辺の型["+rts+"]が一致しないので比較できません  
        ` if(i_a > ip_a){} `
 
### conditionGE:
 - [x] 🍀 parse(): >=の後ろはexpressionです  
        ` if(i_a >= )i_a=0; `
 - [x] 🍀 semanticCheck(): 左辺の型["+lts+"]と右辺の型["+rts+"]が一致しないので比較できません  
        ` if(i_a >= ip_a){} `

### conditionEQ:
 - [x] 🍀 parse(): ==の後ろはexpressionです  
        ` if(i_a == )i_a=0; `
 - [x] 🍀 semanticCheck(): 左辺の型["+lts+"]と右辺の型["+rts+"]が一致しないので比較できません  
        ` if(i_a == ip_a){} `

### conditionNE:
 - [x] 🍀 parse(): !=の後ろはexpressionです  
        ` if(i_a != )i_a=0; `
 - [x] 🍀 semanticCheck(): 左辺の型["+lts+"]と右辺の型["+rts+"]が一致しないので比較できません  
        ` if(i_a != ip_a){} `

### statementIf:
 - [x] 🍀 parse(): ifの後ろはconditionBlockです  
        → )まで飛ばす →{からstatement →なければ次の;まで飛ばす  
        ` if i_a>0){ i_a=0; } `  
        ` if i_a>0{ i_a=0; } `  
        ` if i_a>0 i_a=0; } `   
        ↑ 3つ目程トークン抜けがあると、「(」ない→;まで飛ばす、「{」(statementの開始)がない→;まで飛ばす。で2重で;まで飛ばす処理が入るので、次の行の構文解析が飛ばされることになるが、許容。  
 - [x] 🍀 parse(): conditionBlockの後ろはstatementです  
        → 次の;まで飛ばす  
        ` if(i_a>0); `  
        ` if(i_a>0) i_a=0; } //}だけが余計なもの `  
 - [x] 🍀 parse(): elseの後ろはstatementです  
        → 次の;まで飛ばす  
        ` if(i_a>0){}else; `
 
### statementWhile:
 - [x] 🍀 parse(): whileの後ろはconditionBlockです  
        → )まで飛ばす →{からstatement →なければ次の;まで飛ばす  
        ` while i_a>0){ i_a=0; } `  
        StatementIfと同じなので省略  
 - [x] 🍀 parse(): conditionBlockの後ろはstatementです  
        → 次の;まで飛ばす  
        ` while(i_a>0); `

### statementBlock:
 - [x] 🍀 statement内部でエラー  
        → ;か}まで読み飛ばす  
        ` if(i_a > 0){ if(i_a < ) i_a=0; }else if(){ i_a=0; } // statementBlock内部で回復エラーが発生した場合、}以降から再開されるかの確認`
 - [x] 💫 parse(): }がありません  
        → }を補う  
        ` if(i_a > 0){ i_a=0; `

### conditionBlock:
 - [x] 🍀 parse(): (の後ろはconditionExpressionです  
        → )まで飛ばす →{からstatement →なければ次の;まで飛ばす  
        ` if() i_a=0; `  
 - [x] 💫 parse(): )がありません  
        → )を補う  
        ` if(i_a < 0 i_a=0; // )が補われ、コード生成 `

### expressionOr:
 - [x] 🍀　parse(): ||の後ろはconditionTermです  
        → ConditionBlockで対処するのでここでは回復エラーだけ出して何もしない  
        ` if(i_a < 0 || ) i_a=0; `
 - [x] 🍀 semanticCheck(): 左辺の型[" + lts + "]と右辺の型[" + rts + "]はT_boolである必要があります  
        → 実行できてしまって想定通りの動作をしなかった場合のデバッグが面倒になりそうなのでコンパイルしない  
        ` if(true || i_a > 0){} `  || の左右にbool型以外を入れられない作りになっている

### termAnd:
 - [x] 🍀 parse(): &&の後ろはconditionFactorです  
        → ConditionBlockで対処するのでここでは回復エラーだけ出して何もしない  
        ` if(i_a < 0 && ) i_a=0; `
 - [x] 🍀 semanticCheck(): 左辺の型[" + lts + "]と右辺の型[" + rts + "]はT_boolである必要があります  
        → 実行できてしまって想定通りの動作をしなかった場合のデバッグが面倒になりそうなのでコンパイルしない  
        ` if(true && i_a > 0){} `  || の左右にbool型以外を入れられない作りになっている

### notFactor:
 - [x] 🍀 parse(): !の後ろはConditionUnsignedFactorです  
        → ConditionBlockで対処するのでここでは回復エラーだけ出して何もしない  
        ` if(! ) i_a=0; `
 - [x] 🍀 semanticCheck(): !の後ろはT_boolです[" + rts + "]  
        → 実行できてしまって想定通りの動作をしなかった場合のデバッグが面倒になりそうなのでコンパイルしない  
        ` if(!i_a > 0){} `  ! の後ろにbool型以外を入れられない作りになっている

### conditionUnsignedFactor:
 - [x] 🍀 parse(): [の後ろはconditionExpressionです  
        → ]まで飛ばす →なければ)まで飛ばしてconditionBlockで終わり、保険で;  
        ` if([ < 0]) i_a=0; `
 - [x] 💫 parse(): ]がありません
        → ]を補う  
        ` if([i_a < 0 ) i_a=0; `



## CV10,11の節点

```
# CV10
x program         ::= { declaration } { statement } EOF //変更
x declaration     ::= intDecl | constDecl
o intDecl         ::= INT declItem { COMMA declItem } SEMI
o constDecl       ::= CONST INT constItem { COMMA constItem } SEMI
o constItem       ::= [ MULT ] IDENT ASSIGN [ AMP ] NUM
o declItem        ::= [ MULT ] IDENT [ LBRA NUM RBRA ]

# CV11
x program         ::= { declaration } { declBlock } EOF　//変更
o declBlock       ::= LCUR { declaration } { statement } RCUR
```
int/constDecl以下での🍀は、エラーだけ出して処理はこの2つの節点に託す  
 
### intDecl:
 - [x] 🍀 parse(): IDENTがありません  
       → ; まで読み飛ばす  
       ` int a, *b, c[10] *d[10]; // ,が抜けてる `  
       ↑ `int ..., c[10]; *d[10]=xx;` と見分けがつかないため、「,がありません」の💫を実装できない。  
       ` int 10; // 識別子無し `
 - [x] 💫 parse(): intDecl: ; を補いました  
       ` int *d[10] // ;がない `   
       ` int e=3; // constがない（＝のところに,か;がないエラー…と出るはず） `  
       ↑ 初期値の代入ができるのは定数constのみ。逆にconstは初期値がないとエラー。

### constDecl:
 - [x] 💫 parse(): INT を補いました  
       ` const a=0; `
 - [x] 🍀 parse(): INTがありません (型指定がない)  
       → ; まで読み飛ばす  
       `const =0;`
 - [x] 🍀 parse(): IDENTがありません  
       → ; まで読み飛ばす  
       `const int =0;`
 - [x] 💫 parse(): ; を補いました  
       ` const int e=3 //;がない `

### constItem:
 - [x] 🍀 parse(): *の後ろは IDENT です  
       `const int *=0;`
 - [x] 🍀 parse(): =がありません  
       ` const int e; // 初期値がない `
 - [x] 💫 parse(): =を補いました  
       ` const int e 3; // ＝がない `
 - [x] 🍀 parse(): 定数の初期化がありません  
       `const int a=;`

### declItem:
 - [x] 🍀 parse(): *の後ろは IDENT です  
       `int *=0;`
 - [x] 🍀 parse(): 配列の要素数がありません  
       `int a[]=0;`
 - [x] 💫 parse(): ] を補いました  
       ` int c[10; // ]が閉じてない `


### declBlock:(CV11)
 - [x] 💫 parse(): } を補いました  
 - global 変数と同じ名前の local 変数が使えること（かつ，参照時に正しく local の方を参照できることの確認  
       ` int a;{int *a; a=1;} `
 - local 変数の2重登録チェック  
       ` int a;{int *a; int a; a=1;} `
 - const local 変数への代入文チェック  
       ` {const int a=0; a=1;} `  



## CV12の節点

```
# CV12
x program         ::= { declaraion } { function } EOF //変更
x declaration     ::= intDecl | constDecl | voidDecl //変更
o voidDecl        ::= VOID IDENT LPAR RPAR { COMMA IDENT LPAR RPAR } SEMI
o declItem        ::= [ MULT ] IDENT [ LBRA NUM RBRA | LPAR RPAR ] //変更
o function        ::= FUNC ( INT [ MULT ] | VOID ) IDENT LPAR RPAR declBlock
x statement       ::= （長いので省略） | statementCall | statementReturn //変更
o statementCall   ::= CALL ident LPAR RPAR SEMI
o statementReturn ::= RETURN [ expression ] SEMI
x variable        ::= ident [ array | call ]　 //変更
o call            ::= LPAR RPAR
o unsignedFactor ::= factorAmp | number | LPAR expression RPAR | addressToValue | CALL ident LPAR RPA //変更
```

### function:
 - [x] 💫 parse(): 返り値の型を指定してください  
       `func { a;}`
 - [x] 🍀 parse(): 識別子(ident)がありません  
       → (, ), { まで読み飛ばしてdeclBlockの判定へ  
       `func { a;}`  
 - [x] 🍀 parse(): 同じ識別子の関数があります  
       → (, ), { まで読み飛ばしてdeclBlockの判定へ  
       ` `
 - [x] 💫 parse(): ( を補いました  
       `func { a;}`
 - [x] 💫 parse(): ) を補いました  
       `func { a;}`
 - [x] 🍀 parse():  declBlock( { )がありません
       → func まで読み飛ばす  
       `func a() a;} func int () {}`
 - [x] 🍀 semanticCheck(): この識別子は関数として宣言されていません  
       `const int funcA = 0; func int funcA(){}`

### declItem:
 - [x] 💫 parse(): ) を補いました  
       `int a(;`

### voidDecl:
 - [x] 🍀 parse(): 識別子(ident)がありません  
       → ;まで飛ばす  
       `void (), b();`
 - [x] 💫 parse(): ( を補いました  
       `void a), b();`
 - [x] 💫 parse(): ) を補いました  
       `void a(, b();`
 - [x] 💫 parse(): ; を補いました  
       `void a(), b()`  
 - [x] 🍀 semanticCheck(): 既に宣言されています  
       `void funcA(), funcA();`

### statementCall:
 - [x] 🍀 parse(): 識別子(ident)がありません  
       → ;まで飛ばす  
       `func int a(){ call (); b; }`
 - [x] 💫 parse(): ( を補いました  
       `func int a(){ call a); }`
 - [x] 💫 parse(): ) を補いました  
       `func int a(){ call a(; }`
 - [x] 💫 parse(): ; を補いました  
       `func int a(){ call a() }`

### statementReturn:
 - [x] 💫 parse(): ; を補いました   
       `func int a(){ return 0 }`

### call:
 - [x] 💫 parse(): ) を補いました    
       `func int a(){ input a(; }`

### unsignedFactor:
 - [x] 💫 parse(): ( を補いました  
       ```
       //test1
       void funcA();
       int funcB(),a;
       func void funcA(){
       a = call funcB) + 1;
       }
       func int funcB(){
       return 1;
       }
       ```
 - [x] 💫 parse(): ) を補いました  
       `↑(test1)を使用`
 - [x] 🍀 parse(): callの後ろはidentです  
       `↑(test1)を使用`

### declBlock:
 - [x] 🍀 semanticCheck(): 関数の型が必要です  
       → return文が存在するのにfunctionの型がない(err)場合  
       `↑(test1)のfuncBの型を消す`  
 - [x] 🍀 semanticCheck(): 関数がvoid型にもかかわらず、返り値が存在します  
       → return文と式が存在するのにfunctionがvoidの場合  
       `↑(test1)のfuncAに return 1; を追記`
 - [x] 🍀 semanticCheck(): xx型の返り値が必要です  
       → return文が存在しないのにfunctionの型がある(void/err以外)場合  
       `↑(test1)のfuncBの return 1; を消す`
 - [x] 🍀 semanticCheck(): 関数の型["+functinoTypeS+"]と返り値の型["+sts+"]が異なります  
       → functionの型がvoid/err以外で、return文の型と不一致  
       `↑(test1)のfuncBを int a[0]; return a[9]; に書き換え`


### semanticCheck():
 - [x] プロトタイプ宣言はちゃんと機能していますか？  
       ```
       int a;
       void funcA();
       int funcB();
       func int funcA(){
       a = call funcB() + 1;
       return a;
       }
       ```
 - [x] *プロトタイプ宣言がない関数を使おうとしたときにちゃんとエラーを出せますか？  
       `↑の int funcB(); をコメントアウト`
 - [x] *プロトタイプ宣言がある関数について，宣言時の型と定義時の型が違うときにエラーが出せますか？  
       `int funcA(); func int* funcA(){}`
 - [x] return の処理で，定義時に設定された戻り値の型と同じ型の値を return できているか確認  
       → DeclBlockよりもfunctionで判定する方がBNF的に綺麗そうだが、functionで判定できるのはDeclBlockの解析を全て行ったあとなので、複数箇所にreturnがある場合、どこのreturnでのエラーなのか特定できない。なので、functionの型をfunctionからDeclBlockに与え、DeclBlock内で判定する。 
       （DeclBlockで<returnのトークン,その型>のリストをreturn文の分だけ保存しfunctionに返せばできなくなさそうだが、DeclBlockでの処理とfunctionでの処理両方が複雑になるためこの案は棄却）  
       ```
       int a;
       void funcA();
       int funcB();
       func void funcA(){
       a = call funcB() + 1;
       }
       func int funcB(){
       int b;
       return b;
       }
       ```
 - [x] void型以外の関数で，return がない場合のチェック  
       `int funcA(); func int funcA(){}`  

