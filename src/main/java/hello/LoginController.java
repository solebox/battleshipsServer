package hello;
import java.sql.*;
import java.util.List;

import dto.Player;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.web.bind.annotation.*;

import javax.sql.DataSource;
import javax.sql.RowSet;


@RestController
public class LoginController {
    // JDBC driver name and database URL
    static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
    static final String DB_URL = "jdbc:mysql://localhost/USERS?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC";
    static final String USER = "oren";
    static final String PASS = "1234";
    Connection conn = null;
    Statement stmt = null;
    static String username, password, email;
    @Autowired GameDao battleShips;
    @Autowired PlayerDao players;


    @RequestMapping("/signUp")
    public String signUp(JdbcTemplate jt, @RequestBody String username, @RequestParam String password, @RequestParam String email) {

        try{

            //STEP 3: Open a connection
            System.out.println("Connecting to database...");
            conn = DriverManager.getConnection(DB_URL,USER,PASS);

            //STEP 4: Execute a query
            System.out.println("Creating statement...");
            stmt = conn.createStatement();
            String sql;

            sql = "SELECT username FROM Users WHERE username='" + username +"'";
//            RowSet rs = stmt.executeQuery(sql);
            RowSet rs = (RowSet) jt.queryForRowSet(sql);

            //if user doesn't exist
            if (!rs.next()) {
                //insert to table
                sql = "INSERT INTO Users VALUES (" + "'" + username + "'" + ", " + "'" + password + "'" + ", " + "'" + email +"'" + ", " + "'0')";
                stmt.executeUpdate(sql);
                rs.close();
                stmt.close();
                conn.close();
                return "completed";
            }

            else return "user_exists";

            //STEP 6: Clean-up environment
        }catch(SQLException se){
            //Handle errors for JDBC
            se.printStackTrace();
        }catch(Exception e){
            //Handle errors for Class.forName
            e.printStackTrace();
        }finally{
            //finally block used to close resources
            try{
                if(stmt!=null)
                    stmt.close();
            }catch(SQLException se2){
            }// nothing we can do
            try{
                if(conn!=null)
                    conn.close();
            }catch(SQLException se){
                se.printStackTrace();
            }//end finally try
        }//end try

       return "user_exists";
    }

    @RequestMapping("/signIn")
    public String signIn(@RequestParam String username, @RequestParam String password) {
        String result = "fail";
        try{
            boolean is_logged_in = this.players.isPasswordCorrect(username, password);

            if (!is_logged_in) return "wrong_password";
            else {
                 result = "signed_in";
                return result;
            }

            //STEP 6: Clean-up environment

        }catch(Exception e){
            //Handle errors for Class.forName
            result = ""+ e;
            e.printStackTrace();
        }finally{
            //finally block used to close resources
            try{
                if(stmt!=null)
                    stmt.close();
            }catch(SQLException se2){
            }// nothing we can do
            try{
                if(conn!=null)
                    conn.close();
            }catch(SQLException se){
                se.printStackTrace();
            }//end finally try
        }//end try

        return result;
    }

    @RequestMapping("/getLobbyData")
    public String getLobbyData(@RequestParam String user) {
        try{

            //STEP 3: Open a connection
            System.out.println("Connecting to database...");
            conn = DriverManager.getConnection(DB_URL,USER,PASS);

            //STEP 4: Execute a query (this is an example)
            System.out.println("Creating statement...");
            stmt = conn.createStatement();

            String sql = "SELECT score FROM Users WHERE username='" + user +"'";
            ResultSet rs = stmt.executeQuery(sql);
            rs.next();
            String score = String.valueOf(rs.getInt("score"));

            sql = "SELECT username, score FROM Users ORDER BY score DESC";
            rs = stmt.executeQuery(sql);

            String[] top_data = new String[6];
            int i = 0;
            while(rs.next() && i < 5){
                top_data[i] = rs.getString("username");
                top_data[i+1] = String.valueOf(rs.getInt("score"));
                i+=2;
            }

            String[] room_state = new String[3];
            String[] room_name = new String[3];

            for (i = 0; i < 3; i++) {
                room_state[i] = MyServerApplication.getInstance().getMyAllRooms()[i].getCurrentState().toString();
                room_name[i] = MyServerApplication.getInstance().getMyAllRooms()[i].getName();
            }

            String top_data_string = String.join("_", top_data);
            return score + "_" + room_state[0] + "_" + room_state[1] + "_" + room_state[2] + "_" +room_name[0] + "_" + room_name[1] + "_" + room_name[2] + "_" + top_data_string;

            //STEP 6: Clean-up environment
        }catch(SQLException se){
            //Handle errors for JDBC
            se.printStackTrace();
        }catch(Exception e){
            //Handle errors for Class.forName
            e.printStackTrace();
        }finally{
            //finally block used to close resources
            try{
                if(stmt!=null)
                    stmt.close();
            }catch(SQLException se2){
            }// nothing we can do
            try{
                if(conn!=null)
                    conn.close();
            }catch(SQLException se){
                se.printStackTrace();
            }//end finally try
        }//end try

        return null;
    }
    
}
