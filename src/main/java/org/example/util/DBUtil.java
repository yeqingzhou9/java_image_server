package org.example.util;

import com.mysql.jdbc.Connection;
import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;
import org.junit.Test;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DBUtil {
    //封装数据库连接池（双重校验锁的线程安全的单例模式）
    //1.volatile修饰的静态变量
    //2.两个if判断，中间进行synchronized加锁操作保证线程安全
    private static volatile DataSource DS;

    private static DataSource getDataSource(){
        if(DS==null){
            synchronized (DBUtil.class){
                if(DS==null){
                    MysqlDataSource dataSource=new MysqlDataSource();
                    dataSource.setURL("jdbc:mysql://localhost:3306/image_system");
                    dataSource.setUser("root");
                    dataSource.setPassword("123456");
                    dataSource.setUseSSL(false);
                    dataSource.setCharacterEncoding("utf8");
                    DS=dataSource;
                }
            }
        }
        return DS;
    }

    public static Connection getConnection(){
        try {
            return (Connection) getDataSource().getConnection();
        } catch (SQLException e) {
            throw new RuntimeException("获取数据库连接失败",e);
        }
    }

    @Test
    public void testGetConnection(){
        System.out.println(getConnection());
    }

    public static void close(Connection c, Statement s, ResultSet rs) {//java.sql包下的
        try {
            if(rs!=null) rs.close();
            if(s!=null) s.close();
            if(c!=null) c.close();
        } catch (SQLException e) {
            throw new RuntimeException("释放数据库资源出错",e);
        }
    }
}
