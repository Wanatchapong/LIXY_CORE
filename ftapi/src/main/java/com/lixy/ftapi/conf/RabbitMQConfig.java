package com.lixy.ftapi.conf;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.retry.backoff.ExponentialBackOffPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
@ComponentScan({ "com.lixy.ftapi.conf" })
@PropertySource(value = { "classpath:rabbit_mq.properties" }, ignoreResourceNotFound=true)
public class RabbitMQConfig {
	
	@Autowired
	private Environment environment;

	@Bean
	public CachingConnectionFactory connectionFactory() {
		CachingConnectionFactory connectionFactory = new CachingConnectionFactory();
		connectionFactory.setHost(environment.getRequiredProperty("rabbitmq.host"));
		connectionFactory.setPort(Integer.valueOf(environment.getRequiredProperty("rabbitmq.port")));
		connectionFactory.setUsername(environment.getRequiredProperty("rabbitmq.username"));
		connectionFactory.setPassword(environment.getRequiredProperty("rabbitmq.password"));
		return connectionFactory;
	}

	@Bean
	public RabbitAdmin amqpAdmin(CachingConnectionFactory connectionFactory) {
		return new RabbitAdmin(connectionFactory);
	}

	@Bean
	public RabbitTemplate rabbitTemplate(CachingConnectionFactory connectionFactory,  RetryTemplate retryTemplate) {
		RabbitTemplate template = new RabbitTemplate(connectionFactory);
		template.setExchange("FTAPI_EXCHANGE");
		template.setRetryTemplate(retryTemplate);
		return template;
	}
	
	@Bean
	public RabbitTemplate requestTemplate(CachingConnectionFactory connectionFactory,  RetryTemplate retryTemplate) {
		RabbitTemplate template = new RabbitTemplate(connectionFactory);
		template.setExchange("FTAPI_REQUEST_EXCHANGE");
		template.setRetryTemplate(retryTemplate);
		return template;
	}
	
	@Bean
	public RetryTemplate retryTemplate() {
		RetryTemplate template = new RetryTemplate();
		
		SimpleRetryPolicy retryPolicy = new SimpleRetryPolicy();
		retryPolicy.setMaxAttempts(2); //1 try , 1 fail try
		
		ExponentialBackOffPolicy backOffPolicy = new ExponentialBackOffPolicy();
		backOffPolicy.setMaxInterval(50000);
		backOffPolicy.setInitialInterval(50000);
		
		template.setRetryPolicy(retryPolicy);
		template.setBackOffPolicy(backOffPolicy);
		return template;
	}
	
	@Bean
	public Queue newFortuneQueue() {
		return new Queue("NEW_FORTUNE_QUEUE");
	}

	@Bean
	public Queue tokenExpireQueue() {
		return new Queue("TOKEN_EXPIRE_QUEUE");
	}
	
	@Bean
	public Queue newCustomerQueue() {
		return new Queue("NEW_CUSTOMER_QUEUE");
	}
	
	@Bean
	public Queue mailQueue() {
		return new Queue("MAIL_QUEUE");
	}
	
	@Bean
	public Queue requestQueue() {
		return new Queue("REQUEST_QUEUE");
	}
	
	@Bean
	public Queue alarmQueue() {
		return new Queue("ALARM_QUEUE");
	}
	
	@Bean
	public Queue uploadQueue() {
		return new Queue("UPLOAD_QUEUE");
	}
	
	@Bean
	public TopicExchange topicExchange() {
		return new TopicExchange("FTAPI_EXCHANGE");
	}
	
	@Bean
	public TopicExchange requestExchange() {
		return new TopicExchange("FTAPI_REQUEST_EXCHANGE");
	}
	
	@Bean
	public Binding requestExchangeTokenExpireQueueBinding(TopicExchange requestExchange, Queue requestQueue) {
		return BindingBuilder.bind(requestQueue).to(requestExchange).with("REQUEST");
	}

	@Bean
	public Binding topicExchangeTokenExpireQueueBinding(TopicExchange topicExchange, Queue tokenExpireQueue) {
		return BindingBuilder.bind(tokenExpireQueue).to(topicExchange).with("TOKENEXPIRE");
	}
	
	@Bean
	public Binding topicExchangeUploadQueueBinding(TopicExchange topicExchange, Queue uploadQueue) {
		return BindingBuilder.bind(uploadQueue).to(topicExchange).with("FILEUPLOAD");
	}
	
	@Bean
	public Binding topicExchangeNewFortuneQueueBinding(TopicExchange topicExchange, Queue newFortuneQueue) {
		return BindingBuilder.bind(newFortuneQueue).to(topicExchange).with("NEWFORTUNE");
	}
	
	
	@Bean
	public Binding topicExchangeNewUserQueueBinding(TopicExchange topicExchange, Queue newCustomerQueue) {
		return BindingBuilder.bind(newCustomerQueue).to(topicExchange).with("NEWCUSTOMER");
	}
	
	@Bean
	public Binding topicExchangeMailQueueBinding(TopicExchange topicExchange, Queue mailQueue) {
		return BindingBuilder.bind(mailQueue).to(topicExchange).with("MAIL");
	}
	
	@Bean
	public Binding topicExchangeAlarmQueueBinding(TopicExchange topicExchange, Queue alarmQueue) {
		return BindingBuilder.bind(alarmQueue).to(topicExchange).with("ALARM");
	}

}
