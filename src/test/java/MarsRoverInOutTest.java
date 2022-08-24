import Rover.RoverInOut;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class MarsRoverInOutTest {
    @ParameterizedTest
    @CsvSource({
            "N, E",
            "E, S",
            "S, W",
            "W, N"
    })
    public void TurnRightClockwise(String startsFacing, String endsFacing){
        RoverInOut roverInOut = new RoverInOut(startsFacing, 0, 0);
        roverInOut.right();
        assertEquals(endsFacing, roverInOut.getFacing());
    }

    @ParameterizedTest
    @CsvSource({
            "N, W",
            "W, S",
            "S, E",
            "E, N"
    })
    public void TurnLeftAntiClockwise(String startsFacing, String endsFacing){
        RoverInOut rover = new RoverInOut(startsFacing, 0, 0);
        rover.left();
        assertEquals(endsFacing, rover.getFacing());
    }

    @ParameterizedTest
    @CsvSource({
            "F,N,5,6",
            "B,N,5,4",
            "R,E,5,5",
            "L,W,5,5"
    })
    public void InstructionToCommand(Character instruction, String endsFacing, int endX, int endY){
        RoverInOut roverInOut = new RoverInOut("N", 5, 5);
        roverInOut.mapInstructionToCommand(instruction).run();
        assertEquals(endsFacing, roverInOut.getFacing());
        assertArrayEquals(new int[]{endX, endY}, roverInOut.getPosition());
    }

    @Test
    public void executesSequenceOfInsttructions(){
        RoverInOut roverInOut = new RoverInOut("N", 5, 5);
        roverInOut.go("RFF");
        assertEquals("E", roverInOut.getFacing());
        assertArrayEquals(new int[]{7, 5}, roverInOut.getPosition());
    }
}
