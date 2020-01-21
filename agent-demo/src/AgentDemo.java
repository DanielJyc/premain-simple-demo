import java.lang.instrument.Instrumentation;

/**
 * @description:
 * @author: jiayancheng
 * @email: jiayancheng@foxmail.com
 * @datetime: 2020/1/18 12:36 PM
 * @version: 1.0.0
 */
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
