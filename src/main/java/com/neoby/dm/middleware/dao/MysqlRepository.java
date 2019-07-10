package com.neoby.dm.middleware.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
public class MysqlRepository {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public MysqlRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }


    public boolean execute(String sql) {
        log.debug(sql.substring(0, 6));
        boolean state = Boolean.FALSE;
        try {
            jdbcTemplate.execute(sql);
            state = Boolean.TRUE;
        } catch (DuplicateKeyException e) {
            log.error("Duplicate entry");
        } catch (DataAccessException e) {
            log.error(sql);
            log.error(e.toString());
        }
        return state;
    }
}
