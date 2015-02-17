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
import org.springframework.context.annotation.Configuration;

import fr.univmobile.backend.jobs.utils.ApiParisUtils;
import fr.univmobile.backend.jobs.utils.FeedUtils;

import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableAutoConfiguration
@ComponentScan
@EntityScan(basePackages = "fr.univmobile.backend.domain")
@EnableJpaRepositories(basePackages = "fr.univmobile.backend.domain")
@EnableJpaAuditing(auditorAwareRef = "sessionAuditorAware")
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
	public Step rssFeedStep() {
		return stepBuilderFactory.get("rssFeedStep").tasklet(new Tasklet() {
			public RepeatStatus execute(StepContribution contribution,
					ChunkContext chunkContext) {

				feedUtils.persistRssFeeds();

				return null;
			}
		}).build();
	}

	@Bean
	public Job rssFeedJob(Step rssStep) throws Exception {
		return jobBuilderFactory.get("rssFeedJob")
				.incrementer(new RunIdIncrementer()).start(rssStep).build();
	}

	@Bean
	public Step customFeedStep() {
		return stepBuilderFactory.get("customFeedStep").tasklet(new Tasklet() {
			public RepeatStatus execute(StepContribution contribution,
										ChunkContext chunkContext) {

				feedUtils.persistCustomFeed();

				return null;
			}
		}).build();
	}
	@Bean
	public Job customFeedJob(Step customNewsStep) throws Exception {
		return jobBuilderFactory.get("customFeedStep").incrementer(new RunIdIncrementer()).start(customNewsStep).build();
	}

	@Bean
	public Step restoMenuesStep() {
		return stepBuilderFactory.get("restoMenuesStep").tasklet(new Tasklet() {
			public RepeatStatus execute(StepContribution contribution,
										ChunkContext chunkContext) {

				feedUtils.persistRestoMenues();

				return null;
			}
		}).build();
	}
	@Bean
	public Job restoMenuesJob(Step restoMenuesStep) throws Exception {
		return jobBuilderFactory.get("restoMenuesStep").incrementer(new RunIdIncrementer()).start(restoMenuesStep).build();
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
