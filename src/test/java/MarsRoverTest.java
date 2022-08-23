import Rover.Rover;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MarsRoverTest {

    @ParameterizedTest
    @CsvSource({
            "N, E",
            "E, S",
            "S, W",
            "W, N"
    })
    public void TurnRightClockwise(String startsFacing, String endsFacing){
        Rover rover = new Rover(startsFacing);
        rover.go("R");
        assertEquals(endsFacing, rover.getFacing());
    }
}
