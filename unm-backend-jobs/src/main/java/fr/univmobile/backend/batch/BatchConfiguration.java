package fr.univmobile.backend.batch;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.orm.jpa.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;

import fr.univmobile.backend.jobs.utils.ApiParisUtils;
import fr.univmobile.backend.jobs.utils.FeedUtils;

import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableAutoConfiguration
@ComponentScan
@EntityScan(basePackages = "fr.univmobile.backend.domain")
@EnableJpaRepositories(basePackages = "fr.univmobile.backend.domain")
//@EnableJpaAuditing(auditorAwareRef = "auditorProvider")
@EnableBatchProcessing
public class BatchConfiguration {

	@Autowired
	private JobBuilderFactory jobBuilderFactory;

	@Autowired
	private StepBuilderFactory stepBuilderFactory;
 
	@Autowired
	private FeedUtils feedUtils;

	@Autowired
	private ApiParisUtils apiParisUtils;

	@Bean 
	FeedUtils feedUtils() {
		return new FeedUtils();
	}

	@Bean 
	ApiParisUtils apiParisUtils() {
		return new ApiParisUtils();
	}
	    
	@Bean
	public Step rssStep() {
		return stepBuilderFactory.get("rssStep").tasklet(new Tasklet() {
			public RepeatStatus execute(StepContribution contribution,
					ChunkContext chunkContext) {

				feedUtils.persistFeeds();

				return null;
			}
		}).build();
	}

	@Bean
	public Job rssJob(Step rssStep) throws Exception {
		return jobBuilderFactory.get("rssJob")
				.incrementer(new RunIdIncrementer()).start(rssStep).build();
	}

	// ApiParis Job

	@Bean
	public Step apiParisStep() {
		return stepBuilderFactory.get("apiParisStep").tasklet(new Tasklet() {
			public RepeatStatus execute(StepContribution contribution,
					ChunkContext chunkContext) {

				apiParisUtils.getEvents();

				return null;
			}
		}).build();
	}

	@Bean
	public Job apiParisJob(Step apiParisStep) throws Exception {
		return jobBuilderFactory.get("apiParisJob")
				.incrementer(new RunIdIncrementer()).start(apiParisStep)
				.build();
	} 
}
