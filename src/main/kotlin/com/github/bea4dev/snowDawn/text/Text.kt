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

    ITEM_ICE("氷"),
    ITEM_COAL("石炭"),
    ITEM_TORCH("松明"),

    LUCAS("司令官ルーカス"),
    BENE("ベネ"),
    TUTORIAL_1("21XX年――\n人類は異星文明との全面戦争に突入した。"),
    TUTORIAL_2("敵の猛攻に圧倒され敗北は秒読み……\n\nしかし、不可解にも攻撃は唐突に止んだ。"),
    TUTORIAL_3("聞こえるか、%0。\n私はこの作戦を指揮するルーカスだ。\n"),
    TUTORIAL_4("周知のとおり、昨年を境に敵は一切の攻撃をやめた。\n君には敵の母星に当たる地球型惑星《TR1‑e》の調査を命じる。"),
    TUTORIAL_5("油断はするな。攻撃が止んだ今も、連中の真意は不明だ。\n二度とあの悪夢を繰り返すわけにはいかない……。"),
    TUTORIAL_6("──目的は惑星の現況確認だ。\n\n"),
    TUTORIAL_7("地表には我々が設営した調査施設があるが、\n連絡は途絶えたまま。\n内部を調べ、記録媒体を回収せよ。"),
    TUTORIAL_8("健闘を祈る"),
    TUTORIAL_9("……待て。\n\n"),
    TUTORIAL_10("突入カプセルの電磁波センサーが\n異常値を検知している。"),
    TUTORIAL_11("機体はガタが来ていても、\n生命線たる計測器だけは万全のはずだ。"),
    TUTORIAL_12("それが一系統どころか\n全系統で異常を示している……。"),
    TUTORIAL_13("嫌な予感がする――。"),
    TUTORIAL_14("……目を覚ましたようですね。"),
    TUTORIAL_15("不時着したようですが……大丈夫ですか？"),
    TUTORIAL_16("遅ればせながら自己紹介を。\n私は ベネ といいます。\n大きな衝撃音を聞いて様子を見に来ました。"),
    TUTORIAL_17("あなたは調査施設を探しているのでしょう？\n残念ながら、ここからかなり\n離れた場所にあります。"),
    TUTORIAL_18("すぐ出発したいところですが、\nこの惑星の気候は苛烈です。\nまずは装備を整えましょう。\nこれを――"),
    TUTORIAL_19("付近の残骸の中から見つけた\n緊急用サバイバルキットです。\n体を温める手段も入っています。\nひと息つきましょう。"),
    TUTORIAL_20("火のそばで温まる必要があります。\nキャンプファイアを設置してみてください。"),
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
            message = message.replace("%${argument.index}%", argument.value)
        }
        return message
    }
}