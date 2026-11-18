package model;
import service.PaymentStrategy;

public class CashPayment implements PaymentStrategy {
    @Override
    public String processPayment(double amount) {
        return "Cash on Delivery / Counter Payment of LKR " + amount + " confirmed.";
    }
}
