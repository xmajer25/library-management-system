package com.xmajer.librarymanagementsystem.data.repository;

import com.xmajer.librarymanagementsystem.data.model.BookCopy;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BookCopyRepository extends JpaRepository<BookCopy, Long> {
    List<BookCopy> findAllByBookId(Long bookId);

    Optional<BookCopy> findByIdAndBookId(Long id, Long bookId);
}
