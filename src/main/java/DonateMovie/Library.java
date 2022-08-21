package DonateMovie;

import java.util.ArrayList;
import java.util.Collection;

public class Library {

    private Collection<Movie> catalogue = new ArrayList<>();

    public Collection<Movie> getCatalogue() {
        return catalogue;
    }

    public void donate(Movie movie) {
        catalogue.add(movie);
    }

    public boolean contains(Movie movie) {
        return getCatalogue().contains(movie);
    }
}
