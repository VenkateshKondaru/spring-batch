package com.venkate.boot.batch.config;



import javax.sql.DataSource;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.stereotype.Component;

import com.venkate.boot.batch.entity.Product;

@Configuration
@Component
public class BatchConfiguration {
	
	@Autowired
	private JobBuilderFactory jbf;
	
	@Autowired
	private StepBuilderFactory sdf;
	
	@Bean
	public Job job() {
		return jbf.get("CsvtoDB")
				.incrementer(new RunIdIncrementer())
				.start(step()).build();
	}
	@Bean
	public Step step() {
		return sdf.get("Upload1").<Product,Product>chunk(3).reader(reader()).processor(processor()).writer(writer()).build();
	}
	@Bean
	public ItemReader<Product> reader() {
		FlatFileItemReader<Product> reader=new FlatFileItemReader<>();
		reader.setResource(new ClassPathResource("Products.csv"));
		DefaultLineMapper<Product> lineMapper = new DefaultLineMapper<>();
		DelimitedLineTokenizer lineTokenizer = new DelimitedLineTokenizer();
		lineTokenizer.setNames("id","name","description","price");
		BeanWrapperFieldSetMapper<Product> fieldSetMapper = new BeanWrapperFieldSetMapper<>();
		fieldSetMapper.setTargetType(Product.class);
		lineMapper.setLineTokenizer(lineTokenizer);
		lineMapper.setFieldSetMapper(fieldSetMapper);
		reader.setLineMapper(lineMapper);
		return reader;
	}
	
	@Bean
	public ItemProcessor<Product, Product> processor(){
		
		return (p)->{p.setPrice(p.getPrice()*0.9567);
		return p;
		};
	}
	
	@Bean
	public ItemWriter<Product> writer(){
		JdbcBatchItemWriter<Product> writer= new JdbcBatchItemWriter<>();
		writer.setDataSource(dataSource());
		writer.setItemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<Product>());
		writer.setSql("INSERT INTO PRODUCT(ID,NAME,DESCRIPTION,PRICE) values(:id,:name,:description,:price)");
		return writer;
	}
	
	@Bean
	public DataSource dataSource() {
		DriverManagerDataSource dataSource=new DriverManagerDataSource();
		dataSource.setDriverClassName("com.mysql.jdbc.Driver");
		dataSource.setUrl("jdbc:mysql://localhost:3306/mydb");
		dataSource.setUsername("root");
		dataSource.setPassword("test1234");
		return dataSource;
	}

}
