# Json2Java(全网最简版)
json字符串转Java对象,生成对应文件
# 描述&特点 
简易的Json转Java工具,满足基本日常使用(特殊需求可自行增添,代码就一页)
在网上找了好些个这类工具,不是只暴露iead插件就是复杂&没文档,于是自己写了个全网最简版
# 用法
```
public class initial {

    public static void main(String args[]) throws IOException {
        String jsonString="";
        Json2Java j2j = new Json2Java();
        j2j.fileDictoryPath="D:\\";//配置路径
        j2j.classNameStr="HelloWorld";//配置类名
        String javaContext = j2j.parse(jsonString);
    }
}

```
# 进阶
当遇到有多个api接口需要生成对象时,只需要一个循环,
```
for(String url:apis){
        String jsonStr = remoteService.get(url);
        Json2Java j2j = new Json2Java();
        j2j.classNameStr=jsonStr.substring(10);//举个栗子,视情况而定
        String javaContext = j2j.parse(jsonString);
}
```
# 此外
后续将增加json转SQL(Java bean 一键生成mapper sql文件)
