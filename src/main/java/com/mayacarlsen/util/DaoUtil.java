package com.mayacarlsen.util;

import java.sql.Connection;
import java.util.logging.Logger;

import org.apache.tomcat.jdbc.pool.PoolProperties;
import org.apache.tomcat.jdbc.pool.XADataSource;
import org.sql2o.Sql2o;

public class DaoUtil {
	
	private static final Logger logger = Logger.getLogger(DaoUtil.class.getCanonicalName());

	// Heroku PostgreSQL database connection from system environment variable
//	public static final String HEROKU_DATABASE_URL = System.getenv("JDBC_DATABASE_URL");
	
	// Heroku PostgreSQL database connection URL
	public static final String HEROKU_DATABASE_URL = "jdbc:postgresql://ec2-54-225-71-119.compute-1.amazonaws.com:5432/df1ee1153d9ulv?user=lnvfbhpyxbknmy&password=201decc8aa33924cfd161680d73336add505fe894ccd84cf37d1eeddf347c6fa&sslmode=require";
	
	private final static Sql2o sql2o = new Sql2o(HEROKU_DATABASE_URL, null, null);

	private static XADataSource datasource;
	
	static {
		initDatasource();
	}

	public static Sql2o getSql2o() {
		return sql2o;
	}

	public static Connection getConnection() {
        try {
            return datasource.getConnection();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

	public static void initDatasource() {
        PoolProperties p = new PoolProperties();
        
        p.setUrl(HEROKU_DATABASE_URL);
        p.setDriverClassName("org.postgresql.Driver");
 
        p.setJmxEnabled(true);
        p.setTestWhileIdle(false);
        p.setTestOnBorrow(true);
        p.setValidationQuery("SELECT 1 FROM DUAL");
        p.setTestOnReturn(false);
        p.setValidationInterval(30000);
        p.setTimeBetweenEvictionRunsMillis(30000);
 
        p.setMaxActive(5);
        p.setMaxIdle(5);
        p.setInitialSize(2);
        p.setMaxWait(10000);
        p.setRemoveAbandonedTimeout(60);
        p.setMinEvictableIdleTimeMillis(30000);
        p.setMinIdle(1);
 
        p.setLogAbandoned(true);        
        p.setRemoveAbandoned(true);
 
        p.setJdbcInterceptors("org.apache.tomcat.jdbc.pool.interceptor.ConnectionState;"
                + "org.apache.tomcat.jdbc.pool.interceptor.StatementFinalizer");
 
        datasource = new XADataSource();
        datasource.setPoolProperties(p);
 
    }
 
    public static void closeDatasource() {
        datasource.close();
    }
}
