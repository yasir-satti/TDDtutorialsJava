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

Wrote code and made it pass. The ShoppingBasket.getTotal() was not taking into account the quantity value

### Refactor

ShoppingBasket.getTotal() has un-encapsulated item ( item.getUnitPrice() * item.getQuantity() ). So:

1. extract into private method
2. But this private method can be moved into Item class
3. But also methods getUnitPrice() and getQuantity() are no longer referenced so refactor > inline method which replaces them with unitPrice and quantity respectively


So overall:

1. design is easy to understand
2. removed duplication
3. solved dependencies issues
4. hidden details from ShoppingBasket class