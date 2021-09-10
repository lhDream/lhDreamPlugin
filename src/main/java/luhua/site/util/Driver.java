package luhua.site.util;

import luhua.site.Application;

import java.sql.Connection;
import java.sql.DriverManager;

/**
 * @description: 数据库驱动
 * @author: lhDream
 * @create: 2021-09-08 17:15
 **/
public class Driver {
    /**
     * 数据库地址
     */
    private final String URL;
    /**
     * 数据库驱动
     */
    private final String DRIVER;
    /**
     * 用户名
     */
    private final String USERNAME;
    /**
     * 密码
     */
    private final String PASSWORD;

    /**
     * 数据库操作对象
     */
    private static Connection con;

    private static Driver driver;

    public static void init(){

    }

    /**
     * @description: 构造数据库驱动
     * @author: lhDream
     * @create: 2021-09-08 17:15
     **/
    private Driver(String URL,String DRIVER,String USERNAME,String PASSWORD){
        this.URL = URL;
        this.DRIVER = DRIVER;
        this.USERNAME = USERNAME;
        this.PASSWORD = PASSWORD;
        try {
            Class.forName(this.DRIVER);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            Application.getLog().error("database driver init error.");
            Application.getLog().error(e.getLocalizedMessage());
        }
    }

    public static Connection getCon() throws Exception{
        if(null == con || con.isClosed()){

        }
        return null;
    }

}
