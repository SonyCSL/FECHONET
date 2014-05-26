###概要

FECHONETは、富士通BSC製スマートコンセント[F-PLUG][]を[ECHONET Lite][]オブジェクトとしてネットワークに参加させるためのAndroidウィジェットです。

###使い方

1. F-PLUGとAndroidをペアリングします。（結構コツがあります。まずPCで試してからAndroidでやろうと思うと、PCでペアリングした時点で他の機器ではペアリングできなくなるので、一旦設定を全消しする必要があります）
2. ペアリングされたAndroid上で、FECHONETをウィジェットとして走らせます。うまくいけば「Service Started」というToastが出ます。

* 以上です。ネットワークからはECHONET Liteの分電盤・温度センサー・照度センサー・湿度センサーが見えるはずです。
* 値は15秒おきに更新されます。センサーは例えば[Kadecot][]を使えば簡単に見ることができます。（Kadecotとウィジェットが走る端末はECHONET Liteのプロトコル上の制約により別でないといけません。ご注意を）
* 複数のF-PLUGを持っていませんが、プログラム的にはペアリングされているF-PLUGが複数ある場合も分電盤は一つであり、各F-PLUGが分電盤の別チャンネルとして見えるように作ったつもりです。その他のセンサーはF-PLUGの数だけ複製されます。
* ECHONET Liteのドライブには[OpenECHO][]を用いています。

###バージョン

* 2014/5/26 Ver.0.1

###ToDo

* 分電盤オブジェクトは、瞬時値しか出ません。累積の値をとりたいのですが、[F-PLUGのメッセージ][]を理解していないため、まだ実装できてません。
* サービスが起動しますが、一度起動すると設定->アプリ->FECHONET->アプリ終了　をしないと止まりません。ウィジェットがなくなった時点で止まるようにしたいです。
* 折角ウィジェットなので、現在の値をウィジェットに表示したいです。
* ポーリング間隔を指定できるようにしたいです。
* その他慌てて作ったため非常に不安定なので改善したいです。

###ライセンス
本ソフトウェアの著作権は[株式会社ソニーコンピュータサイエンス研究所][]が保持しており、[MITライセンス][]で配布されています。ライセンスに従い，自由にご利用ください。

[株式会社ソニーコンピュータサイエンス研究所][]および本プログラムは富士通BSC社とは無関係です。

[F-PLUG]:http://www.bsc.fujitsu.com/services/f-plug/ "F-PLUG"
[ECHONET Lite]: http://www.echonet.gr.jp/ "ECHONET Lite"
[Kadecot]: http://kadecot.net/ "Kadecot"
[OpenECHO]: https://github.com/SonyCSL/OpenECHO "OpenECHO"
[F-PLUGのメッセージ]: http://www.bsc.fujitsu.com/services/f-plug/downloads/message.html "F-PLUGのメッセージ"
[株式会社ソニーコンピュータサイエンス研究所]: http://www.sonycsl.co.jp/ "株式会社ソニーコンピュータサイエンス研究所"
[MITライセンス]: http://opensource.org/licenses/mit-license.php "MITライセンス"
