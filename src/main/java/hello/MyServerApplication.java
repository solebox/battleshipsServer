package hello;

import dto.Cell;
import dto.GameRoom;

public class MyServerApplication {
    private static MyServerApplication instance;
    private GameRoom[] myAllRooms ;
 //   private HashMap<String,Player> players;
    public static MyServerApplication getInstance() {
        if (instance == null)
        {
            instance = new MyServerApplication();
        }
        return instance;
    }

    public MyServerApplication()
    {
        this.setMyAllRooms(new GameRoom[3]);
        myAllRooms[0] = new GameRoom("Room 0");
        myAllRooms[1] = new GameRoom("Room 1");
        myAllRooms[2] = new GameRoom("Room 2");
    }

    public GameRoom[] getMyAllRooms() {
        return myAllRooms;
    }
    public void setMyAllRooms(GameRoom[] myAllRooms) {
        this.myAllRooms = myAllRooms;
    }
}
