package com.linsta.linsta_backend.request;

import lombok.Data;

@Data
public class EditNameRequest {
    private Long id;
    private String newName;
}
