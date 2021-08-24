ScriptEntityPlus [Java8 MC1.9-1.17.1]
==========
概要
--------------------------------------------------
[ScriptBlockPlus](https://github.com/yuttyann/ScriptBlockPlus)に、エンティティにスクリプトを設定することができる機能を追加するプラグインです。  

導入
-----------
[Releases](https://github.com/yuttyann/ScriptEntityPlus/releases)または[Yuttyann Files](https://file.yuttyann44581.net/)から`ScriptEntityPlus`のダウンロードを行ってください。  
その後前提プラグインである[`ScriptBlockPlus`](https://github.com/yuttyann/ScriptBlockPlus/releases)(最新版を推奨)をダウンロードを行い`plugins`フォルダへ保存すれば完了です。  

使い方
-----------
基本的にはツールの[**`説明文`**](https://github.com/yuttyann/ScriptEntityPlus/tree/master/src/main/resources/lang)に従ってください。  
プレイヤーから**コマンド**`/sbp tool`を入力し**ツール**`Script Connection`を入手してください。  
対象の指定方法は**ブロックを対象とする場合は左クリック**、**エンティティを対象とする場合は右クリック**です。  
――――――――――――――――――――――――――――――――――  
**パーミッション**  
ツール"Script Connection"の使用: `scriptentityplus.tool.scriptconnection`  
――――――――――――――――――――――――――――――――――  
**ツールのモード**  
アップデート`v1.0.2`にて`NORMAL MODE`と`DEATH MODE`が追加されました。  
ツールをメインハンドに所持した状態で左クリック行うことでモードを切り替えることができます。  
  
**`NORMAL MODE`**  
エンティティをクリックした際に実行されるスクリプトを設定することができます。  
  
**`DEATH MODE`**  
エンティティが死亡した際に実行されるスクリプトを設定することができます。 
――――――――――――――――――――――――――――――――――  
**チャットイベント**  
テキストにカーソルを合わせる、クリックを行うことで情報の表示やコマンドの実行をすることができます。  
(所謂[`tellraw`](https://minecraft-ja.gamepedia.com/%E3%82%B3%E3%83%9E%E3%83%B3%E3%83%89/tellraw)です。)  
  
**`スクリプトの選択 [MAINHAND+SHIFT+LEFT_CLICK]`**  
緑色のテキストをクリックすることで、エンティティに設定したいスクリプトを選択することができます。  
![ScriptTypes](https://dl.dropboxusercontent.com/s/jvfmhrvyqvs1g50/ScriptTypes.png)  
  
**`設定されているスクリプトの表示 [OFFHAND+RIGHT_CLICK]`**  
緑色のテキストをクリックすることで、スクリプトを実行するコマンドがチャットに設定されます。  
![Scripts](https://dl.dropboxusercontent.com/s/tyn94f3h5x88ytz/Scripts.png)  
  
**`エンティティの設定 [OFFHAND+SHIFT+RIGHT_CLICK]`**  
橙色の`[...]`で囲まれたテキストをクリックすることで、設定の`有効`、`無効`、`表示`を行うことができます。  
また、水色のテキストにカーソルを合わせることで設定の説明が表示されます。  
![EntitySettings](https://dl.dropboxusercontent.com/s/gpjrhmilz3yxvs0/EntitySettings.png)  
――――――――――――――――――――――――――――――――――  

ファイル関係
-----------
ファイルの管理: `ScriptBlockPlusのスクリプトの種類と座標をエンティティのUUIDを元に保存しているため、`  
`UUIDの変更(例: 額縁のアイテムを変更等)があった場合設定ファイルが残存し続けてしまうので注意してください。`  

ファイルのパス: `設定の保存先は` **`plugins/ScriptBlockPlus/json/entityscript/....`** `です。`  
  
ファイルの削除: `ツールでの削除またはプレイヤー以外が死亡した場合に設定ファイルが削除されます。`  
`また、エンティティのスクリプトを削除しても設定元のスクリプトには影響はありません。`  

対応プラットフォーム
-----------
[`ScriptBlockPlus`](https://github.com/yuttyann/ScriptBlockPlus)と**同様**です。  

プラグイン記事
-----------
**[[Poteton, JP]](https://mcpoteton.com/mcpl-scriptentityplus)**  
**[[SpigotMC, EN]](https://www.spigotmc.org/resources/scriptentityplus.80165/)**  