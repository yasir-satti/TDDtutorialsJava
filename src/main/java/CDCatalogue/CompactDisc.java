package CDCatalogue;

public class CompactDisc {
    private int stock;

    public CompactDisc(int initialStock) {

        this.stock = initialStock;
    }

    public int getStockCount() {
        return stock;
    }

    public void buy(int quantity) throws InsufficientStockException {
        if(stock < quantity)
            throw new InsufficientStockException("Not enough stock to make the CD purchase");
        stock -= quantity;
    }
}
