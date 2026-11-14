package lk.sjp.pizza.service;

public interface PaymentStrategy {
    String processPayment(double amount);
}
