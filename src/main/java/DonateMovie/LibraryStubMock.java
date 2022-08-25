package DonateMovie;

import java.util.HashMap;
import java.util.Map;

public class LibraryStubMock {
    private final MovieInfo movieInfo;
    private final Map<String, MovieStubMock> catalogue = new HashMap<String, MovieStubMock>();

    public LibraryStubMock(MovieInfo movieInfo) {
        this.movieInfo = movieInfo;
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
    }
}
