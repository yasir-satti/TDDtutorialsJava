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
