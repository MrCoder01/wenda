package com.sun;

import org.apache.catalina.connector.Connector;
import org.apache.coyote.http11.Http11NioProtocol;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.embedded.EmbeddedServletContainerFactory;
import org.springframework.boot.context.embedded.MultipartConfigFactory;
import org.springframework.boot.context.embedded.ServletRegistrationBean;
import org.springframework.boot.context.embedded.tomcat.TomcatConnectorCustomizer;
import org.springframework.boot.context.embedded.tomcat.TomcatEmbeddedServletContainerFactory;
import org.springframework.boot.context.web.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.servlet.DispatcherServlet;


import javax.servlet.MultipartConfigElement;
import javax.servlet.Servlet;

@SpringBootApplication
public class WendaApplication extends SpringBootServletInitializer{

	//打包继承与安装
	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
		return builder.sources(WendaApplication.class);
	}

	public static void main(String[] args) {
		SpringApplication.run(WendaApplication.class, args);
	}

//	/**
//	 * 文件上传临时路径
//	 */
//	@Bean
//	MultipartConfigElement multipartConfigElement() {
//		MultipartConfigFactory factory = new MultipartConfigFactory();
//		factory.setLocation("D:/workspace/wenda/src/main/resources/static/");
//		return factory.createMultipartConfig();
//	}


	@Bean
	public EmbeddedServletContainerFactory createEmbeddedServletContainerFactory()
	{
		TomcatEmbeddedServletContainerFactory tomcatFactory = new TomcatEmbeddedServletContainerFactory();
		tomcatFactory.addConnectorCustomizers(new MyTomcatConnectorCustomizer());
		return tomcatFactory;
	}


}
class MyTomcatConnectorCustomizer implements TomcatConnectorCustomizer
{
	public void customize(Connector connector)
	{
		Http11NioProtocol protocol = (Http11NioProtocol) connector.getProtocolHandler();
		//设置最大连接数
		protocol.setMaxConnections(2000);
		System.out.println("~~~~~~~~~~~~");
		//设置最大线程数
//		protocol.setMaxThreads(2000);
//		protocol.setConnectionTimeout(30000);
	}
}
