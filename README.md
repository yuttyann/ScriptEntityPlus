ScriptEntityPlus [Java8 MC1.9-1.15.2]
==========
概要
--------------------------------------------------
[ScriptBlockPlus](https://dev.bukkit.org/projects/scriptblock)に、エンティティにスクリプトを設定することができる機能を追加するアドオンです。  

導入
-----------
[Releases](https://github.com/yuttyann/ScriptEntityPlus/releases)または[Yuttyann Files](https://file.yuttyann44581.net/)から`ScriptEntityPlus`のダウンロードを行ってください。  
その後前提プラグインである[`ScriptBlockPlus`](https://github.com/yuttyann/ScriptBlockPlus/releases)(v1.9.3以降)をダウンロードを行い`plugins`フォルダへ保存すれば完了です。  

使い方
-----------
基本的にはツールの[**`説明文`**](https://github.com/yuttyann/ScriptEntityPlus/tree/master/src/main/resources/lang)に従ってください。  
プレイヤーから**コマンド**`/sbp tool`を入力し**ツール**`Script Connection`を入手してください。  
対象の指定方法は**ブロックを対象とする場合は左クリック**、**エンティティを対象とする場合は右クリック**です。  

仕組み
-----------
ファイルの管理: `ScriptBlockPlusのスクリプトの種類と座標をエンティティのUUIDを元に保存しているため、`  
`UUIDの変更(例: 額縁のアイテムを変更等)があった場合設定ファイルが残存し続けてしまうので注意してください。`  
  
ファイルの削除: `ツールでの削除またはプレイヤーが意外が死亡した場合に設定ファイルが削除されます。`  
`また、エンティティのスクリプトを削除しても設定元のスクリプトには影響はありません。`  

対応プラットフォーム
-----------
[`ScriptBlockPlus`](https://github.com/yuttyann/ScriptBlockPlus)と**同様**です。  

プラグイン記事
-----------
...  