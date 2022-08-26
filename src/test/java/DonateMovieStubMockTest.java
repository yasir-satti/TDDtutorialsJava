import DonateMovie.*;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class DonateMovieStubMockTest {

    @Test
    public void donateMovieAddedToCatalogueWithImdbInfo(){
        String ImdbId = "tt12345";
        String title = "The Abyss";
        int year = 1989;
        MovieInfo movieInfo = new StubMovieInfo(title, year);
        Emailserver emailserver = mock(Emailserver.class);
        LibraryStubMock libraryStubMock = new LibraryStubMock(movieInfo, emailserver);
        libraryStubMock.donate(ImdbId);
        MovieStubMock movieStubMock = libraryStubMock.findMovie(ImdbId);
        assertEquals(title, movieStubMock.getTitle());
        assertEquals(year, movieStubMock.getYear());
    }

    @Test
    public void membersEmailedAboutDonatedtitle() {
        Emailserver emailserver = mock(Emailserver.class);
        String title = "The Abyss";
        String year = "1989";
        new LibraryStubMock(new StubMovieInfo(title, Integer.parseInt(year)), emailserver).donate("");
        verify(emailserver).sendEmail(
                "New Movie",
                "All members",
                new String[]{title, year});
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
