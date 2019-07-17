package by.pvt.service;

import by.pvt.dto.SystemUsers;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.dbunit.Assertion;
import org.dbunit.DBTestCase;
import org.dbunit.PropertiesBasedJdbcDatabaseTester;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.ITable;
import org.dbunit.dataset.filter.DefaultColumnFilter;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.dbunit.operation.DatabaseOperation;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.Reader;
import java.util.Date;
import java.util.Properties;

public class SystemUsersServiceTest extends DBTestCase {

    private Properties properties;
    private SqlSessionFactory sqlSessionFactory;
    private SystemUsersService systemUsersService;

    public SystemUsersServiceTest(String name) {
        super(name);
        properties = new Properties();
        try {
            properties.load(Thread.currentThread().getContextClassLoader().getResourceAsStream("db.config.properties"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.setProperty(PropertiesBasedJdbcDatabaseTester.DBUNIT_DRIVER_CLASS, properties.getProperty("db.driver"));
        System.setProperty(PropertiesBasedJdbcDatabaseTester.DBUNIT_CONNECTION_URL, properties.getProperty("db.url"));
        System.setProperty(PropertiesBasedJdbcDatabaseTester.DBUNIT_USERNAME, properties.getProperty("db.username"));
        System.setProperty(PropertiesBasedJdbcDatabaseTester.DBUNIT_PASSWORD, properties.getProperty("db.password"));
        System.setProperty(PropertiesBasedJdbcDatabaseTester.DBUNIT_SCHEMA, "");
    }

    @Override
    protected IDataSet getDataSet() throws Exception {
        return new FlatXmlDataSetBuilder().build(SystemUsersServiceTest.class.getResourceAsStream("system_users_TestDataSet.xml"));
    }

    @Override
    @Before
    protected void setUp() throws Exception {
        try (Reader reader = Resources.getResourceAsReader("by/pvt/service/mybatis-config-test.xml")) {
            sqlSessionFactory = new SqlSessionFactoryBuilder().build(reader);
        }
        systemUsersService = new SystemUsersService();
        systemUsersService.sqlSessionFactory = sqlSessionFactory;
    }

    @Test
    public void testListSystemUsers() throws Exception {
        //when
        IDataSet databaseDataSet = getConnection().createDataSet();
        ITable actualTable = databaseDataSet.getTable("system_users");

        systemUsersService.getSystemUsers().stream().forEach(u -> System.out.println(u.getId() + " " + u.getUsername()));

        int count = systemUsersService.getSystemUsers().size();

        //then
        IDataSet expectedDataSet = new FlatXmlDataSetBuilder().build(SystemUsersServiceTest.class.getResourceAsStream("system_users_TestDataSet_Expected.xml"));
        ITable expectedTable = expectedDataSet.getTable("system_users");
        ITable filteredActualTable = DefaultColumnFilter.includedColumnsTable(actualTable, expectedTable.getTableMetaData().getColumns());

        Assertion.assertEquals(expectedTable, filteredActualTable);
        Assert.assertEquals(5, count);
    }

    @Test
    public void testAdd() {
        // given
        SystemUsers systemUser = new SystemUsers();
        systemUser.setId(7);
        systemUser.setUsername("User7");
        systemUser.setActive(true);
        systemUser.setDateofbirth(new Date());

        // when
        systemUsersService.add(systemUser);

        // then
        Assert.assertTrue(systemUsersService.getSystemUsers().stream().anyMatch(user -> user.getId() == 7));
    }

    @Test
    public void testUpdate() {
        //given
        SystemUsers user = new SystemUsers();
        user.setId(5);
        user.setUsername("User10");
        user.setActive(false);

        //when
        systemUsersService.update(user);

        //then
        SystemUsers updatedUser = systemUsersService.getSystemUsers().stream()
                .filter(u -> u.getId().equals(user.getId()))
                .findFirst()
                .orElse(null);

        Assert.assertNotNull(updatedUser);
        Assert.assertEquals(user.getUsername(), updatedUser.getUsername());
        Assert.assertEquals(user.getActive(), updatedUser.getActive());
    }

    @Test
    public void testDelete() {
        //given
        int id = 3;

        //when
        systemUsersService.delete(id);

        //then
        Assert.assertTrue(systemUsersService.getSystemUsers().stream().noneMatch(user -> user.getId() == id));
    }

    @Override
    protected DatabaseOperation getTearDownOperation() throws Exception {
        return DatabaseOperation.CLEAN_INSERT;
    }
}