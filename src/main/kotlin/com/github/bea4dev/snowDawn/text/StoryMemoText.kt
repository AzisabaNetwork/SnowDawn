package com.github.bea4dev.snowDawn.text

import com.github.bea4dev.snowDawn.save.ServerData
import com.github.bea4dev.snowDawn.world.WorldRegistry
import net.kyori.adventure.key.Key
import net.kyori.adventure.text.Component
import net.kyori.adventure.translation.GlobalTranslator
import net.kyori.adventure.translation.TranslationRegistry
import org.bukkit.World
import java.text.MessageFormat
import java.util.Locale

object StoryMemoText {
    private val STORY_MEMO_TEXT = mapOf(
        WorldRegistry.SNOW_LAND to listOf(
            "メモ\n物資の中に氷を入れておいた。\nこれがあれば農業を行うこともできるだろう。\n氷は松明で溶かすと良いだろう。",
            "EAGLE調査隊記録: No.1\n敵側の攻撃が止んでから、はや10年。\n遂に敵の本拠地である惑星<TR1-e>の調査を開始する。",
            "メモ\nスクラップをクラフトすると肥料の触媒を作ることができる。\nあとはかまどに入れて温めれば合成肥料の完成だ。\n俺とお前だけの秘密のレシピだ。\nぜひ活用してくれ。",
            "EAGLE調査隊記録: No.2\nこの惑星のほとんどは雲に覆われているという話だったが、\n内側は静寂に包まれているようだ。\n絶えず雪が降り続けている。",
            "EAGLE調査隊記録: No.3\n洞窟にはBOTがうろついている。\n単体の攻撃力はさほどでもないが、囲まれると厄介だ。\n飛びかかってきたときに叩き落とすことを勧める。",
            "EAGLE調査隊記録: No.4\nBOTという名称でも今後困らないだろうが、\nせっかくなので名前をつけてみた。\nPhageなんでのはどうだろうか。\n地球由来の形のよく似たウイルスから名前を拝借した。",
            "メモ\n電波塔の建設作業が面倒でしかたない。\n根本に物資を置いておくように言われた。\n誰が利用するのかはわからないが、\nこれを読んでいる君の役に立っていることを切に願う…",
            "EAGLE調査隊記録: No.5\nルーカスに簡易拠点の建設を依頼された。\n建設予定地としてここから1000メートルほど離れた場所を選んだ。\nあの場所なら高台になっていてロケットの発着も可能なはずだ。",
            "メモ\nまた面倒事を押し付けられた。\n連絡時の口調が強かったのも気がかりだ。\n最近の彼は少し様子がおかしいと思う。\n何かあったのだろうか。",
            "メモ\n敵側の攻撃が止んだのも不可解だ。\n敵のワナ…というのは考えすぎだろうか",
            "EAGLE調査隊記録: No.6\n洞窟の調査中に偶然、広大な地下空間を発見した。\n深層岩の層を抜けた先に空間へとつながる通路を発見したのだ。\nこれより調査準備に取り掛かる。",
            "メモ\nこの惑星は相変わらず寒い。\n準備に少し手間取っているが、もう少しで終わるだろう。\nそういえばルーカスは珍しく地下空間への調査を承諾してくれた。\nでも彼は何か言いたげだった。\nそろそろ腹を割って話してくれれば良いのだけれど…",
            "メモ\nPhageの中には合成燃料が入っているようだ。\n石油由来の燃料よりも効率が高い。\n上層部の連中の狙いはこれか…？",
        ),
        WorldRegistry.SECOND_MEGA_STRUCTURE to listOf("Test Title2\naaa\nbbb"),
    )

    init {
        val registry = TranslationRegistry.create(Key.key("snow_dawn", "story"))
        registry.defaultLocale(Locale.JAPAN)
        for ((world, texts) in STORY_MEMO_TEXT) {
            for ((index, text) in texts.withIndex()) {
                for ((lineIndex, line) in text.split("\n").withIndex()) {
                    registry.register("memo:${world.name}:${index}:${lineIndex}", Locale.JAPAN, MessageFormat(line))
                }
            }
        }
        val translator = GlobalTranslator.translator()
        translator.addSource(registry)
    }

    fun getNext(world: World): List<Component>? {
        val index = ServerData.storyTextIndex.computeIfAbsent(world.name) { 0 }

        val storyMemoText = STORY_MEMO_TEXT[world] ?: return null

        if (index >= storyMemoText.size) {
            return null
        }

        ServerData.storyTextIndex[world.name] = index + 1

        val lines = storyMemoText[index]
            .split("\n")
            .indices
            .map { line -> "memo:${world.name}:${index}:${line}" }
            .map { key -> Component.translatable(key) }

        return lines
    }
}