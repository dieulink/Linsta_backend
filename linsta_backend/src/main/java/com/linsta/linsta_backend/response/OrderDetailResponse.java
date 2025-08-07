
package com.linsta.linsta_backend.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderDetailResponse {
    private Long productId;
    private int productPrice;
    private int quantity;
}
