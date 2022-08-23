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

