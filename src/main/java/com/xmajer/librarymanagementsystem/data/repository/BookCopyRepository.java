package com.xmajer.librarymanagementsystem.data.repository;

import com.xmajer.librarymanagementsystem.data.model.BookCopy;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookCopyRepository extends JpaRepository<BookCopy, Long> {
}
