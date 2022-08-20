package Shopping;

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
