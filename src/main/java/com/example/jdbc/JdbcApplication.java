package com.example.jdbc;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.map.IMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.core.DataClassRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;
import java.util.Map;

@SpringBootApplication
public class JdbcApplication {

    private static final Logger logger = LoggerFactory.getLogger(JdbcApplication.class);

    @Bean
    @Order(1)
    public CommandLineRunner dataInsert(@Qualifier("hazelcastInstance") HazelcastInstance hazelcastInstance) {
        return args -> {
            IMap<Object, Object> people = hazelcastInstance.getMap("person");
            people.put(1, new Person("John", 23));
            people.put(2, new Person("Mary", 35));
            people.put(3, new Person("Amber", 15));
            people.put(4, new Person("Matthew", 68));
            people.put(5, new Person("Carol", 45));
            people.put(6, new Person("Carolyn", 45));
        };
    }

    @Bean
    @Order(2)
    public CommandLineRunner queryData(JdbcTemplate jdbcTemplate) {
        return args -> {
            List<Map<String, Object>> maps = jdbcTemplate.queryForList("SELECT * FROM person");
            logger.info(String.valueOf(maps));
            List<Person> allPeople = jdbcTemplate.query("SELECT * FROM person", DataClassRowMapper.newInstance(Person.class));
            logger.info(String.valueOf(allPeople));
            Integer age = jdbcTemplate.queryForObject("SELECT age FROM person WHERE name = 'Matthew'", Integer.class);
            logger.info(String.valueOf(age));
            List<Person> person = jdbcTemplate.query("SELECT * FROM person WHERE name = ?", DataClassRowMapper.newInstance(Person.class), "Carolyn");
            person.forEach(item -> { logger.info(item.name+" ===== "+item.age);});
            //logger.info(String.valueOf(person));
        };
    }

    public static void main(String[] args) {
        SpringApplication.run(JdbcApplication.class, args);
    }

}
