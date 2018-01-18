package fm;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.springframework.context.annotation.Bean;
import org.thymeleaf.extras.java8time.dialect.Java8TimeDialect;

@SpringBootApplication
public class FootballManagerApplication {
	
	public static Log logger = LogFactory.getLog(FootballManagerApplication.class);
	
	@Bean
	protected ServletContextListener listener() {
		return new ServletContextListener() {
			@Override
			public void contextInitialized(ServletContextEvent event) {
				logger.info("ServletContext initialized");
			}
			
			@Override
			public void contextDestroyed(ServletContextEvent event) {
				logger.info("ServletContext destroyed");
			}
		};
	}
	
	@Bean
    public Java8TimeDialect java8TimeDialect() {
        return new Java8TimeDialect();
    }
	
	public static void main(String[] args) {
		SpringApplication.run(FootballManagerApplication.class, args);
	}
}
