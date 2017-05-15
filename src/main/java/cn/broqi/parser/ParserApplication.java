package cn.broqi.parser;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.embedded.ServletContextInitializer;
import org.springframework.boot.context.web.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;


@ComponentScan("cn.broqi.parser")
@Configuration
@EnableAutoConfiguration
@SpringBootApplication
@EnableScheduling
//@EnableWebMvc	
public class ParserApplication extends SpringBootServletInitializer {

	public static void main(String[] args) {
		SpringApplication.run(ParserApplication.class, args);
	}

	@Bean
	public ServletContextInitializer initializer() {
		return new ServletContextInitializer() {
			public void onStartup(ServletContext servletContext) throws ServletException {
				System.setProperty("parser.root", servletContext.getRealPath("/"));
			}
		};
	}
	

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(ParserApplication.class);
	}

	@Bean(destroyMethod = "shutdown")
	public Executor taskExecutor() {
		return Executors.newScheduledThreadPool(10);
	}
}
