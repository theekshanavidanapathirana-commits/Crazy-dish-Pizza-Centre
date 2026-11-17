package lk.sjp.pizza.model;
import lk.sjp.pizza.service.Billable;

public abstract class OrderItem implements Billable {
    private String itemName;
    private double basePrice;

    public OrderItem(String itemName, double basePrice) {
        this.itemName = itemName;
        this.basePrice = basePrice;
    }

    public String getItemName() { 
        return itemName; 
    }
    public double getBasePrice() {
        return basePrice;
    }
}
