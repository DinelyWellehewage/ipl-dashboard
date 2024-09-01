package io.javapro.ipl_dashboard.data;

import javax.sql.DataSource;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import io.javapro.ipl_dashboard.model.Match;

@Configuration
@EnableBatchProcessing
public class BatchConfig {
        private final String[] FIELD_NAMES = new String[] { "id", "season", "city", "date", "match_type",
                        "player_of_match",
                        "venue", "team1", "team2", "toss_winner", "toss_decision", "winner", "result", "result_margin",
                        "target_runs", "target_overs", "super_over", "method", "umpire1", "umpire2"
        };

        @Autowired
        public JobBuilderFactory jobBuilderFactory;
        @Autowired
        public StepBuilderFactory stepBuilderFactory;

        @Bean
        public FlatFileItemReader<MatchInput> reader() {
                return new FlatFileItemReaderBuilder<MatchInput>()
                                .name("MatchItemReader")
                                .resource(new ClassPathResource("match-data.csv"))
                                .delimited()
                                .names(FIELD_NAMES)
                                .targetType(MatchInput.class)
                                .build();
        }

        @Bean
        public MatchDataProcessor processor() {
                return new MatchDataProcessor();
        }

        @Bean
        public JdbcBatchItemWriter<Match> writer(DataSource dataSource) {
                return new JdbcBatchItemWriterBuilder<Match>()
                                .sql("INSERT INTO match (id, city, date, player_of_match, venue, team1, team2, toss_winner, toss_decision, match_winner, result, result_margin, umpire1, umpire2) "
                                                + "VALUES (:id, :city, :date, :playerOfMatch, :venue, :team1, :team2, :tossWinner, :tossDecision, :matchWinner, :result, :resultMargin, :umpire1, :umpire2)")
                                .dataSource(dataSource)
                                .beanMapped()
                                .build();
        }

        @Bean
        public Job importUserJob(JobRepository jobRepository, Step step1, JobCompletionNotificationListener listener) {
                return jobBuilderFactory
                                .get("importUserJob")
                                .incrementer(new RunIdIncrementer())
                                .listener(listener)
                                .flow(step1)
                                .end()
                                .build();
        }

       

        @Bean
        public Step step1(JobRepository jobRepository, DataSourceTransactionManager transactionManager,
                        FlatFileItemReader<MatchInput> reader, MatchDataProcessor processor,
                        JdbcBatchItemWriter<Match> writer) {
                return stepBuilderFactory
                                .get("step1")
                                .<MatchInput, Match>chunk(10)
                                .reader(reader())
                                .processor(processor())
                                .writer(writer)
                                .build();
        }

}
