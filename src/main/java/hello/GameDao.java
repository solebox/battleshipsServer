package hello;

import dto.Game;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Repository
public class GameDao {
    private JdbcTemplate jdbcTemplate;

    @Autowired
    public GameDao(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public boolean updateScore(String username){
        String sql;
        sql = "UPDATE Users SET score=score+50 WHERE username=?";
        boolean success = false;
        try{
                int rows_effected =  jdbcTemplate.update(sql, username);
                success =  true;
        }catch(Exception e){
            e.printStackTrace();
        }
        return success;
    }

    public JdbcTemplate getTemplate(){
        return this.jdbcTemplate;
    }
}