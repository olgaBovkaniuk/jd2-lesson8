package by.pvt.service;

import by.pvt.dao.SystemUsersMapper;
import by.pvt.dto.SystemUsers;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SystemUsersService {
    private static Logger log = Logger.getLogger(SystemUsersService.class.getName());

    SqlSessionFactory sqlSessionFactory;

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
        SqlSession sqlSession = sqlSessionFactory
                .openSession();

        int result = sqlSession
                .getMapper(SystemUsersMapper.class)
                .insert(systemUser);

        sqlSession.commit();
        sqlSession.close();
        log.info("Added new systemUser with result = " + result);
    }

    public void update(SystemUsers systemUser) {
        SqlSession sqlSession = sqlSessionFactory
                .openSession();

        sqlSession
                .getMapper(SystemUsersMapper.class)
                .updateByPrimaryKey(systemUser);

        sqlSession.commit();
        sqlSession.close();
        log.info("Updated systemUser with id = " + systemUser.getId());
    }

    public void delete(Integer id) {
        SqlSession sqlSession = sqlSessionFactory
                .openSession();

        sqlSession
                .getMapper(SystemUsersMapper.class)
                .deleteByPrimaryKey(id);
        sqlSession.commit();
        sqlSession.close();
        log.info("Deleted systemUser with id = " + id);
    }

    public static void main(String[] args) {
        SystemUsers systemUsers = new SystemUsers();
        systemUsers.setId(2);
        systemUsers.setUsername("User2");
        systemUsers.setActive(false);
        systemUsers.setDateofbirth(new Date());

        new SystemUsersService().add(systemUsers);

        SystemUsersService service = new SystemUsersService();
        service
                .getSystemUsers()
                .forEach(user ->
                        System.out.println(user.getId() + " "
                                + user.getUsername() + " "
                                + user.getActive()));

        systemUsers.setId(2);
        systemUsers.setActive(true);
        systemUsers.setUsername("User2-3");
        systemUsers.setDateofbirth(new Date());
        service.update(systemUsers);

        service
                .getSystemUsers()
                .forEach(user ->
                        System.out.println(user.getId() + " "
                                + user.getUsername() + " "
                                + user.getActive()));

        service.delete(2);

        service
                .getSystemUsers()
                .forEach(user ->
                        System.out.println(user.getId() + " "
                                + user.getUsername() + " "
                                + user.getActive()));
    }
}
