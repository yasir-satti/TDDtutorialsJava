# Practicing TDD with Java

TDD makes you:

1. Write classes/methods/getters/setters in the test, but obviously fails because no code is written yet. So to make the test pass it forces you to go and write the code for these elements. So it forces you to write minimul code.
2. Make design decisions as you make your test pass
3. Write asserstions that will fail and error message explains why it failed. This provides the hint for you what code is needed to make the test pass

TDD golden rule

"We do not write a code unless the test requires it"

Writing a test and it fails !

Start adding methods and other classes as we attempt to make the tests pass

## 1: The 3 Steps of TDD ( Shopping basket totals )
### Test 1 & 2

Wrote test 1 for an empty shopping basket and it fails !

Wrote code and made it pass

Write test 2 for a basket with 1 item and it fails 

Wrote code and made it pass

### Test 3

Wrote test 3 for basket with two items and failed

Wrote code and made it pass

### Refactor

Duplicated code in every test when creating a new basket

exctracted into a private method and passed basket items as paramemter

### Test 4

Wrote test for item with quantity 2 and failed

Wrote code and made it pass. The Shopping.ShoppingBasket.getTotal() was not taking into account the quantity value

### Refactor

Shopping.ShoppingBasket.getTotal() has un-encapsulated item ( item.getUnitPrice() * item.getQuantity() ). So:

1. extract into private method
2. But this private method can be moved into Shopping.Item class
3. But also methods getUnitPrice() and getQuantity() are no longer referenced so refactor > inline method which replaces them with unitPrice and quantity respectively


So overall:

1. design is easy to understand
2. removed duplication
3. solved dependencies issues
4. hidden details from Shopping.ShoppingBasket class

## 2: Good TDD Habits ( Donate Movie )

- Write the Assert first and go back to write the setup
- Run the test before you write the code to make sure it fails because of wrong answer and not an exception
- TDD works better when a test has ONE reason to fail

### Assert first ! then declare your references

1. assertion first, so referencing objects that does not exists yet

```java
assertTrue(library.getCatalogue().contains(movie));
```


2. work backward from the refrence to the objects declarations

Start declaring objects:
- Library class, then
- getCatalogue() method, then
- Movie class, then
- movie instance

3. Declare objects
```java
Library library = new Library();
Movie movie = new Movie();
```
4. action !
```java
library.donate(movie);
```

woops! method donate() is referenced but not delared. So declare it. Now you can action !

5. Now assert !
```java
assertTrue(library.getCatalogue().contains(movie));
```

still fails, but will come to that...


### Run the test and see it fail because the answer is wrong before it passes

1. run the test
2. It fails with NullPpointerException
```java
java.lang.NullPointerException: Cannot invoke "java.util.Collection.contains(Object)" because the return value of "DonateMovie.Library.getCatalogue()" is null
```

This is because library.getCatalogue() is returning NULL
```java
public class Library {
    public Collection<Movie> getCatalogue() {
        return null;
    }

    public void donate(Movie movie) {
    }
}
```

So replace NULL with new ArrayList<> which is the wrong answer expected by the assertion
```java
public class Library {
    public Collection<Movie> getCatalogue() {
        return new ArrayList<>();
    }

    public void donate(Movie movie) {
    }
}
```

now run the test and we get...
```java
org.opentest4j.AssertionFailedError: 
Expected :true
Actual   :false
```

So the assertion now fails for the wrong answer. In this way we tested the assertion failes correctly.

### Make the test pass

So we need to do the following:
- library.getCatalogue() to return a catalogue
- librayr.donate() to add movie to the catalogue

so 
```java
public class Library {

    private Collection<Movie> catalogue = new ArrayList<>();

    public Collection<Movie> getCatalogue() {
        return catalogue;
    }

    public void donate(Movie movie) {
        catalogue.add(movie);
    }
}
```

Test passes !

## Refactoring

The assertion has "message chain" where we are navigating from one object to another in the chain. So we un-encapsulated this list.
```java
library.getCatalogue().contains(movie)
```
So let us refactore:
- encapsulate by moving to private method
```java
@Test
public void donateMovie(){
Library library = new Library();
Movie movie = new Movie();
library.donate(movie);
assertTrue(contains(library, movie));
}

private boolean contains(Library library, Movie movie) {
    return library.getCatalogue().contains(movie);
}
```

run test to make sure it still passes !

- Move it into Library class
```java
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
```
run test to make sure it still passes !

## Effecient test assertion implementation

Think of the assertion
```java
assertEquals(1, movie.getCopies());
```

So we could just hadcode the value in movie.getCopies()
```java
public class Movie {

    public int getCopies() {
        return 1;
    }
}
```

But this is not efficent f you want to go through a number of test cases. So a better implementation is to parametrise the copies in Movie class
```java
public class Movie {
    private int copies;

    public int getCopies() {
        return copies;
    }

    public void addCopy() {
        copies++;
    }
}
```
and add line movie.addCopy() when the book donation happens

```java
public class Library {

    private Collection<Movie> catalogue = new ArrayList<>();

    public Collection<Movie> getCatalogue() {
        return catalogue;
    }

    public void donate(Movie movie) {
        catalogue.add(movie);
        movie.addCopy();
    }

    public boolean contains(Movie movie) {
        return getMovieCollection().contains(movie);
    }

    private Collection<Movie> getMovieCollection() {
        return getCatalogue();
    }
}
```
run test to make sure it still passes !

Also method library.getCatalogue()
 is no longer referenced
```java
public class Library {

    private Collection<Movie> catalogue = new ArrayList<>();

    public Collection<Movie> getCatalogue() {
        return catalogue;
    }

    public void donate(Movie movie) {
        catalogue.add(movie);
        movie.addCopy();
    }

    public boolean contains(Movie movie) {
        return getCatalogue().contains(movie);
    }
}
```
so we can remove it
```java
public class Library {

    private Collection<Movie> catalogue = new ArrayList<>();

    public void donate(Movie movie) {
        catalogue.add(movie);
        movie.addCopy();
    }

    public boolean contains(Movie movie) {
        return catalogue.contains(movie);
    }
}
```
run test to make sure it still passes !

## ONE and ONLY one reason for a test to fail

The test below can fail for either of the assetions
```java
@Test
    public void donateMovie(){
        Library library = new Library();
        Movie movie = new Movie();
        library.donate(movie);
        assertTrue(library.contains(movie));
        assertEquals(1, movie.getCopies());
    }
```
This is not good! we need to have control over each test and know by DESIGN it will fail for one reason ONLY. So refactor:

- declare library and movie in test class constructor
```java
public DonateMovieTest() {
        library = new Library();
        movie = new Movie();
        }
```
move the second assertion into a separate test. Also rename first test to reflect what it is testing
```java
public class DonateMovieTest {

    private final Library library;
    private final Movie movie;

    public DonateMovieTest() {
        library = new Library();
        movie = new Movie();
    }

    @Test
    public void movieAddedToCatalogue(){
        library.donate(movie);
        assertTrue(library.contains(movie));
    }

    @Test
    public void rentalCopyAdded(){
        library.donate(movie);
        assertEquals(1, movie.getCopies());
    }

}
```
