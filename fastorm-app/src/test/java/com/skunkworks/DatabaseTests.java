package com.skunkworks;

import com.skunkworks.config.DatabaseConfig;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * stole on 03.07.17.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = DatabaseConfig.class)
@Slf4j
public class DatabaseTests {

    @Autowired
    DataSource dataSource;

    @Test
    public void count() throws Exception {
        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement("select count(*) from Customer");
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {

                log.info("result:" + resultSet.getLong(1));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    @Test
    public void selectOne() throws Exception {
        long id = 1;
        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement("select 1 from Customer where id = ?");
            preparedStatement.setLong(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            boolean res = resultSet.next();
            log.info("result:" + res);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }
}
