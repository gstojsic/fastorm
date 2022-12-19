package com.skunkworks;

import com.skunkworks.config.DatabaseConfig;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.util.Arrays;
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
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

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

    @Test
    public void selectIn() throws Exception {
        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement("select * from Customer where id in (?)");
            Long[] ids = Arrays.<Long>array(1L, 2L, 3L, 4L);
            preparedStatement.setArray(1, connection.createArrayOf("bigint", ids));
            ResultSet resultSet = preparedStatement.executeQuery();
            boolean res = resultSet.next();
            log.info("result:" + res);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void calculate() throws Exception {
        int DAYS = 365 * 1;
        //int DAYS = 183 * 1;
        double prftPerDayPerMHS = .0747D; //usd
        int mhs = 20;
        double mhsPrice = 13.5;
        double startInvestment = mhsPrice * mhs;
        double outPerDay = .0D;
        double cumulativeProfit = .0D;
        LinkedHashMap<Integer, Integer> mhsPlusDays = new LinkedHashMap<>();
        for (int i = 1; i <= DAYS; i++) {
            outPerDay = prftPerDayPerMHS * mhs;
            cumulativeProfit += outPerDay;
            if (cumulativeProfit >= mhsPrice) {
                int increment = (int) Math.floor(cumulativeProfit / mhsPrice);
                mhsPlusDays.put(i, increment);
                mhs += increment;
                cumulativeProfit -= (mhsPrice * increment);
            }
        }
        log.info("total mhs:" + mhs);
        log.info("exit output per day:" + Double.toString(outPerDay));
        log.info("start investment repay for exit output (days):" + (startInvestment / outPerDay));
        log.info("mhsPlusDays:" + mhsPlusDays);

        LocalDate startDate = LocalDate.of(2017, 6, 25);
        mhsPlusDays.keySet().forEach(days -> log.info("change day: " + startDate.plus(days, ChronoUnit.DAYS)));
    }
}
