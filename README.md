# Practicing TDD with Java

Note: testing is done with [JUnit 5](https://junit.org/junit5/docs/current/user-guide/#writing-tests)

TDD makes you:

1. Write classes/methods/getters/setters in the test, but obviously fails because no code is written yet. So to make the test pass it forces you to go and write the code for these elements. So it forces you to write minimul code.
2. Make design decisions as you make your test pass
3. Write asserstions that will fail and error message explains why it failed. This provides the hint for you what code is needed to make the test pass

TDD golden rule

"We do not write a code unless the test requires it"

Writing a test and it fails !

Start adding methods and other classes as we attempt to make the tests pass

## Contents
1. The 3 Steps of TDD
2. Good TDD Habits
3. What Tests We Should Write?
4. Duplication and Design
5. Inside out TDD
6. Scalling TDD with Stubs and Mocks

## 1: The 3 Steps of TDD ( Shopping basket totals )
### Test 1 & 2

Wrote test 1 for an empty shopping basket
```java
public class ShoppingBasketTest {

    @Test
    public void totalOfEmptyBasket(){
        ShoppingBasket basket = new ShoppingBasket();
        assertEquals(0.0, basket.getTotal(), 0.0);
    }
}
```
But it complains the following objects do not exist ( because we did not write code yet ! ):
- ShoppingBasket
- basket.getTotal()

So we need to declare them
```java
public class ShoppingBasket {

    public double getTotal() {
        return 0.0;
    }
}
```
Now run the test and it passes !

Write test 2 for a basket with 1 item passed to the shopping basket and it fails 
```java
public class ShoppingBasketTest {

    @Test
    public void totalOfEmptyBasket(){
        ShoppingBasket basket = new ShoppingBasket();
        assertEquals(0.0, basket.getTotal(), 0.0);
    }

    @Test
    public void totalOfSingleItem(){
        ShoppingBasket basket = new ShoppingBasket(Arrays.asList(new Item(100.0, 1)));
        assertEquals(100.0, basket.getTotal(), 0.0);
    }
}
```
It forces us to declare Item
```java
public class Item{
    
    public Item(double unitPrice, int quantity) {
    }
}
```
Also we need to declare constructor for ShoppingBasket class
```java
public ShoppingBasket(List<Item> items) {
    }
```
Now the first test requires to pass list of items since the constructor requires it now

```java
import java.util.ArrayList;

public class ShoppingBasketTest {

 @Test
 public void totalOfEmptyBasket() {
  ShoppingBasket basket = new ShoppingBasket(ArrayList<>());
  assertEquals(0.0, basket.getTotal(), 0.0);
 }

 @Test
 public void totalOfSingleItem() {
  ShoppingBasket basket = new ShoppingBasket(Arrays.asList(new Item(100.0, 1)));
  assertEquals(100.0, basket.getTotal(), 0.0);
 }
}
```
Now run the test and it fails. Expecting 100.0 but gets 0.0

This is because ShoppingBasket.getTotoal() return 0.0. We need to fix that.
```java
public class ShoppingBasket {

    private final List<Item> items;

    public ShoppingBasket(List<Item> items) {
        this.items = items;
    }

    public double getTotal() {
        if(items.isEmpty())
            return 0.0;
        return items.get(0).getUnitPrice();
    }
}
```
Now we need to fix item.getUnitPrice()
```java
public class Item{

    private final double unitPrice;

    public Item(double unitPrice, int quantity) {
        this.unitPrice = unitPrice;
    }

    public double getUnitPrice() {
        return unitPrice;
    }
}
```
Run thes tests and they pass
### Test 3

Wrote test 3 for basket with two items
```java
@Test
    public void totalOfTwoItems(){
        ShoppingBasket basket = new ShoppingBasket(
                Arrays.asList(
                        new Item(100.0, 1),
                        new Item(200.0, 1))
        );

        assertEquals(300.0, basket.getTotal(), 0.0);
    }
```
Run the test but it fails. Expecting 300.0 but got 100.0

The reason is that ShoppingBasket.getTotal() did not account for the quantity. So let us fix this
```java
public double getTotal() {
        return items.stream()
                .mapToDouble(item -> item.getUnitPrice()).sum();
    }
```

Now the test passes

### Refactor

Duplicated code in every test when creating a new basket
```java
ShoppingBasket basket = new ShoppingBasket(Arrays.asList(new Item(100.0, 1)));
```

exctracted into a private method and passed basket items as paramemter
```java
private ShoppingBasket buildBasketWithItems(Items... items){
    return new Shoppingasket(
            Arrays.asList(new Item(100.0, 1))
        );
}
```
So now building new basket becomes
```java
ShoppingBasket basket = buildBasketWithItems((new Item(100.0, 1)));
```
Run the test and it passes. So th refactoring makes it like this
```java
public class ShoppingBasketTest {

    @Test
    public void totalOfEmptyBasket(){
        ShoppingBasket basket = buildBasketWithItems();
        assertEquals(0.0, basket.getTotal(), 0.0);
    }

    @Test
    public void totalOfSingleItem(){
        ShoppingBasket basket = buildBasketWithItems(new Item(100.0, 1));
        assertEquals(100.0, basket.getTotal(), 0.0);
    }

    @Test
    public void totalOfTwoItems(){
        ShoppingBasket basket = buildBasketWithItems(
                    new Item(100.0, 1),
                    new Item(200.0, 1)
        );

        assertEquals(300.0, basket.getTotal(), 0.0);
    }

    private ShoppingBasket buildBasketWithItems(Item... items) {
        return new ShoppingBasket(Arrays.asList(items));
    }
}
```


### Test 4

Wrote test for item with quantity 2
```java
@Test
    public void totalOfItemWithQuantityTwo(){
        ShoppingBasket basket = buildBasketWithItems(
                new Item(100.0, 2)
        );
        assertEquals(200.0, basket.getTotal(), 0.0);

    }
```
Now the test fails. The ShoppingBasket.getTotal() was not taking into account the quantity value. So we will fix this by adding item.getQuantity()
```java
public double getTotal() {
        return items.stream()
                .mapToDouble(item -> 
        item.getUnitPrice() * item.getQuantity()).sum();
    }
```
We add getQuantity() in Item class
```java
public class Item{

    private final double unitPrice;
    private final int quantity;

    public Item(double unitPrice, int quantity) {

        this.unitPrice = unitPrice;
        this.quantity = quantity;
    }

    public double getUnitPrice() {
        return unitPrice;
    }

    public double getQuantity() {
        return quantity;
    }
}
```
Now the test passes

### Refactor

Shopping.ShoppingBasket.getTotal() has un-encapsulated item ( item.getUnitPrice() * item.getQuantity() ). So:

Extract into private method getSubTotal()
```java
public class ShoppingBasket {

 private final List<Item> items;

 public ShoppingBasket(List<Item> items) {
  this.items = items;
 }

 public double getTotal() {
  return items.stream()
          .mapToDouble(item -> item.getSubtotal()).sum();
 }
 
 private double getSubTotal(Item item){
     return item.getUnitPrice() * getQuanityt();
 }
} 
```
Run the test and it passes

But this private method can be moved into Item class

```java
public class Item {

 private final double unitPrice;
 private final int quantity;

 public Item(double unitPrice, int quantity) {

  this.unitPrice = unitPrice;
  this.quantity = quantity;
 }

 public double getUnitPrice() {
  return unitPrice;
 }

 public int getQuantity() {
  return quantity;
 }

 double getSubtotal() {
  return getUnitPrice * getQuantity();
 }
}
```
But also methods getUnitPrice() and getQuantity() are no longer referenced so refactor > inline method which replaces them with unitPrice and quantity respectively
```java
public class Item{

    private final double unitPrice;
    private final int quantity;

    public Item(double unitPrice, int quantity) {

        this.unitPrice = unitPrice;
        this.quantity = quantity;
    }

    double getSubtotal() {
        return unitPrice * (double) quantity;
    }
}
```


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
## 3: What Tests We Should Write? ( CDs: buy, search, .. )

We could write tests for each element/object (cosntructor, getter, setter, method, module, ...etc).

Here we face a few issues:
1. Tests are based on the design rather than descovering best deisgn to writing tests
2. Tests know too much about the internals of the deisgn and become coupled. So attepting to refactor will be very difficult 

So we need to write with is known as "meaningful tests".

We start at the requirements and create a tests list. So the requirements:

Customers can buy CDs, searching on the title and the artist. Record labels send batches of CDs to the warehouse. Customers can only order titles that are in stock.

Tests list:

1. Buy a CD
   - CD is in stock - stock count is reduced by quantity
   - Insufficient stock - throw an exception

2. Search for a CD
   - It's in catologue - return the CD info
   - No match - return nothing
   - Multiple matches - return list

3. Receive stock from a label:
   - Single title that is in the catalogue - add copies to the CD's stock
   - Not in the catalogue - dd it, and copies to it
   - Multiple CDs - any new CDs added to catalogue, and stock added to each CD

### Test 1: CD is in stock - stock count is reduced by 1

so we start with assertion
```java
@Test
    public void cdIsInStock() {
        assertEquals(9, cd.getStockCount());
    }
```
Then we go and declare the objects ( cd, cd.getStockCount() )
```java
public class CompactDisc {
    private int stock;

    public CompactDisc(int initialStock) {
        this.stock = initialStock;
    }

    public int getStockCount() {
        return stock;
    }

    public void buy(int quantity) {
        stock -= quantity;
    }
}
```
do the action ( cd.buy(1) )
```java
@Test
    public void cdIsInStock() throws InsufficientStockException {
        CompactDisc cd = new CompactDisc(10);
        cd.buy(1);
        assertEquals(9, cd.getStockCount());
    }
```
Run the test and it passes

### Test 2: Insufficient stock - throw an exception

Here we are testing that when there is insufficient stock to make a CD purchase then an exception is thrown. So the test is like
```java
@Test
    public void InsufficientStock(){
        Exception exception = assertThrows(InsufficientStockException.class, () -> {
                    new CompactDisc(0).buy(1);
                });
    }
```
Then we declare InsufficientStockException class
```java
public class InsufficientStockException extends Exception{
    public InsufficientStockException(String message){
        super(message);
    }
}
```
This requires us to add Throws clause to buy method and first testion defintion
```java
@Test
    public void cdIsInStock() throws InsufficientStockException {
        CompactDisc cd = new CompactDisc(10);
        cd.buy(1);
        assertEquals(9, cd.getStockCount());
    }
```
and cd.buy method
```java
public void buy(int quantity) throws InsufficientStockException {
        if(stock < quantity)
            throw new InsufficientStockException("Not enough stock to make the CD purchase");
        stock -= quantity;
    }
```
Run the test and it passes

### Concluaion

1. Test lists are good excercise to sit down with user/customer to capture test cases
2. Working throw the requirements and tests list
3. Think ahead is good but no coding yet
4. Write tests for meaningful outcomes, outcomes users want to happen
5. Some times the test implementations are obvious 
6. As you gain experience you realise you need less tests, and the criteria is not how many tests you write.

## 4: Duplication and Design ( Mars Rover navigation)

1. Duplication is the opposite of re-use 
2. Removing duplication introduces re-sue abstraction 

### Test rover navigation

Wrote a test assertion to check Mars Rover has navigated turning right, fo from facing Noth it should face East
```java
public class MarsRoverTest {

   @Test
   public void TurnRightNorthToEast() {
      Rover rover = new Rover("N");
      rover.go("R");
      assertEquals("E", rover.getFacing());
   }
}
```
Then implemented the Rover class and its methods getFacing() ang go()
```java
public class Rover {
    private String facing;

    public Rover(String facing) {
        this.facing = facing;
    }

    public String getFacing() {
        return facing;
    }

    public void go(String instruction) {
        if (facing == "N")
            facing = "E";
        else if (facing == "E")
            facing = "S";
        else if (facing == "S")
            facing = "W";
        else
            facing = "N";
    }
}
```
#### Refactoring: The Rule of Three
Now we can spot a pattern here in rover.go():

1. When spotting a pattern the rule of thee, in general, says to wait until you a pattern repeated three times then refactor
2. Waiting too long can render the refactorng too difficult ans the codebase grows in complexity

So we can refactor grover.go() method
```java
public void go(String instruction) {
        String[] campass = new String[]{"N", "E", "S", "W"};
        int index = Arrays.asList(campass).indexOf(facing);
        facing = campass[index + 1];
    }
```
### Refactor: remove test duplication

The three tests have the same behavior just with different inputs. So we can refactor by removing this duplication, but you must take into account that emoving duplication might lead to difficulty understating what a test does so it is important to name the test helper method clearly explaining what it is doing

So we can use paramaterised tests. Here the est vody has the beahavviour and all we need to do is pass parameters to test the behaviour.

So our test class is now much reduced and even removed the other two tests
```java
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
```
### Refactor: some test duplication is good
Let us test the rover turning LEFT.

If you try using the test method it will become complicated and not easy to understand what is happening. This where some duplication is good.
```java
@ParameterizedTest
    @CsvSource({
            "N, W",
            "W, S",
            "S, E",
            "E, N"
    })
    public void TurnLeftAntiClockwise(String startsFacing, String endsFacing){
        Rover rover = new Rover(startsFacing);
        rover.go("L");
        assertEquals(endsFacing, rover.getFacing());
    }
```
R8nning the test will fail because rover.go() does not understand how to turn left. So let us fix that
```java
public void go(String instruction) {
        if(instruction == "R") {
            String[] campass = new String[]{"N", "E", "S", "W"};
            int index = Arrays.asList(campass).indexOf(facing);
            facing = campass[(index + 1) % 4];
        } else{
            String[] campass = new String[]{"W", "S", "E", "N"};
            int index = Arrays.asList(campass).indexOf(facing);
            facing = campass[(index + 1) % 4];
        }
    }
```
We can furthur refactor rover.go()
1. we turn to a soecific direction left or right
2. this defines how the campass is formatted
so we end up with helper methods right() and left

Also the turn depends on the campass direction
```java
public void go(String instruction) {
        if(instruction == "R") {
            right();
        } else{
            left();
        }
    }

    private void right() {
        turn("N", "E", "S", "W");
    }

    private void left() {
        turn("W", "S", "E", "N");
    }

    private void turn(String n, String e, String s, String w) {
        String[] campass = new String[]{n, e, s, w};
        int index = Arrays.asList(campass).indexOf(facing);
        facing = campass[(index + 1) % 4];
    }
```

## 5: Inside out TDD ( Mars Rover navigation 2.0 )
### Tests are couplled with internals

Here we started by testing parts of the implementation. This prepares for later putting it all together for whole solution.

So we started writing tests for parts of the internal implementation:

roverInOut.right()
<br>roverInOut.left()
<br>roverInOut.forward()
<br>roverInOut.back()
<br>helper method to map commands to instructions mapToCommand()
```java
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
}
```
Looking at the RoverInOut class implementation we can sse how the tests are tightly couplled with its internals
```java
public class RoverInOut {
    private String facing;
    private int endY;
    private int endX;

    public RoverInOut(String facing, int x, int y) {
        this.facing = facing;
        this.endX = x;
        this.endY = y;
    }

    public String getFacing() {
        
        return facing;
    }

    public int[] getPosition() {
        return new int[]{endX, endY};
    }

    public Runnable mapInstructionToCommand(Character instruction) {
        Map<Character, Runnable> commands = new HashMap<Character, Runnable>();
        commands.put('F', this::forward);
        commands.put('B', this::back);
        commands.put('R', this::right);
        commands.put('L', this::left);
        return commands.get(instruction);
    }

    public void right() {
        turn("N", "E", "S", "W");
    }

    public void left() {
        turn("W", "S", "E", "N");
    }

    private void forward() {
        if (facing == "N")
           endY += 1;
        else if (facing == "E")
            endX += 1;
        else if (facing == "S")
            endY -= 1;
        else if (facing == "W")
            endX -= 1;
    }

    private void back() {
        if (facing == "N")
            endY -= 1;
        else if (facing == "E")
            endX -= 1;
        else if (facing == "S")
            endY += 1;
        else if (facing == "W")
            endX += 1;
    }

    private void turn(String n, String e, String s, String w) {
        String[] campass = new String[]{n, e, s, w};
        int index = Arrays.asList(campass).indexOf(facing);
        facing = campass[(index + 1) % 4];
    }
}
```
### Write tests for Rover navigation

Now let us write test for rover sequence of instructions starting with assertions and working backward
```java
@Test
    public void executesSequenceOfInsttructions(){
        RoverInOut roverInOut = new RoverInOut("N", 5, 5);
        roverInOut.go("RFF");
        assertEquals("E", roverInOut.getFacing());
        assertArrayEquals(new int[]{7, 5}, roverInOut.getPosition());
    }
```
The method roverIntOut.go() is a new one where it is the high level action that triggers which direction to trun to and the movement. So now we need to declare it
```java
public void go(String instruction) {
        instruction.chars().
                forEach(
                        c -> mapInstructionToCommand((char)c).run()
                );
    }
```
Here it takes the the instruction string, for example "RFF", and loops for each charachter runs the relevant method via mapToCommand() method..

So now this test is the high level test that the user will be looking for.

### Conclusion

Advantage:
- Each of the tests pinpints to a specific part of the internal design, so when a test fails we know were and why it failed

Disadvantage:
- The tests know alot about the internal design of the rover exposing our test code to internals of our code. This means it will be difficult to refactor because then you have to also change the tests after changing the code.
- Sometimes when trying at the end to put the high level test together the difference pieces do not work 

## 6: Scalling TDD with Stubs and Mocks ( Donate Movie 2.0 )
### Test with stubs

Here we have a movie library that has an external dependency; movie information (title and year) provided by an api when the imdb Id is provided with the request.

So we started with the assertions for the title and year and work backward
```java
@Test
    public void donateMovieAddedToCatalogueWithImdbInfo(){
        String ImdbId = "tt12345";
        String title = "The Abyss";
        int year = 1989;
        LibraryStubMock libraryStubMock = new LibraryStubMock(movieInfo);
        libraryStubMock.donate(ImdbId);
        MovieStubMock movieStubMock = libraryStubMock.findMovie(ImdbId);
        assertEquals(title, movieStubMock.getTitle());
        assertEquals(year, movieStubMock.getYear());
    }
```
Here we need to declare movieInfo which will contain the needed movie information (title and year).

We will define it as interface object
```java
public interface MovieInfo {
}
```
Then will inject the needed information using a stub StubMovieInfo which implements the MovieInfo interface
```java
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
        public StubMovieInfo(String title, int year) {
        }
    }
```
for our test to fail for the wrong title and year we need to return MovieStubMock() object and when we call librayrStubMock.findMovie() we return incorrect values
```java
public class MovieStubMock {
   public MovieStubMock(String imdbId) {
   }

   public String getTitle() {
      return "";
   }

   public int getYear() {
      return 0;
   }
}

public class LibraryStubMock {
    public LibraryStubMock(MovieInfo movieInfo) {
    }

    public MovieStubMock findMovie(String imdbId) {
        return new MovieStubMock("");
    }

    public void donate(String imdbId) {
    }
}
```
Now let us make our test pass for the correct values.

1. The assertions use movieStubMock to get the title and year, so 
2. we need to declare movieStubMock, 
3. It is returned as we search the libraryStubMock using the imdbId value,
4. The movieStubMock is donated by using the imdbId value

So we start by declaring the libraryStubMock.donate()

Our movie catalogue has imdbId as its key and the value is MovieStubMock object containing the title and year
```java
public void donate(String imdbId) {
        Map<String, String> info = movieInfo.fetch(imdbId);
        catalogue.put(
                imdbId,
                new MovieStubMock(
                        info.get("title"),
                        Integer.parseInt(info.get("year")))
        );
    }
```
When the donation is called this causes our stub to pass the movie info using movieInfo.fetch()
```java
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
```
So now we can use libraryStubMock.findMovie() to look for the movie with the imdbId value
```java
public MovieStubMock findMovie(String imdbId) {
        return catalogue.get(imdbId);
    }
```

### Testing with Mocks

There is a scenario where our logic calls commands, queries and triggers actions. So here we are not returning data, like in stubs, but we want to verify that the command, query or trigger has been invoked. This is where we use a mock object which allows us to test that the invokation did happen as expected.

The mock object implements the interface of the class we are testing. So when the class is invoked the mock object internally remembers the invokation. Later we can assert that the invokation did happen and query its assciated data.

So now we havea requirement to send email to members whenever a movie is donated.

So we write a test to verify that the emailserver has been called whenever a movie is donated.
```java
@Test
    public void membersEmailedAboutDonatedtitle() {
        verify(emailserver).sendEmail(
                "New Movie",
                "All members",
                new String[]{title, year});
    }
```
So the email will have:
1. template named New Movie
2. Send to all members
3. array with the movie title and year

then we mock an isntance of Emailserver. This mocked object will remember when Emailserver is called.
```java
@Test
    public void membersEmailedAboutDonatedtitle() {
        Emailserver emailserver = mock(Emailserver.class);
        verify(emailserver).sendEmail(
                "New Movie",
                "All members",
                new String[]{title, year});
    }
```
Now we need to implement method emailserver.sendEmail()
```java
public interface Emailserver {
    void sendEmail(String template,
                   String distributionList,
                   String[] params);
}
```
Now we need a movie title to donate. So we use our Stub to pass new movie data
```java
new LibraryStubMock(
        new StubMovieInfo(title, Integer.parseInt(year)))
        .donate("");
```
We run the test but fails saying emailerve was not invoked.

So to do that we need to pass emailserver to libraryStubMock to inovkae emailserver after donate action
```java
new LibraryStubMock(
        new StubMovieInfo(title, Integer.parseInt(year)), emailserver)
        .donate("");
```
This forces us to modify the constructor
```java
public LibraryStubMock(MovieInfo movieInfo, Emailserver emailserver) {
        this.movieInfo = movieInfo;
        this.emailserver = emailserver;
    }
```
After that we runthe emailserver.sendMail() method after donate action
```java
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
```
Now we run the test and it passes.

### Conclusion
So Stubs and Mockes are useful:
1. are usefull that stubs provide data when our logic requires fetching them. then the data is used to test our logic:
2. We used data without concerning ourselves how, where or what. This is very powerful as it allows us to write tests quickly. It is called speration of concern
3. We seperated our interface from our implementation providing felxibility. So if we want to fetch movie data from tomoato and not from imdb or sending sms text messages and not emails we can do that quickly
4. All of this is the result of designing test from Outside In. This is exactly fits how customer requirements work and it is reflects in the tests.

