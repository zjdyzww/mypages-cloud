package cn.m1yellow.mypages.excavation;

import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

/**
 * 打 war 包导入外部 tomcat 运行，需要改变启动方式
 */
public class ServletInitializer extends SpringBootServletInitializer {
    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        // 此处的 Application.class 为带有 @SpringBootApplication 注解的启动类
        return builder.sources(MypagesExcavationApplication.class);
    }
}
