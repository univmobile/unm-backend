package fr.univmobile.backend.jobs.test;

import java.util.Properties;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import fr.univmobile.backend.domain.User;
import fr.univmobile.backend.jobs.utils.ApiParisUtils;

@Configuration
@EnableJpaRepositories("fr.univmobile.backend.domain")
@EnableTransactionManagement
@EnableJpaAuditing
public class PersistenceJPAConfig {

	 @Bean
	 public AuditorAware<User> TestAuditorAwarebean() {
	  return new AuditorAware<User>() {
	   @Override
	   public User getCurrentAuditor() {
	    return null;
	   }
	  };
	 }

	@Bean
	public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
		LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
		em.setDataSource(dataSource());
		em.setPersistenceUnitName("domain-persistence");

		JpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
		em.setJpaVendorAdapter(vendorAdapter);
		em.setJpaProperties(additionalProperties());

		return em;
	}

	@Bean
	public DataSource dataSource() {
		DriverManagerDataSource dataSource = new DriverManagerDataSource();
		dataSource.setDriverClassName("com.mysql.jdbc.Driver");
		dataSource.setUrl("jdbc:mysql://localhost:3306/univmobile_new");
		dataSource.setUsername("root");
		dataSource.setPassword("123456");
		return dataSource;
	}

	@Bean
	public PlatformTransactionManager transactionManager(
			EntityManagerFactory emf) {
		JpaTransactionManager transactionManager = new JpaTransactionManager();
		transactionManager.setEntityManagerFactory(emf);

		return transactionManager;
	}

	@Bean
	public PersistenceExceptionTranslationPostProcessor exceptionTranslation() {
		return new PersistenceExceptionTranslationPostProcessor();
	}

	Properties additionalProperties() {
		Properties properties = new Properties();
		properties.setProperty("hibertname.show_sql", "true");
		properties.setProperty("hibernate.hbm2ddl.auto", "update");
		properties.setProperty("hibernate.dialect",
				"org.hibernate.dialect.MySQL5Dialect");
		return properties;
	}
	
	 @Bean 
	 public ApiParisUtils apiParisUtils() {
	  return new ApiParisUtils();
	 }
	 
	 @Bean
	 public static PropertyPlaceholderConfigurer propertyPlaceholderConfigurer() {
	     PropertyPlaceholderConfigurer ppc = new PropertyPlaceholderConfigurer();
	     ppc.setIgnoreResourceNotFound(true);
	     return ppc;
	 }
	 
}