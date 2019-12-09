package com.jxxt.sues

import org.jsoup.Jsoup
import org.jsoup.nodes.Document

class FindContext {
    //html获取个人id URL              by Regex
    fun findText(a: String): String {
        val regex = Regex("""<iframe src="(.*)ignoreHead=1""")
        val result = regex.find(a) ?: return "no"
        return "http://jxxt.sues.edu.cn/eams/" + result.groupValues[1].replace("&amp;", "&") + "ignoreHead=1"
    }

    //HTML to Map                by Jsoup
    fun resolveClasses(html: String): MutableList<String>? {
        val doc: Document = Jsoup.parse(html)

        //获取script Elements
        val docEles = doc.getElementsByTag("script")
        for (i in docEles) {
            if (i.toString().contains("new TaskActivity")) {
                val lines = i.toString().replace("\n", "")
                val lineRegexPattern = Regex("""new TaskActivity.*?table""")
                val linesRegex = lineRegexPattern.findAll(lines)
                val list= mutableListOf<String>()
                linesRegex.forEach {
//                    println(it.value)
                    list+=it.value
                }
                return list
            }
        }
        return null
    }
    fun getToyear(html: String): Int {
        val doc: Document = Jsoup.parse(html)

        //获取script Elements
        val docEles = doc.getElementsByTag("script")
        for (i in docEles) {
            if (i.toString().contains("new TaskActivity")) {
                val lines = i.toString().replace("\n", "")
                val lineRegexPattern = Regex("""[0-9]{4}""")
                return lineRegexPattern.find(lines)?.value?.toInt() ?: 0
            }
        }
        return 0
    }
}