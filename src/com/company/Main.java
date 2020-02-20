package com.company;

import com.company.bean.Employee;
import com.company.dao.EmployeeMapper;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;

public class Main {

    public SqlSessionFactory getSqlSessionFactory() {
        InputStream inputStream = null;
        try {
            inputStream = Resources.getResourceAsStream("mybatis-config.xml");
        } catch (IOException e) {
            e.printStackTrace();
        }
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
        return sqlSessionFactory;
    }

    @Test
    /**
     * 1、根据mybatis-config.xml配置文件创建一个SqlSessionFactory对象
     * 2、sql映射文件EmployeeMapper.xml，配置sql以及sql封装规则
     * 3、将sql映射文件注册在配置文件中
     * 4、写代码
     *      1）、根据全局配置文件获得SqlSessionFactory
     *      2）、使用SqlSessionFactory工厂获取SqlSession实例，进行增删改查
     *          一个sqlSession代表一次会话，用完关闭
    */
    public void test() throws IOException {
        InputStream inputStream = Resources.getResourceAsStream("mybatis-config.xml");
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);

        //2、获取sqlSession实例，能直接执行已经映射的sql语句
        SqlSession openSession = sqlSessionFactory.openSession();
        try{
            //参数一：sql的唯一标识，参数二：传入sql的参数
            Employee employee = openSession.selectOne("com.company.dao.EmployeeMapper.getEmpById",1);
            System.out.println(employee);
        }finally {
            openSession.close();
        }
    }

    //接口式编程

    /**
     * sqlSession和Connection一样都是非线程安全的，每次使用都应该去获取新的对象
     * mapper接口没有实现类，但是mybatis会为接口生成一个代理对象
     *
     * 两个重要配置文件
     *      mybatis-config 全局配置文件：包含数据库连接池信息，事务管理器信息等，但也有无需配置文件进行的运行方式
     *      sql映射文件，保存了每一个sql语句的映射信息
     *      将sql抽出来，（与hibernate区别的体现，mybatis由程序员手动编写sql语句，从而实现半自动化）
     */
    @Test
    public void test2(){
        SqlSessionFactory sqlSessionFactory = getSqlSessionFactory();
        SqlSession openSession = sqlSessionFactory.openSession();
        try {
            //获取接口的实现类对象
            //会为接口自动的创建一个代理对象，代理对象去执行增删改查
            EmployeeMapper employeeMapper = openSession.getMapper(EmployeeMapper.class);
            Employee employee = employeeMapper.getEmpById(1);
            System.out.println(employeeMapper.getClass());
            System.err.println(employee);
        }finally {
            openSession.close();
        }

    }
}
