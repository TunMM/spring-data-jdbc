package com.tiem.spring_data_jdbc;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.jdbc.core.JdbcTemplate;

import com.tiem.spring_data_jdbc.entities.Customer;

@SpringBootApplication
public class SpringDataJdbcApplication implements CommandLineRunner {

	private static final Logger log = LoggerFactory.getLogger(SpringDataJdbcApplication.class);

	@Autowired
	private JdbcTemplate jdbcTemplate;

	public static void main(String[] args) {
		SpringApplication.run(SpringDataJdbcApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		// TODO Auto-generated method stub
		log.info("Creating table");

		jdbcTemplate.execute("DROP TABLE customers IF EXISTS");
		jdbcTemplate.execute("CREATE TABLE customers (id SERIAL, first_name VARCHAR(255), last_name VARCHAR(255))");

		List<Object[]> splitUpNames = Arrays.asList("Maung Kyaw", "Aung Hein", "Min Thu", "Kyaw Shwe", "Hla Mg")
				.stream().map(names -> names.split(" ")).collect(Collectors.toList());
		
		splitUpNames.forEach(name -> log.info(String.format("Inserting customer record for %s %s", name[0], name[1])));
		
		jdbcTemplate.batchUpdate("INSERT INTO customers(first_name, last_name) VALUES (?, ?)", splitUpNames);
		
		log.info("Querying for customer records where first_name = 'Kyaw'");
		
		jdbcTemplate.query("SELECT * FROM customers WHERE first_name = ?", 
				(rs, rownum) -> new Customer(rs.getLong("id"), rs.getString("first_name"), rs.getString("last_name")) 
				, "Kyaw").stream().forEach(cus -> log.info(cus.toString()));
		
		jdbcTemplate.query("SELECT * FROM customers WHERE first_name = ?", 
				(rs, rownum) -> new Customer(rs.getLong("id"), rs.getString("first_name"), rs.getString("last_name")) 
				, "Hla").stream().forEach(cus -> log.info(cus.toString()));
		
		jdbcTemplate.query("SELECT * FROM customers WHERE last_name = ?", 
				(rs, rownum) -> new Customer(rs.getLong("id"), rs.getString("first_name"), rs.getString("last_name")) 
				, "Thu").stream().forEach(cus -> log.info(cus.toString()));
	}

}
