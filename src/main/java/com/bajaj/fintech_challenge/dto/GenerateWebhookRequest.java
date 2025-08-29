package com.bajaj.fintechchallenge.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GenerateWebhookRequest {
    private String name;
    private String regNo;
    private String email;
}