package org.darcy.SimpleProject.config;

import java.util.Date;

import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.type.JdbcType;
import org.darcy.SimpleProject.config.properties.DatabaseProperties;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

import com.alibaba.druid.pool.DruidDataSource;

import tk.mybatis.spring.annotation.MapperScan;

//数据库配置
@Configuration
@EnableConfigurationProperties(DatabaseProperties.class)
@MapperScan(basePackages = "org.darcy.SimpleProject.dao", sqlSessionFactoryRef = "defaultSqlSessionFactory")
public class MybatisConfig {

	private final Log log = LogFactory.getLog(getClass());

	// mapper 路径
	@Value("${mybatis.business.mapper-locations}")
	private String mapperLocation;

	@Autowired
	private DatabaseProperties dbproperties;

	// <!-- step1：定义数据源，druid数据库连接池 -->
	@Bean(name = "defaultDataSource")
	@Primary
	public DataSource druidDataSource() {
		log.info("step1：定义数据源，druid数据库连接池: " + dbproperties.getUrl());
		DruidDataSource dataSource = new DruidDataSource();
		dataSource.setDriverClassName(dbproperties.getDriverClassName());
		dataSource.setUrl(dbproperties.getUrl());
		dataSource.setUsername(dbproperties.getUsername());
		dataSource.setPassword(dbproperties.getPassword());
		dataSource.setInitialSize(dbproperties.getInitialSize());
		dataSource.setMinIdle(dbproperties.getMinIdle());
		dataSource.setMaxActive(dbproperties.getMaxActive());
		dataSource.setMaxWait(dbproperties.getMaxWait());
		dataSource.setTimeBetweenEvictionRunsMillis(dbproperties.getTimeBetweenEvictionRunsMillis());
		dataSource.setMinEvictableIdleTimeMillis(dbproperties.getMinEvictableIdleTimeMillis());
		dataSource.setPoolPreparedStatements(dbproperties.isPoolPreparedStatements());
		dataSource.setMaxPoolPreparedStatementPerConnectionSize(
				dbproperties.getMaxPoolPreparedStatementPerConnectionSize());

		return dataSource;
	}

	// step2：定义SqlSessionFactory
	@Primary
	@Bean(name = "defaultSqlSessionFactory")
	// @DependsOn(value = "defaultDataSource")
	public SqlSessionFactory defaultSqlSessionFactory() throws Exception {
		log.info("step2：定义SqlSessionFactory");
		SqlSessionFactoryBean bean = new SqlSessionFactoryBean();
		// 1.数据源
		bean.setDataSource(druidDataSource());

		// 扫描mybatis mapper文件
		PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
		bean.setMapperLocations(resolver.getResources(mapperLocation));

		// 2.配置文件
		log.debug("step2.1：读取配置前：" + new Date());
		org.apache.ibatis.session.Configuration mybatisConfig = bean.getObject().getConfiguration();
		log.debug("step2.1：读取配置后：" + new Date());
		mybatisConfig.setMapUnderscoreToCamelCase(true);
		mybatisConfig.setJdbcTypeForNull(JdbcType.NULL);
		// 全局的映射器启用或禁用缓存
		mybatisConfig.setCacheEnabled(true);
		// 允许 JDBC 支持生成的键。需要适合的驱动。如果设置为 true 则这个设置强制生成的键被使用，尽管一些驱动拒绝兼容但仍然有效
		mybatisConfig.setUseGeneratedKeys(true);
		// 配置默认的执行器。SIMPLE 执行器没有什么特别之处。REUSE 执行器重用预处理语句。BATCH 执行器重用语句和批量更新
		mybatisConfig.setDefaultExecutorType(ExecutorType.REUSE);
		// 全局启用或禁用延迟加载。当禁用时，所有关联对象都会即时加载。
		mybatisConfig.setLazyLoadingEnabled(true);
		// 设置超时时间，它决定驱动等待一个数据库响应的时间
		mybatisConfig.setDefaultStatementTimeout(25000);

		return bean.getObject();

	}

	// step3：定义sqlSession
	@Scope("prototype")
	@Bean(name = "defaultSqlSession")
	public SqlSession sqlSession() throws Exception {
		log.info("step3：定义sqlSession");
		SqlSessionTemplate a = new SqlSessionTemplate(defaultSqlSessionFactory());
		return a;
	}

	// step4：定义事务管理器
	@Bean
	public PlatformTransactionManager dataSourceTransactionManager() {
		log.info("step4：定义事务管理器transactionManager");
		DataSourceTransactionManager transactionManager = new DataSourceTransactionManager(druidDataSource());
		return transactionManager;
	}
}
