package com.alextjalsma.observability;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;

@SpringBootApplication
public class ObservabilityApplication {

    public static void main(String[] args) {
        SpringApplication.run(ObservabilityApplication.class, args);
    }

    @Bean
    ApplicationListener<ApplicationReadyEvent> readyEventApplicationListener(CustomerService cs) {
        return event -> cs.all().forEach(System.out::println);
    }
}

@Service
class CustomerService {

    private final JdbcTemplate template;

    private final RowMapper<Customer> customerRowMapper =
			(rs, rowNum) -> new Customer(rs.getInt("id"), rs.getString("name"));

    public CustomerService(JdbcTemplate template) {
        this.template = template;
    }

	Customer byId(Integer id) {
		return this.template.queryForObject("select * from customer where id=?",
				this.customerRowMapper, id);
	}

	Collection<Customer> all() {
		return this.template.query("select * from customer", this.customerRowMapper);
	}


}

record Customer(Integer id, String name) {

}