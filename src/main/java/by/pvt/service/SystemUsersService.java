package by.pvt.service;

import by.pvt.dao.SystemUsersMapper;
import by.pvt.dto.SystemUsers;
import by.pvt.dto.SystemUsersExample;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SystemUsersService {
    private static Logger log = Logger.getLogger(SystemUsersService.class.getName());

    private SqlSessionFactory sqlSessionFactory;

    public SystemUsersService() {
        try {
            sqlSessionFactory = new SqlSessionFactoryBuilder().build(
                    Resources.getResourceAsStream("by/pvt/service/mybatis-config.xml")
            );
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    public List<SystemUsers> getSystemUsers() {
       return sqlSessionFactory
                .openSession()
                .getMapper(SystemUsersMapper.class)
                .selectByExample(null);
    }

    public void add(SystemUsers systemUser) {
        int result = sqlSessionFactory
                .openSession()
                .getMapper(SystemUsersMapper.class)
                .insert(systemUser);
        log.info("Added new systemUser with result = " + result);
    }

    public static void main(String[] args) {
        SystemUsers systemUsers = new SystemUsers();
        systemUsers.setId(2);
        systemUsers.setUsername("User2");
        systemUsers.setActive(false);
        systemUsers.setDateofbirth(new Date());

        new SystemUsersService().add(systemUsers);

        new SystemUsersService()
                .getSystemUsers()
                .forEach(user ->
                        System.out.println(systemUsers.getId() + " "
                                + systemUsers.getUsername() +  " "
                                + systemUsers.getActive()));
    }
}
