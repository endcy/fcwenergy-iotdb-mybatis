package com.fcwenergy.iotdb.config;

import com.baomidou.mybatisplus.autoconfigure.MybatisPlusProperties;
import com.baomidou.mybatisplus.extension.spring.MybatisSqlSessionFactoryBean;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import javax.annotation.Resource;
import javax.sql.DataSource;

/**
 * ...
 *
 * @author endcy
 * @date 2024/06/23 18:30:30
 */
@Configuration
@MapperScan(basePackages = "com.fcwenergy.iotdb.modules.mapper", sqlSessionFactoryRef = "iotDbSqlSessionFactory")
public class IotDbDataSourceConfig {

    @Resource
    private MybatisPlusProperties properties;

    @Bean(name = "iotDbDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.iotdb")
    public DataSource iotDbDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean(name = "iotDbSqlSessionFactory")
    public SqlSessionFactory iotDbSqlSessionFactory(@Qualifier("iotDbDataSource") DataSource dataSource) throws Exception {
        MybatisSqlSessionFactoryBean sqlSessionFactory = new MybatisSqlSessionFactoryBean();
        sqlSessionFactory.setDataSource(dataSource);
        // 指定 Mapper 映射文件的位置
        sqlSessionFactory.setMapperLocations(new PathMatchingResourcePatternResolver().getResources("classpath*:iotdb-mapper/*.xml"));
        sqlSessionFactory.setTypeAliasesPackage(properties.getTypeAliasesPackage());
        sqlSessionFactory.setTypeHandlersPackage(properties.getTypeHandlersPackage());
        // 将 MyBatis-Plus 相关配置项添加到 MyBatis 插件列表中
        return sqlSessionFactory.getObject();
    }

    @Bean(name = "iotDbSqlSessionTemplate")
    public SqlSessionTemplate iotDbSqlSessionTemplate(@Qualifier("iotDbSqlSessionFactory") SqlSessionFactory sqlSessionFactory) {
        return new SqlSessionTemplate(sqlSessionFactory);
    }


}
