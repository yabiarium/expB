vAriable_1
[128]
// 識別子がうまく切り出せるか、１文字記号の字句が消えていないか、確認

abc+-def[ghij+*k]lmn
// abc + - def [ ghij + * k ] lmn     のはず。 + あたりが消えてなくなっていないか？

ab0c+-1def[gh2ij+*k3]4lm5n
// ab0c + - 1 def [ gh2ij + * k3 ] 4 lm5n　　　のはず。
// + ] が消えてなくなっていないか？　lm5n の l が消えてなくなっていないか？

_ia
_0a_1_aaa
// ident の先頭は，英字or'_' ，2文字目以降は，英数字or'_'

i_a
&i_b
ip_d
*ip_e
ia_f[i_a]
&ia_f[20]
ipa_g[*ip_d]
*ipa_g[ia_f[i_a]]
c_h
// 変数表記： '_' が英字の扱いを受けているか？