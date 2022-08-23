import Rover.Rover;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MarsRoverTest {

    @Test
    public void TurnRightNorthToEast(){
        Rover rover = new Rover("N");
        rover.go("R");
        assertEquals("E", rover.getFacing());
    }

    @Test
    public void TurnRightEastToSouth(){
        Rover rover = new Rover("E");
        rover.go("R");
        assertEquals("S", rover.getFacing());
    }

    @Test
    public void TurnRightSouthToWest(){
        Rover rover = new Rover("S");
        rover.go("R");
        assertEquals("W", rover.getFacing());
    }
}
