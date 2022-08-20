import DonateMovie.Library;
import DonateMovie.Movie;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class DonateMovieTest {

    @Test
    public void donateMovie(){
        Library library = new Library();
        Movie movie = new Movie();
        library.donate(movie);
        assertTrue(library.getCatalogue().contains(movie));
    }
}