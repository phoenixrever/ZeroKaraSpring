package com.phoenixhell.zerokaraspring.mvc.servlet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.servlet.context.AnnotationConfigServletWebServerApplicationContext;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.servlet.HandlerExecutionChain;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class DispatchServletDemo {
    private static final Logger log = LoggerFactory.getLogger(DispatchServletDemo.class);

    public static void main(String[] args) throws Exception {
        //选择支持内嵌tomcat的的spring容器的实现
        AnnotationConfigServletWebServerApplicationContext context =
                new AnnotationConfigServletWebServerApplicationContext(WebConfig.class);

        // 作用 解析 @RequestMapping 以及派生注解，生成路径与控制器方法的映射关系, 在初始化时就生成
        RequestMappingHandlerMapping handlerMapping = context.getBean(RequestMappingHandlerMapping.class);

        // 获取映射结果
        Map<RequestMappingInfo, HandlerMethod> handlerMethods = handlerMapping.getHandlerMethods();
        handlerMethods.forEach((k, v) -> {
            System.out.println(k + "=" + v);
        });
        //
        // 请求来了，获取控制器方法  返回处理器执行链对象
        /**
         * 注意这边使用mock 有条件一个就是此类在Test 环境下
         * 另外一个就是把add add library 把Spring-test假如到classpath
         * 还有就在pom文件中假如spring test
         * 总之这边选加class path的方法 不用纠结
         */

        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/test4");
        request.setParameter("name", "张三");
        request.addHeader("token", "某个令牌");
        MockHttpServletResponse response = new MockHttpServletResponse();
        HandlerExecutionChain chain = handlerMapping.getHandler(request);
        System.out.println(chain);


        System.out.println(">>>>>>>>>>>>>>>>>>>>>");
        // HandlerAdapter 作用: 调用控制器方法
        MyRequestMappingHandlerAdapter handlerAdapter = context.getBean(MyRequestMappingHandlerAdapter.class);
        handlerAdapter.invokeHandlerMethod(request, response, (HandlerMethod) chain.getHandler());
        // 检查响应 使用数组不用string是因为不容易出现乱码
        byte[] content = response.getContentAsByteArray();
        System.out.println(new String(content, StandardCharsets.UTF_8));

        //System.out.println(">>>>>>>>>>>>>>>>>>>>> 所有参数解析器");
        //for (HandlerMethodArgumentResolver resolver : handlerAdapter.getArgumentResolvers()) {
        //    System.out.println(resolver);
        //}
        //
        //System.out.println(">>>>>>>>>>>>>>>>>>>>> 所有返回值解析器");
        //for (HandlerMethodReturnValueHandler handler : handlerAdapter.getReturnValueHandlers()) {
        //    System.out.println(handler);
        //}

        /*
        学到了什么
            a. DispatcherServlet 是在第一次被访问时执行初始化, 也可以通过配置修改为 Tomcat 启动后就初始化
            b. 在初始化时会从 Spring 容器中找一些 Web 需要的组件, 如 HandlerMapping、HandlerAdapter 等

        本章介绍两个最为重要的组件
            a. RequestMappingHandlerAdapter, 以 @RequestMapping 作为映射路径
            b. RequestMappingHandlerAdapter, 调用 handler
            c. 控制器的具体方法会被当作 handler
                - handler 的参数和返回值多种多样
                - 需要解析方法参数, 由 HandlerMethodArgumentResolver 来做
                - 需要处理方法返回值, 由 HandlerMethodReturnValueHandler 来做
        */
    }
}
