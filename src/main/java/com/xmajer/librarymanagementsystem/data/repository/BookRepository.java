package com.xmajer.librarymanagementsystem.data.repository;

import com.xmajer.librarymanagementsystem.data.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookRepository extends JpaRepository<Book, Long> {
}
