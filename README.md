# SUESjxxt

上海工程技术大学教学系统课程表转日程表

代码位于/app

release下载地址[蓝奏网盘](https://www.lanzous.com/b0d6v7eb) 密码:sues

--------
目前处于测试阶段 部分机型可能闪退 建议使用bugly版本帮助测试 感谢!
感谢GammaPi大佬的[S2C工具](https://github.com/GammaPi/SUES-S2C-Tool)帮助，我将其python代码重构为了kotlin代码并做了移动端界面优化

![image](https://github.com/zsqw123/SUESjxxt/blob/master/app/shot/1.png)

## 支持功能

### 课程表转为日程表

***注意！！！***
**需要连接学校专用网/(网康VPN)才可使用 
导入完成后可离线使用**

    日程表根据每日日程排序 自动跳转到当日(准确的说是目前)日程

### 桌面窗口小部件

    可以在桌面添加小部件 显示效果和应用主界面一致
    不可自定义颜色 且最多只显示接下来的15个课程

![image](https://github.com/zsqw123/SUESjxxt/blob/master/app/shot/4.png)

### 支持导出ics（iCalendar格式）

    文件储存在/Android/data/com.suesjxxt/suesjxxt/1.ics
    导出完成自动弹出分享到其他设备

### 设置界面

![image](https://github.com/zsqw123/SUESjxxt/blob/master/app/shot/2.png)

### 个性化设置

    最新版本可以自定义主题颜色
![image](https://github.com/zsqw123/SUESjxxt/blob/master/app/shot/3.png)

### Dependency

- [anko](https://github.com/Kotlin/anko)
- [Jsoup](https://github.com/jhy/jsoup)
- [Tencent bugly](https://bugly.qq.com)
- [ical4j](https://github.com/ical4j/ical4j)
- [junit](https://github.com/junit-team/junit4)
- [requests](https://github.com/hsiafan/requests)
- [PermissionsDispatcher](https://github.com/permissions-dispatcher/PermissionsDispatcher)

作者Q:3206279058

隐私政策：[隐私政策](http://htmlpreview.github.io/?https://github.com/zsqw123/SUESjxxt/blob/master/Privacy.html)