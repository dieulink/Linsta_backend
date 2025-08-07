package com.linsta.linsta_backend.request;

import lombok.Data;

@Data
public class EditAddressRequest {
    private Long id;
    private String newAddress;
}
