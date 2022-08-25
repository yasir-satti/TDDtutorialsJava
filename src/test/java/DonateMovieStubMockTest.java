import DonateMovie.LibraryStubMock;
import DonateMovie.MovieInfo;
import DonateMovie.MovieStubMock;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DonateMovieStubMockTest {

    @Test
    public void donateMovieAddedToCatalogueWithImdbInfo(){
        String ImdbId = "tt12345";
        String title = "The Abyss";
        int year = 1989;
        MovieInfo movieInfo = new StubMovieInfo(title, year);
        LibraryStubMock libraryStubMock = new LibraryStubMock(movieInfo);
        libraryStubMock.donate(ImdbId);
        MovieStubMock movieStubMock = libraryStubMock.findMovie(ImdbId);
        assertEquals(title, movieStubMock.getTitle());
        assertEquals(year, movieStubMock.getYear());
    }

    private class StubMovieInfo implements MovieInfo {
        private final String title;
        private final int year;

        public StubMovieInfo(String title, int year) {
            this.title = title;
            this.year = year;
        }

        public Map<String, String> fetch(String imdbId){
            Map<String, String> info = new HashMap<>();
            info.put("title", title);
            info.put("year", Integer.toString(year));
            return info;
        }
    }
}
