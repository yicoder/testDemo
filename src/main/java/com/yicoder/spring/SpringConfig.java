package com.yicoder.spring;

import java.util.Properties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;

@Configuration
@PropertySource("classpath:kapatcha.properties")
public class SpringConfig {
    /*// 方式一开始：
    @Value("${isBorder}")
    private String isBorder;
    @Value("${borderColor}")
    private String borderColor;
    @Value("${fontColor}")
    private String fontColor;
    @Value("${imgWidth}")
    private String imgWidth;
    @Value("${imgHeight}")
    private String imgHeight;
    @Value("${fontSize}")
    private String fontSize;
    @Value("${sessionKey}")
    private String sessionKey;
    @Value("${codeLength}")
    private String codeLength;
    @Value("${fontName}")
    private String fontName;

    @Bean //要想使用@Value、用${}占位符注入属性，这个bean是必须的，这个就是占位bean,另一种方式是不用value直接用Envirment变量直接getProperty('key')
    public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }

    @Bean
    public Properties captchaProducer() {

        System.out.println(isBorder);

        Properties ps = new Properties();
        ps.setProperty("kaptcha.border", isBorder);
        ps.setProperty("kaptcha.border.color", borderColor);
        ps.setProperty("kaptcha.textproducer.font.color", fontColor);
        ps.setProperty("kaptcha.image.width", imgWidth);
        ps.setProperty("kaptcha.image.height", imgHeight);
        ps.setProperty("kaptcha.textproducer.font.size", fontSize);
        ps.setProperty("kaptcha.session.key", sessionKey);
        ps.setProperty("kaptcha.textproducer.char.length", codeLength);
        ps.setProperty("kaptcha.textproducer.font.names", fontName);

        return ps;
    }
    // 方式一结束*/

    // 方式二：用Envirment方式
    @Autowired
    private Environment env;

    @Bean
    public Properties captchaProducer(){
        Properties ps= new Properties();
        ps.setProperty("kaptcha.border", env.getProperty("isBorder"));
        ps.setProperty("kaptcha.border.color",env.getProperty("borderColor"));
        ps.setProperty("kaptcha.textproducer.font.color", env.getProperty("fontColor"));
        ps.setProperty("kaptcha.image.width", env.getProperty("imgWidth"));
        ps.setProperty("kaptcha.image.height",env.getProperty("imgHeight"));
        ps.setProperty("kaptcha.textproducer.font.size", env.getProperty("fontSize"));
        ps.setProperty("kaptcha.session.key", env.getProperty("sessionKey"));
        ps.setProperty("kaptcha.textproducer.char.length", env.getProperty("codeLength"));
        ps.setProperty("kaptcha.textproducer.font.names",env.getProperty("fontName"));

        System.out.println(env.getProperty("isBorder"));
        return ps;
    }
    // 方式二结束


    /*@Bean
    public DefaultKaptcha captchaProducer(){
      Properties ps= new Properties();
      ps.setProperty("kaptcha.border", "yes");
      ps.setProperty("kaptcha.border.color", "105,179,90");
      ps.setProperty("kaptcha.textproducer.font.color", "blue");
      ps.setProperty("kaptcha.image.width", "125");
      ps.setProperty("kaptcha.image.height", "45");
      ps.setProperty("kaptcha.textproducer.font.size", "45");
      ps.setProperty("kaptcha.session.key", "code");
      ps.setProperty("kaptcha.textproducer.char.length", "4");
      ps.setProperty("kaptcha.textproducer.font.names", "宋体,楷体,微软雅黑");
      Config config = new Config(ps);
      DefaultKaptcha df= new DefaultKaptcha();
      df.setConfig(config);
      return df;

    }*/


}  