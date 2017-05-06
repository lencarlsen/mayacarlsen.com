package com.mayacarlsen.util;

import java.sql.Connection;
import java.util.logging.Logger;

import org.apache.tomcat.jdbc.pool.PoolProperties;
import org.apache.tomcat.jdbc.pool.XADataSource;
import org.sql2o.Sql2o;
import org.sql2o.quirks.PostgresQuirks;

public class DAOUtil {

    private static final Logger logger = Logger.getLogger(DAOUtil.class.getCanonicalName());

    // Heroku PostgreSQL database connection from system environment variable
    public static final String HEROKU_DATABASE_URL = System.getenv("JDBC_DATABASE_URL");

    private final static Sql2o sql2o;

    private static XADataSource dataSource;

    static {
	initDatasource();
	sql2o = new Sql2o(dataSource, new PostgresQuirks());
    }

    public static Sql2o getSql2o() {
	return sql2o;
    }

    public static Connection getConnection() {
	try {
	    return dataSource.getConnection();
	} catch (Exception e) {
	    throw new RuntimeException(e);
	}
    }

    public static void initDatasource() {
	PoolProperties p = new PoolProperties();

	p.setUrl(HEROKU_DATABASE_URL);
	p.setDriverClassName("org.postgresql.Driver");

	p.setDefaultAutoCommit(false);
	p.setJmxEnabled(false);
	p.setTestWhileIdle(true);
	p.setTestOnBorrow(true);
	p.setTestOnReturn(true);
	p.setValidationQuery("SELECT 1");
	p.setValidationInterval(30000);
	p.setTimeBetweenEvictionRunsMillis(60000);

	p.setMaxActive(5);
	p.setMaxIdle(1);
	p.setMinIdle(1);
	p.setInitialSize(1);
	p.setMaxWait(10000);
	// p.setRemoveAbandoned(true);
	// p.setRemoveAbandonedTimeout(60);
	// p.setLogAbandoned(false);
	p.setMinEvictableIdleTimeMillis(30000);

	p.setJdbcInterceptors("org.apache.tomcat.jdbc.pool.interceptor.ConnectionState;"
		+ "org.apache.tomcat.jdbc.pool.interceptor.StatementFinalizer");

	dataSource = new XADataSource();
	dataSource.setPoolProperties(p);
    }

    public static void closeDatasource() {
	dataSource.close();
    }
}
