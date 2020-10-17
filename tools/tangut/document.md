初稿: 2020-10-16
小松弘幸 ([@komatsuh](https://twitter.com/komatsuh))

# 記事の内容

*   シュトヘルという漫画がとてもよいです
*   西夏文字をコンピューター上で扱う方法の紹介します
*   西夏文字の簡易辞書を作成します
*   シュトヘルの登場人物を西夏文字で表現します
    +   ユルール 𘅝𘚻 (U+1815D U+186BB) - 祝福 (慶喜)
    +   ハラバル 𗱈𗰞 (U+17C48 U+17C1E) - 黒虎
    +   シュトヘル 𘄅𗾢 (U+18105 U+17FA2) - 雀子

![順に ユルール　ハラバル　シュトヘル](https://storage.googleapis.com/zenn-user-upload/t1is1i5kzosaecbybxi9sdszkkae) 
*左から順に ユルール　ハラバル　シュトヘル*


## はじめに

この文書に登場する西夏文字を正しく表示するためには、おそらくフォントのインストールが必要です。下記の GitHub などからダウンロードとインストールができます。


*   [Noto fonts: NotoSerifTangut](https://github.com/googlefonts/noto-fonts/blob/master/hinted/ttf/NotoSerifTangut/NotoSerifTangut-Regular.ttf) (GitHub)

-----

# シュトヘルと西夏文字

シュトヘルという漫画を読んでとても好きになりました。文字の強さの希望の話です。ぜひ読んでいただきたいです。


*   [伊藤悠「シュトヘル」 | ビッグコミックBROS.NET（ビッグコミックブロス）｜小学館](https://bigcomicbros.net/work/6234/)

シュトヘルでは西夏文字という文字が物語の重要な要素として登場します。

![シュトヘル (1) 69 ページより](https://storage.googleapis.com/zenn-user-upload/pugwg67o7a4kjchkks4yfkotrl4g)
*シュトヘル (1) 69 ページより*

西夏文字とは、西夏王朝 (1032-1227) の時代に制定された文字で、漢字に近い見た目をしています。西夏王朝はモンゴルのチンギス・ハーンに滅ぼされますが、西夏文字はその後も 300 年近くは使われていたようです。

![西夏文字 - Wikipedia](https://upload.wikimedia.org/wikipedia/commons/thumb/9/90/Bushell%27s_1896_decipherment_of_Tangut_characters.jpg/220px-Bushell%27s_1896_decipherment_of_Tangut_characters.jpg)
*[西夏文字 - Wikipedia](https://ja.wikipedia.org/wiki/%E8%A5%BF%E5%A4%8F%E6%96%87%E5%AD%97) より*


## シュトヘルの登場人物の名前をコンピューター上で書きたい

シュトヘルでは、仲間の名前を文字に記すという場面がとても印象深く描かれてます。尊いです。

尊いが過ぎるので、自分でも彼らの名前を書きたくなりました。そこで、彼らの名前をコンピューター上で西夏文字として表現できるようにします。


### やりたいこと

実際にやりたいこと


*   シュトヘル、ユルール、ハラバルの名前を、西夏文字でコンピューター上に書きたい。

そのために必要なこと


*   西夏文字をコンピューター上に書けるようにする
*   シュトヘル、ユルール、ハラバルの西夏文字を画像として得る
*   画像としてある西夏文字から、対応する Unicode のコードポイントを割り出したい。


### 西夏文字をコンピューター上に書く (Unicode とフォント)

幸いにして、Unicode には既に西夏文字用のコードポイントが割り当てられています。そのため、西夏文字をコンピューター上で扱うのに大きな問題はなさそうです。


*   [西夏文字 - Wikipedia](https://ja.wikipedia.org/wiki/%E8%A5%BF%E5%A4%8F%E6%96%87%E5%AD%97#%E9%9B%BB%E5%AD%90%E5%8C%96)

しかし、ほとんどのプラットフォームでは西夏文字用のフォントはデフォルトではインストールされていません。Noto fonts 等の西夏文字用のフォントをインストールする必要があります。


*   [Noto fonts: NotoSerifTangut](https://github.com/googlefonts/noto-fonts/tree/master/hinted/ttf/NotoSerifTangut) (GitHub)
*   [Tangut Yinchuan](https://www.babelstone.co.uk/Fonts/Yinchuan.html) (BabelStone)
*   [Tangut Wenhai](https://www.babelstone.co.uk/Fonts/Wenhai.html) (BabelStone)

西夏文字用の IME があることも確認しましたが、今回はコードポイントの直打ちまたはコピーアンドペーストをすることにします。


*   [西夏文 | 字體和输入法 | 古今文字集成](http://ccamc.org/fonts_tangut.php) (西夏文字用 IME)


### シュトヘルの登場人物の名前を西夏文字の画像として得る

ユルール (祝福) の西夏文字は作品中に登場します。ハラバル (黒虎) とシュトヘル (雀子) の西夏文字は単行本の内表紙を参考にしました。

![ユルールの西夏文字 - シュトヘル (14) 146 ページより](https://storage.googleapis.com/zenn-user-upload/93n1i53ggh3eu82jhkqlx7t9zsvd)
*ユルールの西夏文字 - シュトヘル (14) 146 ページより*


### 西夏文字の画像から、対応する Unicode を割り出す

西夏文字は約6,000文字です。ひとつずつ目で確認できなくもない量ではありますが、もうちょっと楽しい方法を探ります。そのために少し情報を収集しました。

-----

# 西夏文字について調べる
より楽しくそしてできれば効率的に調べれるように、西夏文字についての言語学的な知識と、コンピューターで処理するための既存のデータやツールについて調べます。

## 西夏文字について言語学的に知る

西夏文字についていろいろ知っている方が探しやすいだろうと、下記を含むいくつかの文書を読みました。


*   [西夏文字 - Wikipedia](https://ja.wikipedia.org/wiki/%E8%A5%BF%E5%A4%8F%E6%96%87%E5%AD%97)
*   [文字が語りかける民族意識：カラホトと西夏文字 | 貴重書で綴るシルクロード](http://dsr.nii.ac.jp/rarebook/08/)
*   [高世2007.10「西夏文字に親しむ」](https://www.teikokushoin.co.jp/journals/history_world/pdf/200710/history_world200710-14-16.pdf)
*   [世界の文字 - 西夏文字](https://www.chikyukotobamura.org/muse/wr_easia_4.html)
*   [西夏文字の世界](http://kodaimoji.her.jp/pdf/deai3.pdf)
*   [西夏史: むとうすブログ](http://samayoi-bito.cocolog-nifty.com/mutous/cat5606999/index.html)



今回、直接的に役立った内容は以下の 4 点です。

*   西夏文字を指し示すために、夏漢字典番号が広く使われている
*   西夏文字にも部首がある (750種類以上)
*   名詞+形容詞 という語順になる
*   西夏文字は英語で Tangut script

特に、夏漢字典番号は重要でした。夏漢字典とは、その名の通り西夏文字と漢字間の字典です。その字典で使われている西夏文字の分類番号が、西夏文字に関する文献では広く使われています。


## 西夏文字について情報処理的に知る

西夏文字に関するデータベースやツールなどを探しました。

*   古今文字集成 - 西夏文索引
    *   [西夏文索引](http://www.ccamc.org/tangut_index.php)
*   BabelStone
    *   [BabelStone : Tangut Script](https://www.babelstone.co.uk/Tangut/)
*   Unicode.org
    *   [The Unicode Standard, Version 13.0](https://www.unicode.org/charts/PDF/U17000.pdf)

### 古今文字集成

古今文字集成というサイトの西夏文索引はとてもよく出来ていました。Unicode のコードポイントや夏漢字典番号から文字を検索すると、意味や発音・出典などを表示してくれます。中国語のみのサイトですが、漢字からだいたいの意味が想像できます。

意味から文字を逆引きする方法も欲しかったのですが、見つけられませんでした。また古今文字集成が使っているデータがダウンロードできる形で提供されていないかと探しましたが分かりませんでした。

*   [西夏文索引](http://www.ccamc.org/tangut_index.php)

![西夏文索引](https://storage.googleapis.com/zenn-user-upload/u6oa52xwq40k3e8cj735ieojtvgk)

### BabelStone

BabelStone には、西夏文字の文献の書き起こしやフォントなどのデータがまとまっています。今回は特に、英語での意味と夏漢字典番号の対応表が、役に立ちました。

*   [BabelStone : Tangut Script](https://www.babelstone.co.uk/Tangut/)
*   [English Index to Li Fanwen's 2008 Tangut Dictionary](https://www.babelstone.co.uk/Tangut/XHZD_EnglishIndex.html) (西夏英語辞書)
*   [English definitions in Xia-Han Zidian](https://www.babelstone.co.uk/Tangut/XHZD_2008_Definitions.txt) (西夏英語辞書データ)

![English Index to Li Fanwen's 2008 Tangut Dictionary](https://storage.googleapis.com/zenn-user-upload/nr3pri3z13q40rdg3rdruq0xlxto)

### Unicode.org

Unicode.org のデータ (TangutSources.txt) から、Unicode のコードポイントと夏漢字典番号・部首・画数との対応表が得られます。

*   [The Unicode Standard, Version 13.0](https://www.unicode.org/charts/PDF/U17000.pdf) [PDF] (西夏文字一覧 U+17000〜)
*   [https://www.unicode.org/Public/UCD/latest/ucd/TangutSources.txt](https://www.unicode.org/Public/UCD/latest/ucd/TangutSources.txt) (西夏文字データ)
*   [Tangut Components - Wikipedia](https://en.wikipedia.org/wiki/Tangut_Components) (西夏文字の部首 U+18800〜)

![The Unicode Standard, Version 13.0](https://storage.googleapis.com/zenn-user-upload/yczss8knuzzjyetvmtglm2tbgdr3)


## 西夏文字簡易辞書を作る

Unicode.org のデータと BabelStone の英語辞書を組み合わせれば、英語の意味から Unicode のコードポイントを調べるためのデータが作成できます。

Python スクリプトで作った結果を Google Sheets 上におきました。 

*   [西夏文字 (Tangut script) - Google スプレッドシート](https://docs.google.com/spreadsheets/d/1fhmokw6xTFkEWPtlIiejQTFWO4v0WYE4EkOLl8drd8E/edit#gid=0)

この Google Sheets を使って、意味や部首・画数などから文字を探せるようになりました。

![西夏文字 (Tangut script) - Google スプレッドシート](https://storage.googleapis.com/zenn-user-upload/5ciujjrfyaeqyvnz0ieg8xihdvll)

-----

# 君の名を

材料は揃いましたので後は調べるだけです。


## ユルール 𘅝𘚻 (U+1815D U+186BB) - 祝福 (慶喜)

![ユルール 𘅝𘚻](https://storage.googleapis.com/zenn-user-upload/3rtd4c6jwzivgz1tq5lgmezw3bpp)

|文字|Unicode|部首|画数|夏漢字典|意味|
|----|-------|----|----|--------|----|
|𘅝|U+1815D|𘤻(316)|13|0305|to celebrate|
|𘚻|U+186BB|𘩘(601)|16|4630|happy|


ユルールの名前は、実際には上記の簡易辞書を作る前に、約 6,000 文字を全部目で追って見つけました。Unicode 上では部首順画数順で並んでいるので、数百文字を探すだけで見つかります。

ただ、全部を目で見る作業を繰り返すのは楽しくないなということで、簡易辞書を作るきっかけになりました。

参考: [シュトヘル 2: むとうすブログ](http://samayoi-bito.cocolog-nifty.com/mutous/2009/11/2-8d0f.html)


## ハラバル 𗱈𗰞 (U+17C48 U+17C1E) - 黒虎

![ハラバル 𗱈𗰞](https://storage.googleapis.com/zenn-user-upload/fy5h02dtgfqvo5av5pp5kdaax2f9)

|文字|Unicode|部首|画数|夏漢字典|意味|
|----|-------|----|----|--------|----|
|𗱈|U+17C48|𘢷(184)|13|1477|tiger|
|𗰞|U+17C1E|𘢰(177)|11|0176|dark|


西夏語は 名詞+形容詞 なので、虎黒という語順のようです。一文字目は「tiger」から素直に見つかりました。二文字目は「黒」から「dark」という言葉が想像できなくて、部首と画数から探しました。見つけた上で「dark」という意味をみて、あぁ〜となりました。

参考: [シュトヘル 3: むとうすブログ](http://samayoi-bito.cocolog-nifty.com/mutous/2010/07/3-ec0e.html)


## シュトヘル(ウィソ) 𘄅𗾢 (U+18105 U+17FA2) - 雀子

![ウィソ 𘄅𗾢](https://storage.googleapis.com/zenn-user-upload/khhacy6ckvgmfau7r6y28s5p6zj9)

|文字|Unicode|部首|画数|夏漢字典|意味|
|----|-------|----|----|--------|----|
|𘄅|U+18105|𘤳(308)|13|0221|child|
|𗾢|U+17FA2|𘤊(267)|11|2675|bird|

シュトヘルの本名、ウィソ(雀子) です。ハラバル(黒虎) と同じく子雀という語順です。二文字目の左側は上から「ノメメ」になっています。これは西夏文字の獣偏だそうです。「ノノメ」だと人偏だそうです。


## シュトヘル 𘘌𗢛 (U+1860C U+1789B) - 悪霊 (餓鬼)

![シュトヘル 𘘌𗢛](https://storage.googleapis.com/zenn-user-upload/4otqtjvacmv6wap94ebf636oywt5)

|文字|Unicode|部首|画数|夏漢字典|意味|
|----|-------|----|----|--------|----|
|𘘌|U+1860C|𘨲(563)|12|0621|skull (of a dead person)|
|𗢛|U+1789B|𘢉(138)|11|3003|ghost|


明言はされていませんが、作品中に登場する西夏文字のうち、シュトヘル (悪霊) のことを指していると想像したものです。どこに登場するかは探してみてください。


# 終わりに

シュトヘルの連載は 2009 年に始まり 2017 年に終わりました。西夏文字が Unicode に追加されたのは 2016 年です。シュトヘルやユルール達が望んだ、文字が未来へと紡がれていく世の中が、連載期間中に一歩進んでいたのだなと思うと感慨深いものがあります。

先人たちの偉大な貢献に改めて感謝しています。

-----

一般公開前に、次の方々からフィードバックをいただきました。ありがとうございました。

[@call_cc](https://twitter.com/call_cc), [@miuraken](https://twitter.com/miuraken) 