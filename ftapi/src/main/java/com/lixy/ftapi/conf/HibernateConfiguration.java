package com.lixy.ftapi.conf;

import java.util.Properties;

import javax.sql.DataSource;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
@ComponentScan({ "com.lixy.ftapi.conf" })
@PropertySource(value = { "classpath:hibernate.properties" })
public class HibernateConfiguration {

	@Autowired
	private Environment environment;

	@Bean
	public LocalSessionFactoryBean sessionFactory() {
		LocalSessionFactoryBean sessionFactory = new LocalSessionFactoryBean();
		
		sessionFactory.setHibernateProperties(hibernateProperties());
		sessionFactory.setDataSource(dataSource());
		sessionFactory.setPackagesToScan(new String[] { environment.getRequiredProperty("packages.to.scan.for.entity") });
		
		return sessionFactory;
	}

	@Bean
	public DataSource dataSource() {
		Properties properties = new Properties();
		properties.put("CharSet", environment.getRequiredProperty("hibernate.connection.CharSet"));
		properties.put("characterEncoding", environment.getRequiredProperty("hibernate.connection.characterEncoding"));
		properties.put("useUnicode", environment.getRequiredProperty("hibernate.connection.useUnicode"));

		DriverManagerDataSource dataSource = new DriverManagerDataSource();
		dataSource.setConnectionProperties(properties);
		dataSource.setDriverClassName(environment.getRequiredProperty("hibernate.connection.driver_class"));
		dataSource.setUrl(environment.getRequiredProperty("hibernate.connection.url"));
		dataSource.setUsername(environment.getRequiredProperty("hibernate.connection.username"));
		dataSource.setPassword(environment.getRequiredProperty("hibernate.connection.password"));	
		
		return dataSource;
	}

	private Properties hibernateProperties() {
		Properties properties = new Properties();
		properties.put("hibernate.dialect", environment.getRequiredProperty("hibernate.dialect"));
		properties.put("hibernate.show_sql", environment.getRequiredProperty("hibernate.show_sql"));
		properties.put("hibernate.format_sql", environment.getRequiredProperty("hibernate.format_sql"));
		
		properties.put("hibernate.c3p0.min_size", "5");
		properties.put("hibernate.c3p0.max_size", "20");
		properties.put("hibernate.c3p0.timeout", "300");
		properties.put("hibernate.c3p0.max_statements", "50");
		properties.put("hibernate.c3p0.idle_test_period", "300");
		
		properties.put("hibernate.connection.CharSet", environment.getRequiredProperty("hibernate.connection.CharSet"));
		properties.put("hibernate.connection.characterEncoding",environment.getRequiredProperty("hibernate.connection.characterEncoding"));
		properties.put("hibernate.connection.useUnicode", environment.getRequiredProperty("hibernate.connection.useUnicode"));
		
		properties.put("hibernate.id.new_generator_mappings", environment.getRequiredProperty("hibernate.id.new_generator_mappings"));

		return properties;
	}

	@Bean
	@Autowired
	public HibernateTransactionManager transactionManager(SessionFactory s) {
		HibernateTransactionManager txManager = new HibernateTransactionManager();
		txManager.setSessionFactory(s);
		return txManager;
	}

}
