import Model.*;
import Repository.*;
import Service.CartService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class CartServiceTest {
    private ICartRepository cartRepository;
    private IBookRepository bookRepository;
    private IOrderRepository orderRepository;
    private IUserRepository userRepository;
    private CartService cartService;

    private User user;
    private Book book;
    private Order order;

    @BeforeEach
    void setUp() {
        cartRepository = mock(ICartRepository.class);
        bookRepository = mock(IBookRepository.class);
        orderRepository = mock(IOrderRepository.class);
        userRepository = mock(IUserRepository.class);
        cartService = new CartService(cartRepository, bookRepository, orderRepository,userRepository);
    }

    @Test
    void should_Not_Create_New_Cart_if_Existing_Cart_Exists_for_User() {
        User user = new User(1L, "testUser", "123456");
        Cart existingCart = new Cart(1L, user);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(cartRepository.findByUserId(1L)).thenReturn(Optional.of(existingCart));

        Cart cart = cartService.getOrCreateCart(1L);

        assertThat(cart, is(equalTo(existingCart)));
        verify(cartRepository, never()).save(any(Cart.class));
    }


    @Test
    void should_Return_Cart_if_User_Exists_when_Getting_or_Creating_Cart() {
        User user = new User(1L, "testUser", "123456");
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(cartRepository.save(any(Cart.class))).thenAnswer(inv -> inv.getArgument(0));
        Cart cart = cartService.getOrCreateCart(1L);
        assertThat(cart, is(notNullValue()));
        assertThat(cart.getUser().getId(), is(equalTo(user.getId())));
        verify(cartRepository, times(1)).save(any(Cart.class));

    }

    @Test
    void should_Throw_Exception_if_User_Not_Found_when_Getting_or_Creating_Cart() {
        when(userRepository.findById(2L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> cartService.getOrCreateCart(2L));
    }

    @Test
    void should_Throw_Exception_if_User_Not_LoggedIn_when_Adding_to_Cart() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> cartService.addToCart(99L, book, 2));
        assertThat(ex.getMessage(), is(equalTo("User not found")));
        verify(cartRepository, never()).save(any());
    }

    @Test
    void should_Add_New_Item_to_Cart_when_Adding_to_Cart() {
        User user = new User(1L, "testUser", "123456");
        book = new Book(1L, "Java Book", 10);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        Cart emptyCart = new Cart(1L, user);
        when(cartRepository.save(any(Cart.class))).thenReturn(emptyCart);
        Cart cart = cartService.addToCart(1L, book, 3);
        assertThat(cart.getItems(), hasSize(1));
        assertThat(cart.getItems().get(0).getQuantity(), is(equalTo(3)));
    }

    @Test
    void should_Allow_Exceed_Stock_when_Adding_to_Cart() {
        User user = new User(1L, "testUser", "123456");
        book = new Book(1L, "Java Book", 10);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        Cart emptyCart = new Cart(1L, user);
        when(cartRepository.save(any(Cart.class))).thenReturn(emptyCart);
        Cart cart = cartService.addToCart(1L, book, 20);
        assertThat(cart.getItems().get(0).getQuantity(), is(equalTo(10)));
    }

    @Test
    void should_Throw_Exception_if_Invalid_Quantity_when_Adding_to_Cart() {
        User user = new User(1L, "testUser", "123456");
        book = new Book(1L, "Java Book", 10);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(cartRepository.save(any(Cart.class))).thenAnswer(e -> e.getArgument(0));
        assertThrows(IllegalArgumentException.class,
                () -> cartService.addToCart(1L, book, 0));
        assertThrows(IllegalArgumentException.class,
                () -> cartService.addToCart(1L, book, -5));
    }

    @Test
    void should_Throw_Exception_if_User_Not_LoggedIn_when_Checking_Out() {
        User user = new User(1L, "testUser", "123456");
        when(userRepository.findById(99L)).thenReturn(Optional.empty());
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> cartService.checkout(99L));
        assertThat(ex.getMessage(), is(equalTo("User not found")));
        verify(orderRepository, never()).save(any());
    }


    @Test
    void should_Successfully_Checkout_when_Cart_is_Valid() {
        User user = new User(1L, "testUser", "123456");
        book = new Book(1L, "Java Book", 10);
        Cart cart = new Cart(1L, user);
        cart.addItem(new CartItem(book, 2));

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(cartRepository.save(any(Cart.class))).thenReturn(cart);

        cartService.checkout(1L);

        verify(bookRepository, times(1)).save(any(Book.class));
        verify(orderRepository, times(1)).save(any(Order.class));
        assertThat(cart.getItems(), is(empty()));
    }

    @Test
    void should_Throw_Exception_if_Insufficient_Stock_when_Checking_Out() {
        User user = new User(1L, "testUser", "123456");
        book = new Book(1L, "Java Book", 10);
        Cart cart = new Cart(1L, user);
        cart.addItem(new CartItem(book, 15));

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(cartRepository.save(any(Cart.class))).thenReturn(cart);

        assertThrows(IllegalArgumentException.class, () -> cartService.checkout(1L));
    }

    @Test
    void should_Throw_Exception_if_Cart_is_Empty_when_Checking_Out() {
        User user = new User(1L, "testUser", "123456");
        book = new Book(1L, "Java Book", 10);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        Cart cart = new Cart(null, user);
        when(cartRepository.save(any(Cart.class))).thenReturn(cart);

        Exception ex = assertThrows(RuntimeException.class,
                () -> cartService.checkout(1L));

        assertThat(ex.getMessage(), is(equalTo("Cart is empty")));
        verify(orderRepository, never()).save(any());
    }


    @Test
    void should_Throw_Exception_if_Order_Not_Found_when_Canceling_Order() {
        user= new User(1L, "testUser","123456");
        book = new Book(1L, "Java Book", 10);
        order = new Order(book, 2);
        order.setId(100L);
        when(orderRepository.findById(999L)).thenReturn(Optional.empty());
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> cartService.cancelOrder(user, 999L));

        assertThat(ex.getMessage(), is(equalTo("Order not found")));
        verify(bookRepository, never()).save(any());
        verify(orderRepository, never()).delete(any());
    }

    @Test
    void should_Restore_Stock_and_Delete_Order_when_Canceling_Valid_Order() {
        user = new User(1L, "testUser","123456");
        book = new Book(1L, "Java Book", 10);
        order = new Order(book, 2);
        order.setId(100L);
        when(orderRepository.findById(100L)).thenReturn(Optional.of(order));
        cartService.cancelOrder(user, 100L);
        assertThat(book.getStockQuantity(), is(equalTo(12)));
        verify(bookRepository, times(1)).save(book);
        verify(orderRepository, times(1)).delete(order);
    }
}
