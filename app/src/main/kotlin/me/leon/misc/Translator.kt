package me.leon.misc

import java.net.URLEncoder
import me.leon.ext.fromJson
import me.leon.ext.readFromNet
import me.leon.misc.net.linkCheck

object Translator {
    val SUPPORT_LANGUAGE =
        arrayOf(
            "zh-CN",
            "en",
            "ja",
            "ko",
            "fr",
            "de",
            "ru",
            "ar",
            "es",
            "it",
            "hi",
            "th",
            "vi",
            "pl",
            "ro",
            "id",
        )
    private const val URL_SIMPLE_GOOGLE = "%s/api/translate/?engine=google&from=%s&to=%s&text=%s"
    private const val URL_GOOGLE =
        "%s/translate_a/single?client=gtx&dt=t&dj=1&hl=zh-CN&source=input&sl=%s&tl=%s&q=%s"
    private const val URL_GOOGLE_TEST =
        "%s/translate_a/single?client=gtx&dt=t&dj=1&hl=zh-CN&source=input&sl=en&tl=zh-CN&q=hello"
    private const val URL_LINGVA = "https://lingva.ml/api/v1/%s/%s/%s"

    private val googleMirrors =
        listOf(
            "https://translate.google.com",
            "https://translate.googleapis.com",
            "https://translate.amz.wang",
        )
    private val mirrors =
        listOf(
            "https://translate.bus-hit.me",
            "https://simplytranslate.pussthecat.org",
            "https://translate.tiekoetter.com",
            "https://translate.slipfox.xyz",
            "https://translate.catvibers.me",
            "https://t.opnxng.com",
            "https://st.alefvanoon.xyz",
            "https://simplytranslate.leemoon.network"
        )

    private val okServer = mutableSetOf<String>()
    private val googleOkServer = mutableSetOf<String>()

    fun init() {
        mirrors
            .linkCheck(3000)
            .filter { it.second }
            .map { it.first }
            .also {
                okServer.addAll(it)
                println(it)
            }
        googleMirrors
            .map { URL_GOOGLE_TEST.format(it) }
            .linkCheck(5000)
            .filter { it.second }
            .map { it.first.substringBefore("/translate_a") }
            .also {
                googleOkServer.addAll(it)
                println(it)
            }
    }

    /** 3 level lv.1 google lv.2 simple lv.3 lingva */
    fun translate(text: String, src: String = "auto", target: String = "zh-CN"): String =
        if (googleOkServer.isNotEmpty()) {
            google(text, src, target)
        } else if (okServer.isNotEmpty()) {
            simpleGoogle(text, src, target)
        } else {
            lingva(text, src, target)
        }

    fun simpleGoogle(text: String, src: String = "auto", target: String = "zh-CN"): String =
        URL_SIMPLE_GOOGLE.format(okServer.first(), src, target, URLEncoder.encode(text, "utf-8"))
            .readFromNet()
            .fromJson(Map::class.java)["translated-text"]
            .toString()

    fun google(text: String, src: String = "auto", target: String = "zh-CN"): String =
        URL_GOOGLE.format(googleOkServer.first(), src, target, URLEncoder.encode(text, "utf-8"))
            .readFromNet()
            .fromJson(GoogleTranslation::class.java)
            .sentences
            .joinToString("") { it.trans }

    fun lingva(text: String, src: String = "auto", target: String = "zh"): String =
        URL_LINGVA.format(
                src.replace("-CN", ""),
                target.replace("-CN", ""),
                URLEncoder.encode(text, "utf-8")
            )
            .readFromNet()
            .fromJson(Map::class.java)["translation"]
            .toString()

    data class GoogleTranslation(val sentences: List<Sentence>) {
        data class Sentence(val trans: String, val orig: String)
    }
}
