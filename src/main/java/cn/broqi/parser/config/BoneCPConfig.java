package cn.broqi.parser.config;

import javax.sql.DataSource;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.jolbox.bonecp.BoneCPDataSource;

@Configuration
public class BoneCPConfig {
  
  @Bean(destroyMethod = "close")
  @ConfigurationProperties(prefix = "spring.datasource")
  public DataSource dataSource() {
    BoneCPDataSource dataSource = new BoneCPDataSource();
    return dataSource;
  }
  
}
