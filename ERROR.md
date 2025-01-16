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
エラーの分類基準が一貫していればよい。こだわりだすとずっとこだわれる部分なので、実装のやりやすい範囲で作ればよい  

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
 - [x] semanticCheck(): 左辺の型["+lts+"]と右辺の型["+rts+"]が異なります  
        → 変なアドレスに書き込むようになっているといけないのでコード生成しない
 - [x] semanticCheck(): 定数には代入できません

### statementInput:
 - [x] 🍀 parse(): inputの後ろはprimaryです  
        → 次の;まで飛ばす（primaryが不定）  
        ` input ; `  
 - [x] 💫 parse(): ;がありません  
        → primaryの解析後なので、;を補う  
        ` input i_a `  
 - [x] semanticCheck(): 定数には代入できません  
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
 - [x] semanticCheck(): 左辺の型[" + lts + "]と右辺の型[" + rts + "]は足せません

### expressionSub:
 - [x] 🍀 parse(): -の後ろはtermです  
        → 回復エラーだけ出して処理はstatementAssign/Input/Output/Blockに任せる  
        ` i_a = 7 - ; `
 - [x] semanticCheck(): 左辺の型[" + lts + "]から右辺の型[" + rts + "]は引けません

### termMult:
 - [x] 🍀 parse(): *の後ろはfactorです  
        → 回復エラーだけ出して処理はstatementAssign/Input/Output/Blockに任せる  
        ` i_a = 7 * ; `
 - [x] semanticCheck(): 左辺の型[" + lts + "]と右辺の型[" + rts + "]は掛けられません

### termDiv:
 - [x] 🍀 parse(): /の後ろはfactorです  
        → 回復エラーだけ出して処理はstatementAssign/Input/Output/Blockに任せる  
        ` i_a = 7 / ; // 単体/の後ろが数式(+,-,(,数字)以外の場合は字句解析で/=ILLとなる `  
        ↑ なのでこのエラーが出ることはない
 - [x] semanticCheck(): 左辺の型[" + lts + "]は右辺の型[" + rts + "]で割れません

### plusFactor:
 - [x] 🍀 parse(): +の後ろはunsignedFactorです  
        → 回復エラーだけ出して処理はstatementAssign/Input/Output/Blockに任せる  
        ` i_a = 7 + +; `
 - [x] semanticCheck(): +の後ろはT_intです[" + rts + "]

### minusFactor:
 - [x] 🍀 parse(): -の後ろはunsignedFactorです  
        → 回復エラーだけ出して処理はstatementAssign/Input/Output/Blockに任せる  
        ` i_a = 7 + -; `
 - [x] semanticCheck(): -の後ろはT_intです[" + rts + "]

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
 - [x] semanticCheck(): &の後ろはT_intです["+ts+"]

### primaryMult:
 - [x] 🍀 parse(): *の後ろはvariableです  
        → ~~]まで飛ばす。なければ次の;まで飛ばす~~  
        ~~（*が使われるのはAddressToValueか代入先の変数の前のどちらか。前者なら後ろに;があるはず、後者でも;まで行って一行まるっと飛ばすか、配列に使われていたなら]で止められる）~~  
        → 回復エラーだけ出して処理はstatementAssign/Input/Output/Blockに任せる  
        ` i_a = * ; `
 - [x] semanticCheck(): \*の後ろは[int*]です

### variable:
 - [x] semanticCheck(): 配列変数は T_int_array か T_pint_array です
 - [x] semanticCheck(): 配列型の後ろに[]がありません  
       → 意味解析でのエラーは変更なし 

### array: 
 - [x] 💫 parse(): ]がありません  
        → expression解析後のエラーなので、expressinはそこで終了とみなして]を補う  
        ` ia_a[0 = 0; `
 - [x] 🍀 parse(): [の後ろはexpressionです  
        → expression内でのエラー。expressionが不明となる。]か;まで飛ばす  
          （]はarray自身の範囲内の終了を示す。;は外側の（例えばStatementAssign）の終わりを表す。そこも飛んだら次の行の;を読む。「if(){ i_a[0 = 1 } i_a=0; 」の文だと、次の;まで飛ぶのでifの}を飛ばしてしまうが、if側でどうにかする。間違いまみれならどうしようもないのでまともなコンパイルエラーは諦める）  
        ` ia_a[] = 0; `

### ident:
 - [x] semanticCheck(): 変数名規則に合っていません  
       → ~~意味解析でのエラー。変数の型が不明だと以降の意味解析に支障が出る。~~  
         ~~一時的にint型として、以降で出る意味解析でのエラーは構文木の上の階層の意味解析に任せる。~~  
         今回は構文解析でのエラー仕様を変えるだけなので意味解析でのエラーはfatalErrorのままにする。  

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
 - [x] semanticCheck(): 左辺の型["+lts+"]と右辺の型["+rts+"]が一致しないので比較できません

### conditionLE:
 - [x] 🍀 parse(): <=の後ろはexpressionです  
        ` if(i_a <= )i_a=0; `
 - [x] semanticCheck(): 左辺の型["+lts+"]と右辺の型["+rts+"]が一致しないので比較できません

### conditionGT:
 - [x] 🍀 parse(): >の後ろはexpressionです  
        ` if(i_a > )i_a=0; `
 - [x] semanticCheck(): 左辺の型["+lts+"]と右辺の型["+rts+"]が一致しないので比較できません
 
### conditionGE:
 - [x] 🍀 parse(): >=の後ろはexpressionです  
        ` if(i_a >= )i_a=0; `
 - [x] semanticCheck(): 左辺の型["+lts+"]と右辺の型["+rts+"]が一致しないので比較できません

### conditionEQ:
 - [x] 🍀 parse(): ==の後ろはexpressionです  
        ` if(i_a == )i_a=0; `
 - [x] semanticCheck(): 左辺の型["+lts+"]と右辺の型["+rts+"]が一致しないので比較できません

### conditionNE:
 - [x] 🍀 parse(): !=の後ろはexpressionです  
        ` if(i_a != )i_a=0; `
 - [x] semanticCheck(): 左辺の型["+lts+"]と右辺の型["+rts+"]が一致しないので比較できません

### statementIf:
 - [x] 🍀 parse(): ifの後ろはconditionBlockです  
        → )まで飛ばす →{からstatement →なければ次の;まで飛ばす  
        ` if i_a>0){ i_a=0; } `  
        ` if i_a>0{ i_a=0; } `  
        ` if i_a>0 i_a=0; } //;までconditionBlock判定になる `    
 - [x] 🍀 parse(): conditionBlockの後ろはstatementです  
        → 次の;まで飛ばす  
        ` if(i_a>0); `  
        ` if(i_a>0) i_a=0; } //}だけが余計なもの `  
 - [x] 🍀 parse(): elseの後ろはstatementです  
        → 次の;まで飛ばす  
        ` if(i_a>0){}else; `
 
### statementWhile:
 - [x] parse(): whileの後ろはconditionBlockです  
        → )まで飛ばす →{からstatement →なければ次の;まで飛ばす  
        ` while i_a>0){ i_a=0; } `
 - [x] parse(): conditionBlockの後ろはstatementです  
        → 次の;まで飛ばす  
        ` while(i_a>0); `

### statementBlock:
 - [x] statement内部でエラー  
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
 - [x] semanticCheck(): 左辺の型[" + lts + "]と右辺の型[" + rts + "]はT_boolである必要があります

### termAnd:
 - [x] 🍀 parse(): &&の後ろはconditionFactorです  
        → ConditionBlockで対処するのでここでは回復エラーだけ出して何もしない  
        ` if(i_a < 0 && ) i_a=0; `
 - [x] semanticCheck(): 左辺の型[" + lts + "]と右辺の型[" + rts + "]はT_boolである必要があります

### notFactor:
 - [x] 🍀 parse(): !の後ろはConditionUnsignedFactorです  
        → ConditionBlockで対処するのでここでは回復エラーだけ出して何もしない  
        ` if(! ) i_a=0; `
 - [x] semanticCheck(): !の後ろはT_boolです[" + rts + "]

### conditionUnsignedFactor:
 - [x] 🍀 parse(): [の後ろはconditionExpressionです  
        → ]まで飛ばす →なければ)まで飛ばしてconditionBlockで終わり、保険で;  
        ` if([ < 0]) i_a=0; `
 - [x] 💫 parse(): ]がありません
        → ]を補う  
        ` if([i_a < 0 ) i_a=0; `

