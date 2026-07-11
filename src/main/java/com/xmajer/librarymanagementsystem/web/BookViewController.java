package com.xmajer.librarymanagementsystem.web;

import com.xmajer.librarymanagementsystem.dto.response.BookResponse;
import com.xmajer.librarymanagementsystem.service.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/books")
@RequiredArgsConstructor
public class BookViewController {

    private static final int FRONTEND_PAGE_SIZE = 9;

    private final BookService bookService;

    @GetMapping
    public String getBooks(
            @PageableDefault(
                    size = FRONTEND_PAGE_SIZE,
                    sort = "title",
                    direction = Sort.Direction.ASC
            )
            Pageable pageable,
            Model model
    ) {
        Pageable frontendPageable = PageRequest.of(
                pageable.getPageNumber(),
                FRONTEND_PAGE_SIZE,
                pageable.getSort()
        );

        Page<BookResponse> books = bookService.getBooks(frontendPageable);

        Sort.Order sortOrder = frontendPageable.getSort()
                .stream()
                .findFirst()
                .orElse(Sort.Order.asc("title"));

        String currentSort =
                sortOrder.getProperty()
                        + ","
                        + sortOrder.getDirection().name().toLowerCase();

        model.addAttribute("books", books);
        model.addAttribute("currentSort", currentSort);

        return "books/list";
    }

    @GetMapping("/{id}")
    public String getBookDetail(
            @PathVariable Long id,
            Model model
    ) {
        model.addAttribute("book", bookService.getBookById(id));

        return "books/detail";
    }
}
