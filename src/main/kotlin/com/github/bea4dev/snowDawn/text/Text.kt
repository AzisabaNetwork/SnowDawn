package com.github.bea4dev.snowDawn.text

import net.kyori.adventure.key.Key
import net.kyori.adventure.translation.GlobalTranslator
import net.kyori.adventure.translation.TranslationRegistry
import org.bukkit.entity.Player
import java.text.MessageFormat
import java.util.Locale

enum class Text(val jp: String) {
    CLOSE("閉じる"),
    BACK("戻る"),

    CRAFT_UI("クラフト"),
    CRAFT_UI_CLICK_TO_OPEN("クリックで開く"),
    CRAFT_REQUIRED("> 必要なアイテム"),
    CANNOT_CRAFT("材料が足りません"),

    ITEM_SCRAP("スクラップ"),
    ITEM_SCRAP_LORE_0("何らかの残骸、クラフトに使用する。"),

    ITEM_SCRAP_PIPE("スクラップのパイプ"),
    ITEM_SCRAP_PIPE_LORE_0("スクラップの寄せ集めでできたパイプ。"),
    ITEM_SCRAP_PIPE_LORE_1("敵の攻撃に合わせてクリックすると、"),
    ITEM_SCRAP_PIPE_LORE_2("相手の攻撃を弾くことができる。"),
    ITEM_SCRAP_PIPE_LORE_3(""),
    ITEM_SCRAP_PIPE_LORE_4("パリィ！"),

    ITEM_SCRAP_PICKAXE("スクラップのピッケル"),
    ITEM_STONE_PICKAXE("石のピッケル"),
    ITEM_FURNACE("かまど"),

    ITEM_ICE("氷"),
    ITEM_COAL("石炭"),
    ITEM_TORCH("松明"),
    ITEM_STONE("丸石"),
    ITEM_COPPER_INGOT("銅のインゴット"),

    ITEM_COPPER_HELMET("銅のヘルメット"),
    ITEM_COPPER_CHEST_PLATE("銅のチェストプレート"),
    ITEM_COPPER_LEGGINGS("銅のレギンス"),
    ITEM_COPPER_BOOTS("銅のブーツ"),

    MESSAGE_SET_RESPAWN("リスポーン地点を設定しました"),

    LUCAS("司令官ルーカス"),
    BENE("ベネ"),
    TUTORIAL_1("21XX年――\n人類は異星文明との全面戦争に突入した。"),
    TUTORIAL_2("敵の猛攻に圧倒され敗北は秒読み……\n\nしかし、不可解にも攻撃は唐突に止んだ。"),
    TUTORIAL_3("聞こえるか、%0。\n私はこの作戦を指揮するルーカスだ。\n"),
    TUTORIAL_4("周知のとおり、\n昨年を境に敵は一切の攻撃をやめた。\n君には敵の母星に当たる\n地球型惑星<TR1-e>の調査を命じる。"),
    TUTORIAL_5("油断はするな。\n攻撃が止んだ今も、連中の真意は不明だ。\n二度とあの悪夢を繰り返すわけにはいかない……\n\n"),
    TUTORIAL_6("──目的は惑星の現況確認だ。\n\n"),
    TUTORIAL_7("地表には我々が設営した調査施設があるが、\n連絡は途絶えたまま。\n内部を調べ、記録媒体を回収せよ。"),
    TUTORIAL_8("健闘を祈る。\n\n"),
    TUTORIAL_9("……待て。\n\n"),
    TUTORIAL_10("突入カプセルの電磁波センサーが\n異常値を検知している。"),
    TUTORIAL_11("機体はガタが来ていても、\n生命線たる計測器だけは万全のはずだ。"),
    TUTORIAL_12("それが一系統どころか\n全系統で異常を示している……。"),
    TUTORIAL_13("嫌な予感がする――。\n\n"),
    TUTORIAL_14("……目を覚ましたようですね。\n\n"),
    TUTORIAL_15("不時着したようですが……大丈夫ですか？\n\n"),
    TUTORIAL_16("遅ればせながら自己紹介を。\n私は ベネ といいます。\n大きな衝撃音を聞いて様子を見に来ました。"),
    TUTORIAL_17("あなたは調査施設を探しているのでしょう？\n残念ながら、\nここからかなり離れた場所にあります。"),
    TUTORIAL_18("すぐ出発したいところですが、\nこの惑星の気候は苛烈です。\nまずは装備を整えましょう。\nこれを――"),
    TUTORIAL_19("付近の残骸の中から見つけた\n緊急用サバイバルキットです。\n体を温める手段も入っています。\nひと息つきましょう。"),
    TUTORIAL_20("火のそばで温まる必要があります。\nキャンプファイアを設置してみてください。"),
    TUTORIAL_21("キャンプファイアを右クリックすることで、\nリスポーン地点を固定することができます。"),
    TUTORIAL_22("それから、これをどうぞ。\n\n"),
    TUTORIAL_23("スクラップをクラフトして\n即席の道具を作りましょう。\n"),
    TUTORIAL_24("クラフトはインベントリから行えます\n\n"),
    TUTORIAL_25("装備を整えるためにも、\n周辺を探索しませんか？\n"),

    SISETU_0("%0さん、この裏に書き置きを見つけました！\nおそらく、先に行った調査隊のものでしょう\n"),
    SISETU_1("「我々を探している捜索者へ。\n我々は岩盤層を超えて地下空間へと向かう。\nもはや彼の言うことは信用ならない。」"),
    SISETU_2("はて……「彼」とは誰のことでしょうか\n\n"),
    SISETU_3("ひとまず、あなたの上官と連絡を取ったほうが\n良いのではないですか？\n"),
    SISETU_4("このアンテナを利用するとしましょう。\n\n"),
    SISETU_5("単に電源が落ちているだけのようです。\nスイッチを入れてみましょう\n"),
    SISETU_6("...か....き.....、\n\n"),
    SISETU_7("......こえるか...、\n\n"),
    SISETU_8("..こえるなら応答してくれ\n\n"),
    SISETU_9("おお、その声は%0か！\n良かった…無事だったか。\n指揮官ルーカスだ。"),
    SISETU_10("こんにちは、私はベネといいます。\n%0さんを現地でサポートしています\n"),
    SISETU_11("ほう、協力者か。\n心強いな、よろしく頼むよベネ君。\n"),
    SISETU_12("早速だが、施設の様子はどうだろうか。\n\n"),
    SISETU_13("施設はもぬけの殻です。\nただ、書き置きが残されていました。\n集団は地下空間へ向かったとのこと。"),
    SISETU_14("そうか――。\n\n"),
    SISETU_15("%0に次の任務を命ずる。\n\n\n今すぐ帰還に向けて準備せよ――。\n"),
    SISETU_16("ちょっと待ってください！\n\n"),
    SISETU_17("地下へ向かった調査隊のことは\nどうするおつもりですか？\n"),
    SISETU_18("調査隊のことは忘れろ！\nこれは命令だ。\n背くことは許されない。"),
    SISETU_19("話は以上だ。\n通信を終了する。\n"),
    SISETU_20(".............\n\n"),
    SISETU_21("地下空間は非常に危険です。\n連絡が途絶えているとあれば何か身に\n危険が及んでいてもおかしくありません。"),
    SISETU_22("地下空間へ向かうべきです。\n\n命令を無視してでも――"),
    ;

    companion object {
        init {
            val registry = TranslationRegistry.create(Key.key("snow_dawn", "global"))
            registry.defaultLocale(Locale.JAPAN)
            for (text in entries) {
                registry.register(text.toString(), Locale.JAPAN, MessageFormat(text.jp))
            }
            val translator = GlobalTranslator.translator()
            translator.addSource(registry)
        }
    }

    operator fun get(player: Player): String {
        return this.jp
    }

    operator fun get(player: Player, vararg arguments: String): String {
        var message = this[player]
        for (argument in arguments.iterator().withIndex()) {
            message = message.replace("%${argument.index}", argument.value)
        }
        return message
    }
}