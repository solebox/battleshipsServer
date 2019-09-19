package dto;

import org.springframework.stereotype.Component;

@Component
public class Cell {

    public static enum StateEnum {EMPTY, SHIP_PART, BOMBED, BOMBED_SHIP_PART}
    public static StateEnum DEFAULT_STATE = StateEnum.EMPTY;
    private StateEnum state;

    public Cell(){
        this.setState(DEFAULT_STATE);
    }

    public Cell(StateEnum state) {
        this.setState(state);
    }

    public StateEnum getState() {
        return state;
    }

    public void setState(StateEnum state) {
        this.state = state;
    }



}
