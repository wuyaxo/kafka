package com.neoby.dm.middleware.dao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class MysqlRepository {

    private static final Logger logger = LoggerFactory.getLogger(MysqlRepository.class);

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public MysqlRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }


    public boolean execute(String sql) {
        logger.info(sql.substring(0, 6));
        boolean state = Boolean.FALSE;
        try {
            jdbcTemplate.execute(sql);
            state = Boolean.TRUE;
        } catch (DuplicateKeyException e){
            logger.debug("Duplicate entry");
        } catch (DataAccessException e) {
            logger.error(sql);
            logger.error(e.toString());
        }
        return state;
    }
}
