package Model;

import Model.Enum.PaymentMethod;
import Model.Enum.PaymentStatus;
import lombok.Data;

@Data
public class Payment {
    private Long id;
    private Order order;
    private PaymentMethod method;
    private double amount;
    private PaymentStatus status;
}
