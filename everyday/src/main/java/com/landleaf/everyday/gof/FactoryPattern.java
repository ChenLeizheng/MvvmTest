package com.landleaf.everyday.gof;

import com.landleaf.everyday.gof.factory.IRuleConfigParser;
import com.landleaf.everyday.gof.factory.IRuleConfigParserFactory;
import com.landleaf.everyday.gof.factory.JsonRuleConfigParser;
import com.landleaf.everyday.gof.factory.JsonRuleConfigParserFactory;
import com.landleaf.everyday.gof.factory.RuleConfigParserFactoryMap;
import com.landleaf.everyday.gof.factory.SimpleFactory;
import com.landleaf.everyday.gof.factory.XmlRuleConfigParser;
import com.landleaf.everyday.gof.factory.XmlRuleConfigParserFactory;

/**
 * 工厂设计模式
 * //我们根据配置文件的后缀（json、xml、yaml、properties），选择不同的解析器（JsonRuleConfigParser、XmlRuleConfigParser……），将存储在文件中的配置解析成内存对象 RuleConfig。
 */
public class FactoryPattern {

    //简单工厂  提供一个创建对象实例的功能，而无需关心其具体实现。被创建的实例类型可以是接口、抽象类、具体实现类
    private void test(){
        IRuleConfigParser xml = SimpleFactory.createParser("XML");
        xml.parser("");
    }
    //工厂方法 定义一个用于创建对象的接口，让子类决定实例化哪一个类，Factory Method使一个类的实例化延迟到其子类。
    private void test2(String type){
        IRuleConfigParserFactory factory = null;
        if ("json".equalsIgnoreCase(type)) {
            factory = new JsonRuleConfigParserFactory();
        } else if ("xml".equalsIgnoreCase(type)) {
            factory = new XmlRuleConfigParserFactory();
        }
        IRuleConfigParser parser = factory.createParser();
        parser.parser("");
    }

    /**
     *Java 的异常体系可以分为Error和Exception两大块，Error一般是保留给jvm的，在我们平时编程中不需要也不建议去使用。
     * 我们关心的重点应该是Exception，而Exception可以分为checked exception(受检异常)和unchecked exception(非受检异常)
     * checked exception
     * 会抛出受检异常的语句：只有两种语句会抛出受检异常。第一种是throw exception语句；第二种是函数调用语句，这里被调用的函数的声明中包含了 throws 关键词
     * 特点:一个受检异常一定需要被显式地处理或者向上级抛出，反应在代码中就是要么把会抛出受检异常的语句放在try...catch块中，要么为语句所处的函数添加throws声明。
     * unchecked exception
     * 在函数中抛出非受检异常，不会为这个函数的调用增加任何代码级别的负担。不建议catch一个非受检异常或者为函数声明throws非受检异常
     * 使用场景,使用非受检异常也需要满足两个条件：
     * 这种异常的出现是因为代码存在问题，或者没有进行非空检查，或者没有按照函数的要求传入参数，比如操作一个空指针，数组越界等
     * 这种异常是不能通过实现设定好的处理逻辑被修复的，比如当你操作空指针时，你是没有任何办法也没有理由修复这个空指针让它去指向某个对象的
     */

    //用Map优化工厂模式的各种if-else
    private void test3(String type) throws Exception {
        IRuleConfigParserFactory parserFactory = RuleConfigParserFactoryMap.getParserFactory(type);
        IRuleConfigParser parser = parserFactory.createParser();
        if (parserFactory == null) {
            throw new Exception("Rule config file format is not supported: " + type);
        }
        parser.parser("");
    }
}

