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

        //TODO - receive the board state from outside
        myAllRooms[0].getGame().getOpponentBoard().getCells()[0][0].setState(Cell.StateEnum.SHIP_PART);
        myAllRooms[0].getGame().getOpponentBoard().getCells()[0][1].setState(Cell.StateEnum.SHIP_PART);
        myAllRooms[0].getGame().getOpponentBoard().getCells()[0][2].setState(Cell.StateEnum.SHIP_PART);

        myAllRooms[0].getGame().getOpponentBoard().getCells()[3][1].setState(Cell.StateEnum.SHIP_PART);
        myAllRooms[0].getGame().getOpponentBoard().getCells()[3][2].setState(Cell.StateEnum.SHIP_PART);

        myAllRooms[0].getGame().getOpponentBoard().getCells()[4][4].setState(Cell.StateEnum.SHIP_PART);
    }

    public GameRoom[] getMyAllRooms() {
        return myAllRooms;
    }
    public void setMyAllRooms(GameRoom[] myAllRooms) {
        this.myAllRooms = myAllRooms;
    }
}
