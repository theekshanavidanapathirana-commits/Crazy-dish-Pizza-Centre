package lk.sjp.pizza.model;
import lk.sjp.pizza.service.PaymentStrategy;

public class CardPayment implements PaymentStrategy {
    private String cardNumber;
    public CardPayment(String cardNumber) { this.cardNumber = cardNumber; }

    @Override
    public String processPayment(double amount) {
        return "Paid LKR " + amount + " via Credit/Debit Card (Ending: " + 
               (cardNumber.length() >= 4 ? cardNumber.substring(cardNumber.length() - 4) : "XXXX") + ")";
    }
}
