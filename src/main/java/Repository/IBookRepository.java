package Repository;

import Model.Book;

import java.util.Optional;

public interface IBookRepository {
    Optional<Book> findById(Long id);
    void save(Book book);
}
