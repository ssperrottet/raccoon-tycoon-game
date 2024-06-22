import java.util.ArrayList;

public class ProductionCard {
    private final ArrayList<Resources> productionResources;
    private final ArrayList<Resources> priceResources;

    public ProductionCard(ArrayList<Resources> productionResources, ArrayList<Resources> priceResources) {
        this.productionResources = productionResources;
        this.priceResources = priceResources;
    }

    public ArrayList<Resources> getProductionResources() {
        return productionResources;
    }

    public ArrayList<Resources> getPriceResources() {
        return priceResources;
    }

    @Override
    public String toString() {
        return "ProductionCard{" +
                "prod=" + Helper.stringifyArray(productionResources) +
                ", priceResources=" + Helper.stringifyArray(priceResources) +
                '}';
    }
}
