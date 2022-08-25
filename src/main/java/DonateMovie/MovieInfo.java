package DonateMovie;

import java.util.Map;

public interface MovieInfo {
    Map<String, String> fetch(String imdbId);
}
