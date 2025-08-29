package com.bajaj.fintechchallenge.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class GenerateWebhookResponse {
    private String webhookURL;
    private String accessToken;
}