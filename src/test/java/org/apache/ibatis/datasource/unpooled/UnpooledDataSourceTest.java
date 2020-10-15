
import java.sql.*;
import	java.util.Properties;/**
 *    Copyright 2009-2019 the original author or authors.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
import static org.junit.jupiter.api.Assertions.*;

import java.net.URL;
import java.net.URLClassLoader;
import java.util.Enumeration;

import org.apache.ibatis.datasource.pooled.PooledDataSourceFactory;
import org.apache.ibatis.datasource.unpooled.UnpooledDataSource;
import org.apache.ibatis.datasource.unpooled.UnpooledDataSourceFactory;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import javax.sql.DataSource;

class UnpooledDataSourceTest {

  @Test
  void shouldNotRegisterTheSameDriverMultipleTimes() throws Exception {
    // https://code.google.com/p/mybatis/issues/detail?id=430
    UnpooledDataSource dataSource = null;
    dataSource = new UnpooledDataSource("org.hsqldb.jdbcDriver", "jdbc:hsqldb:mem:multipledrivers", "sa", "");
    dataSource.getConnection().close();
    int before = countRegisteredDrivers();
    dataSource = new UnpooledDataSource("org.hsqldb.jdbcDriver", "jdbc:hsqldb:mem:multipledrivers", "sa", "");
    dataSource.getConnection().close();
    assertEquals(before, countRegisteredDrivers());
  }

  @Disabled("Requires MySQL server and a driver.")
  @Test
  void shouldRegisterDynamicallyLoadedDriver() throws Exception {
    int before = countRegisteredDrivers();
    ClassLoader driverClassLoader = null;
    UnpooledDataSource dataSource = null;
    driverClassLoader = new URLClassLoader(new URL[] { new URL("jar:file:/PATH_TO/mysql-connector-java-5.1.25.jar!/") });
    dataSource = new UnpooledDataSource(driverClassLoader, "com.mysql.jdbc.Driver", "jdbc:mysql://127.0.0.1/test", "root", "");
    dataSource.getConnection().close();
    assertEquals(before + 1, countRegisteredDrivers());
    driverClassLoader = new URLClassLoader(new URL[] { new URL("jar:file:/PATH_TO/mysql-connector-java-5.1.25.jar!/") });
    dataSource = new UnpooledDataSource(driverClassLoader, "com.mysql.jdbc.Driver", "jdbc:mysql://127.0.0.1/test", "root", "");
    dataSource.getConnection().close();
    assertEquals(before + 1, countRegisteredDrivers());
  }

  int countRegisteredDrivers() {
    Enumeration<Driver> drivers = DriverManager.getDrivers();
    int count = 0;
    while (drivers.hasMoreElements()) {
      drivers.nextElement();
      count++;
    }
    return count;
  }

  @Test
  void unPooledDataSourceTest() {
    UnpooledDataSourceFactory factory = new UnpooledDataSourceFactory();
    Properties properties = new Properties();
    properties.put("driver", "com.mysql.jdbc.Driver");
    properties.put("url", "jdbc:mysql://192.168.2.27:3307/product");
    properties.put("username", "product_rw");
    properties.put("password", "GOhSyRsrz09ZH4QU");
    factory.setProperties(properties);
    DataSource dataSource = factory.getDataSource();
    Connection connection = null;
    try {
      connection = dataSource.getConnection();
      PreparedStatement preparedStatement = connection.prepareStatement("select * from p_sku order by id desc limit 1");
      ResultSet resultSet = preparedStatement.executeQuery();
      while (resultSet.next()) {
        System.out.println(resultSet.getString(1));
        System.out.println(resultSet.getString(4));
      }
    } catch (Exception e) {
      System.out.println(e);
    } finally {
      try {
        connection.close();
      } catch (Exception e) {

      }
    }
  }

  @Test
  void pooledDataSourceTest() {
    PooledDataSourceFactory factory = new PooledDataSourceFactory();
    Properties properties = new Properties();
    properties.put("driver", "com.mysql.jdbc.Driver");
    properties.put("url", "jdbc:mysql://192.168.2.27:3307/product");
    properties.put("username", "product_rw");
    properties.put("password", "GOhSyRsrz09ZH4QU");
    factory.setProperties(properties);
    factory.setProperties(properties);
    DataSource dataSource = factory.getDataSource();
    Connection conn = null;
    try {
      conn = dataSource.getConnection();
//      PreparedStatement preparedStatement = conn.prepareStatement("select * from p_sku order by id desc limit 1");
//      ResultSet resultSet = preparedStatement.executeQuery();
//      while (resultSet.next()) {
//        System.out.println(resultSet.getString(1));
//        System.out.println(resultSet.getString(4));
//      }
//      conn.close();
//      conn = dataSource.getConnection();
      for (int i = 0; i < 10; i++) {
        dataSource.getConnection();
      }
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      try {
        conn.close();
      } catch (Exception e) {

      }
    }
  }

}
