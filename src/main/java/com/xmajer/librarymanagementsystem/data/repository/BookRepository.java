package com.xmajer.librarymanagementsystem.data.repository;

import com.xmajer.librarymanagementsystem.data.model.Book;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface BookRepository extends JpaRepository<Book, Long> {
    @EntityGraph(attributePaths = "copies")
    @Query("select b from Book b where b.id = :id")
    Optional<Book> findByIdWithCopies(@Param("id") Long id);
}
