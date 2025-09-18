package Service;

import Model.*;
import Repository.IBookRepository;
import Repository.ICartRepository;
import Repository.IOrderRepository;
import Repository.IUserRepository;

import java.util.Optional;

public class CartService  {
    private final ICartRepository cartRepository;
    private final IBookRepository bookRepository;
    private final IOrderRepository orderRepository;
    private final IUserRepository userRepository;

    public CartService(ICartRepository cartRepository,
                       IBookRepository bookRepository,
                       IOrderRepository orderRepository,
                       IUserRepository userRepository) {
        this.cartRepository = cartRepository;
        this.bookRepository = bookRepository;
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
    }

    public Cart getOrCreateCart(long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return cartRepository.findByUserId(userId).orElseGet(() -> {
            Cart newCart = new Cart(null, user);
            return cartRepository.save(newCart);
        });
    }

    public Cart addToCart(Long userid, Book book, int quantity) {
        User user = userRepository.findById(userid)
                .orElseThrow(() -> new RuntimeException("User not found"));
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }

        int availableStock = book.getStockQuantity();
        if (quantity > availableStock) {
            quantity = availableStock;
        }
        Cart cart = getOrCreateCart(userid);
        Optional<CartItem> existingItem = cart.getItems().stream()
                .filter(item -> item.getBook().getId().equals(book.getId()))
                .findFirst();

        if (existingItem.isPresent()) {
            CartItem item = existingItem.get();
            int newQuantity = item.getQuantity() + quantity;
            if (newQuantity > availableStock) {
                throw new IllegalArgumentException("Book is out of stock");
            }
            item.setQuantity(newQuantity);
        } else {
            CartItem newItem = new CartItem(book, quantity);
            cart.addItem(newItem);
        }

        cartRepository.save(cart);
        return cart;
    }


    public void checkout(Long userid) {
        Cart cart = getOrCreateCart(userid);
        if (cart == null || cart.getItems().isEmpty()) {
            throw new RuntimeException("Cart is empty");
        }

        for (CartItem item : cart.getItems()) {
            Book book = item.getBook();
            int quantity = item.getQuantity();

            if (quantity > book.getStockQuantity()) {
                throw new IllegalArgumentException("Not enough stock");
            }
            book.setStockQuantity(book.getStockQuantity() - quantity);
            bookRepository.save(book);

            Order order = new Order(book, quantity);
            orderRepository.save(order);
        }
        cart.getItems().clear();
        cartRepository.save(cart);
    }

    public void cancelOrder(User user, Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));
        Book book = order.getBook();
        book.setStockQuantity(book.getStockQuantity() + order.getQuantity());
        bookRepository.save(book);
        orderRepository.delete(order);
    }
}