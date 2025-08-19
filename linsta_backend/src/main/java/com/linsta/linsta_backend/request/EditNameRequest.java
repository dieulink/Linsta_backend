package com.linsta.linsta_backend.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class EditNameRequest {
    private Long id;
    private String newName;
}
