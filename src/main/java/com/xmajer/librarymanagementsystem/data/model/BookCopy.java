package com.xmajer.librarymanagementsystem.data.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "book_copies")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BookCopy {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "book_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_book_copies_book")
    )
    @Setter
    private Book book;

    @Column(nullable = false)
    @Setter
    private Boolean available = true;

    public BookCopy(Book book) {
        this.book = book;
        this.available = true;
    }

    @PrePersist
    void prePersist() {
        if (available == null) {
            available = true;
        }
    }
}
