# Java Agent 之一：入门实例

> 原文地址：https://www.yuque.com/aidt/blog/ggk9wx 

Java Agent有两种方式：

- 最常用方式是premain方式，它属于静态注入，即在Java应用程序启动时，在类加载器对类的字节码进行加载之前对类字节码进行“再改造”来做功能增强（例如实现AOP）。本文章以该方式举例说明。
- 另一种方式是HotSpot独有的attach方式（JDK1.6才出现），它能实现动态注入，即对已经运行的Java应用的类进行字节码增强。

JavaAgent 是运行在 main方法之前的拦截器，它内定的方法名叫 premain ，也就是说先执行 premain 方法然后再执行 main 方法。下面，通过一个简单的实例进行入门。
# java-agent实例
## 创建Java Agent 规范的Jar

- 创建如下结构文件

```
agent-demo
└── src
    ├── AgentDemo.java
    └── META-INF
        └── MANIFEST.MF
```

AgentDemo.java内容为

```java
public class AgentDemo {
    /**
     * 该方法在main方法之前运行，与main方法运行在同一个JVM中
     * 并被同一个System ClassLoader装载
     * 被统一的安全策略(security policy)和上下文(context)管理
     */
    public static void premain(String agentOps, Instrumentation inst) {

        System.out.println("====premain1 execute====");
        System.out.println(agentOps);
    }

    /**
     * 如果不存在 premain(String agentOps, Instrumentation inst)
     * 则会执行 premain(String agentOps)
     */
    public static void premain(String agentOps) {

        System.out.println("====premain2 execute====");
        System.out.println(agentOps);
    }
}
```

MANIFEST.MF内容为：

```
Manifest-Version: 1.0
Premain-Class: AgentDemo
Can-Redefine-Classes: true

```

- 打包premain部分

```shell
cd agent-demo/src

# 编译成.class 文件
javac AgentDemo.java

# 加入META-INF/MANIFEST.MF，打包
jar -cvfm AgentDemo.jar META-INF/MANIFEST.MF AgentDemo.class
```

## 创建main部分

- 创建如下结构文件

```java
agent-main
└── src
    ├── AgentMain.java
    └── META-INF
        └── MANIFEST.MF
```
AgentMain.java文件内容：

```java
public class AgentMain {
    public static void main(String[] args) {
        System.out.println("=======main======");
    }
}
```
MANIFEST.MF 文件内容

```
Manifest-Version: 1.0
Main-Class: AgentMain
Can-Redefine-Classes: true

```

- 打包
```
cd agent-main/src

# 编译成.class 文件
javac AgentMain.java

# 加入META-INF/MANIFEST.MF，打包
jar -cvfm AgentMain.jar META-INF/MANIFEST.MF AgentMain.class
```

## 执行
通过 `-javaagent`  参数来指定我们的Java代理包，值得一说的是 `-javaagent` 这个参数的个数是不限的，如果指定了多个，则会按指定的先后执行，执行完各个 agent 后，才会执行主程序的 main 方法。

- 执行指令：
```shell
java -javaagent:agent-demo/src/AgentDemo.jar=AgentDemo -jar agent-main/src/AgentMain.jar
```

- 执行结果：

可以看到先执行了premain函数，再执行main方法。
```
objc[6292]: Class JavaLaunchHelper is implemented in both /Users/daniel/.jenv/versions/1.8.0.131/bin/java (0x10280d4c0) and /Library/Java/JavaVirtualMachines/jdk1.8.0_131.jdk/Contents/Home/jre/lib/libinstrument.dylib (0x1028ff4e0). One of the two will be used. Which one is undefined.
====premain1 execute====
AgentDemo
=======main======
```

> 备注：还有一种在main方法执行后再执行代理的方法，而且主程序需要配置 Agent-Class，所以不常用，如果需要自行了解下 agentmain(String agentArgs, Instrumentation inst) 方法。


# 实现代码
详见：[https://github.com/DanielJyc/premain-simple-demo](https://github.com/DanielJyc/premain-simple-demo)
# 附录
打包jar命令参数

```shell
# jar参数
-c  创建一个jar包
-t  显示jar中的内容列表
-x  解压jar包
-u  添加文件到jar包中
-f  指定jar包的文件名
-v  输出详细报告
-m  指定MANIFEST.MF文件
-0  生成jar包时不压缩内容
-M  不生成清单文件MANIFEST.MF
-i  为指定的jar文件创建索引文件
-C  可在相应的目录下执行命令
```

# 参考

- Java 调试工具、热部署、JVM 监控工具都用到了它[https://cloud.tencent.com/developer/article/1513124](https://cloud.tencent.com/developer/article/1513124)
- Java agents, Javassist and Byte Buddy  [https://ivanyu.me/blog/2017/11/04/java-agents-javassist-and-byte-buddy/](https://ivanyu.me/blog/2017/11/04/java-agents-javassist-and-byte-buddy/)
- agent+动态注入系列： [https://www.cnblogs.com/duanxz/tag/%E6%8F%92%E5%BA%84/](https://www.cnblogs.com/duanxz/tag/%E6%8F%92%E5%BA%84/)
- Javassist 使用指南系列：[https://www.jianshu.com/p/43424242846b](https://www.jianshu.com/p/43424242846b)

