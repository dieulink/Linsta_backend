package com.linsta.linsta_backend.response;

import com.linsta.linsta_backend.model.Product;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductPageResponse {
    private List<Product> products;
    private int currentPage;
    private int totalPages;
    private long totalItems;
}