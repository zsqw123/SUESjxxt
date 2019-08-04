package com.jxxt.sues

import org.jsoup.Jsoup
import org.jsoup.nodes.Document

import java.lang.StringBuilder

class FindContext {
    //html获取id URL              by 正则
    fun findText(a: String): String {
        val regex = Regex("""<iframe src="(.*)ignoreHead=1""")
        val result = regex.find(a) ?: return "no"
        return "http://jxxt.sues.edu.cn/eams/" + result.groupValues[1].replace("&amp;", "&") + "ignoreHead=1"
    }

    //HTML to Map                by Jsoup
    fun resolveClasses(html: String): MutableMap<String, String> {
        val doc: Document = Jsoup.parse(html)
        val map: MutableMap<String, String> = mutableMapOf("99" to "test")
        val doc1 = doc.select("td")
        for (i in doc1) {
            val id = i.attr("id").replace("TD", "").replace("_0", "")
            if (i.attr("title") == ""){
                continue
            }
            val contains = i.attr("title")
            map[id]=contains
        }
        map.remove("99")
        map.remove("")
        return map
    }

}
