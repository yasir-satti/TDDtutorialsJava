package DonateMovie;

import java.util.HashMap;
import java.util.Map;

public class LibraryStubMock {
    private final MovieInfo movieInfo;
    private final Map<String, MovieStubMock> catalogue = new HashMap<String, MovieStubMock>();
    private final Emailserver emailserver;

    public LibraryStubMock(MovieInfo movieInfo, Emailserver emailserver) {
        this.movieInfo = movieInfo;
        this.emailserver = emailserver;
    }

    public MovieStubMock findMovie(String imdbId) {
        return catalogue.get(imdbId);
    }

    public void donate(String imdbId) {
        Map<String, String> info = movieInfo.fetch(imdbId);
        catalogue.put(
                imdbId,
                new MovieStubMock(
                        info.get("title"),
                        Integer.parseInt(info.get("year")))
        );
        emailserver.sendEmail(
                "New Movie",
                "All members",
                new String[]{
                        info.get("title"), info.get("year")});
    }
}
