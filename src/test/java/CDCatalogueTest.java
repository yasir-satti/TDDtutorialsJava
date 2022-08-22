import CDCatalogue.CompactDisc;
import CDCatalogue.InsufficientStockException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class CDCatalogueTest {

    @Test
    public void cdIsInStock() throws InsufficientStockException {
        CompactDisc cd = new CompactDisc(10);
        cd.buy(1);
        assertEquals(9, cd.getStockCount());
    }

    @Test
    public void InsufficientStock(){
        Exception exception = assertThrows(InsufficientStockException.class, () -> {
                    new CompactDisc(0).buy(1);
                });
    }
}
