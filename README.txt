Read me plz.
这是一个关于游戏——Divinity: Original Sin EE的本地化工程。
原文本来自轩辕汉化组，不过我看了一下，不少没翻译，应该是后期没跟上。
一些XML的实体书写也是有问题的，会导致游戏内乱码。
我修正了乱码部分的文本，并提取了缺失及漏翻的文本，有兴趣可以自行获取并补上这些翻译。


【一些必要说明】
代码DESS.java用于对比一个现有汉化文本和最新的英文文本，可以筛选出：
1.现有汉化中忘记翻译的文本
2.英文文本中，新添加的条目
并丢弃已被官方删去的无效条目。

需要自行配置以下四个路径值。
private static String urlCN="./src/data/CNenglish.xml";//已汉化好的文本文件
private static String urlEn="./src/data/english.xml";//未汉化好的文本文件
private static String urlOut="./src/data/out.xml";//输出缺失文本
private static String urlCNOut="./src/data/CNout.xml";//输出已汉化好的文本

*借别人没用的Github挂着，不要在这里联系我。
*我本人不维护文本，只是会偶尔来处理一下pull库请求。
╮(╯▽╰)╭