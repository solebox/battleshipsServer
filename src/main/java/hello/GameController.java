package hello;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.util.concurrent.TimeUnit;

//import com.google.gson.Gson;
import dto.Board;
import dto.Game;
import dto.GameRoom;
import dto.Player;
import org.springframework.web.bind.annotation.*;

import static java.net.URLDecoder.decode;

@RestController
public class GameController {
    // JDBC driver name and database URL
    static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
    static final String DB_URL = "jdbc:mysql://localhost/USERS?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC";
    static final String USER = "oren";
    static final String PASS = "1234";
    Connection conn = null;
    Statement stmt = null;
    static int room;
    static String username, password, email;
    static String requestedBoard;

    @RequestMapping("/game")
    public Board index(Board board) {
        return board;
    }
    @GetMapping(value = "/gameroom/{roomId}")
    public @ResponseBody GameRoom getGameRoom(@PathVariable Integer roomId) {

        if (MyServerApplication.getInstance().getMyAllRooms()!=null &&roomId <MyServerApplication.getInstance().getMyAllRooms().length )
            return MyServerApplication.getInstance().getMyAllRooms()[roomId];
        else
            return null;

    }

    @RequestMapping("/getBoard")
    public Board getBoard(@RequestBody String message) throws UnsupportedEncodingException {
        Board board, boardToSend = null;
        if (message.charAt(message.length()-1)=='=') message = message.replace(message.substring(message.length()-1), "");
        String msg[] = message.split("_");
        if (msg!=null && msg.length==3) {
            username = msg[0];
            room = Integer.valueOf(msg[1]);
            requestedBoard = msg[2];
        }
        board = MyServerApplication.getInstance().getMyAllRooms()[room].getGame().getBoard();
        if (requestedBoard.equals("myBoard")) {
            if (board.getBoardOwner().equals(username)) boardToSend = board;
            else boardToSend = MyServerApplication.getInstance().getMyAllRooms()[room].getGame().getOpponentBoard();
        }
        else if (requestedBoard.equals("opBoard")) {
            if (board.getBoardOwner().equals(username)) boardToSend = MyServerApplication.getInstance().getMyAllRooms()[room].getGame().getOpponentBoard();
            else boardToSend = board;
        }

        return boardToSend;
    }

    @RequestMapping("/sendBoard")
    public String sendBoard(@RequestBody Board received_board) throws UnsupportedEncodingException, InterruptedException {

        int room = received_board.getBoardRoom();
        String owner = received_board.getBoardOwner();
        String sender = received_board.getBoardSender();

        Board board1 = MyServerApplication.getInstance().getMyAllRooms()[room].getGame().getBoard();
        Board board2 = MyServerApplication.getInstance().getMyAllRooms()[room].getGame().getOpponentBoard();

        if (board1.getState().equals(Board.BoardStateEnum.EMPTY)) MyServerApplication.getInstance().getMyAllRooms()[room].getGame().setBoard(received_board);
        else if (board2.getState().equals(Board.BoardStateEnum.EMPTY)) MyServerApplication.getInstance().getMyAllRooms()[room].getGame().setOpponentBoard(received_board);
        else if (received_board.getBoardOwner().equals(board1.getBoardOwner())) MyServerApplication.getInstance().getMyAllRooms()[room].getGame().setBoard(received_board);
        else if (received_board.getBoardOwner().equals(board2.getBoardOwner())) MyServerApplication.getInstance().getMyAllRooms()[room].getGame().setOpponentBoard(received_board);

        if (received_board.noShips() && !MyServerApplication.getInstance().getMyAllRooms()[room].getGame().getState().equals(Game.GameState.GAME_OVER))
        {
            MyServerApplication.getInstance().getMyAllRooms()[room].getGame().setState(Game.GameState.GAME_OVER);
            MyServerApplication.getInstance().getMyAllRooms()[room].getGame().setWinner(sender);

            try{
                //STEP 3: Open a connection
                System.out.println("Connecting to database...");
                conn = DriverManager.getConnection(DB_URL,USER,PASS);

                //STEP 4: Execute a query
                System.out.println("Creating statement...");
                stmt = conn.createStatement();
                String sql;
                sql = "UPDATE Users SET score=score+50 WHERE username='" + sender +"';";
                stmt.executeUpdate(sql);

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

        }
        else if (!owner.equals(sender)) MyServerApplication.getInstance().getMyAllRooms()[room].getGame().setTurn(owner);
        return "updated";
    }

    @RequestMapping("/checkStatus")
    public String checkStatus(@RequestBody String message) throws Exception {
        if (message.charAt(message.length()-1)=='=') message = message.replace(message.substring(message.length()-1), "");
        String msg[] = message.split("_");
        if (msg!=null && msg.length==2) {
            username = msg[0];
            room = Integer.valueOf(msg[1]);
        }

        //only one player --> waiting for sec player
        if (MyServerApplication.getInstance().getMyAllRooms()[room].getGame().getPlayers().size() == 1) return "No 2nd player";

        //one board isn't ready --> waiting for sec player's board
        if (!MyServerApplication.getInstance().getMyAllRooms()[room].getGame().getOpponentBoard().getState().equals(Board.BoardStateEnum.READY) || !MyServerApplication.getInstance().getMyAllRooms()[room].getGame().getBoard().getState().equals(Board.BoardStateEnum.READY)) return "waiting for player2's board";

        if (MyServerApplication.getInstance().getMyAllRooms()[room].getGame().getState().equals(Game.GameState.GAME_OVER)) {
            if (MyServerApplication.getInstance().getMyAllRooms()[room].getGame().getWinner().equals(username)) {
                MyServerApplication.getInstance().getMyAllRooms()[room].getGame().winner_knows = true;
                if (MyServerApplication.getInstance().getMyAllRooms()[room].getGame().loser_knows) MyServerApplication.getInstance().getMyAllRooms()[room].resetRoom();
                return "You win!";
            }
            else if (!MyServerApplication.getInstance().getMyAllRooms()[room].getGame().getWinner().equals("none")) {
                MyServerApplication.getInstance().getMyAllRooms()[room].getGame().loser_knows = true;
                if (MyServerApplication.getInstance().getMyAllRooms()[room].getGame().winner_knows) MyServerApplication.getInstance().getMyAllRooms()[room].resetRoom();
                return "Player2 wins!";
            }
        }
        if (MyServerApplication.getInstance().getMyAllRooms()[room].getGame().getTurn().equals(username)) return "YourTurn";
        else return "OpTurn";
    }


    @RequestMapping("/joinRoom")
    public String joinRoom(@RequestBody String message) throws Exception {
        if (message.charAt(message.length()-1)=='=') message = message.replace(message.substring(message.length()-1), "");

        String msg[] = message.split("_");
        if (msg!=null && msg.length==2) {
            username = msg[0];
            room = Integer.valueOf(msg[1]);
        }
//        if (MyServerApplication.getInstance().getMyAllRooms()[room].getGame().getState().equals(Game.GameState.GAME_OVER)) {
//            String name = MyServerApplication.getInstance().getMyAllRooms()[room].getName();
//            MyServerApplication.getInstance().getMyAllRooms()[room] = new GameRoom(name);
//        }
        if (MyServerApplication.getInstance().getMyAllRooms()[room].getGame().getTurn().equals("none")) MyServerApplication.getInstance().getMyAllRooms()[room].getGame().setTurn(username);

        if (MyServerApplication.getInstance().getMyAllRooms()[room].getCurrentState().equals(GameRoom.GameRoomState.Full)) return "full";
        else {
            MyServerApplication.getInstance().getMyAllRooms()[room].getGame().addPlayer(new Player(username));
            MyServerApplication.getInstance().getMyAllRooms()[room].setCurrentState();
            return "joined";
        }
    }
}
