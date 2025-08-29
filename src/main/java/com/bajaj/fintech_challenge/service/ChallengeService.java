package com.bajaj.fintechchallenge.service;

import com.bajaj.fintechchallenge.dto.GenerateWebhookRequest;
import com.bajaj.fintechchallenge.dto.GenerateWebhookResponse;
import com.bajaj.fintechchallenge.dto.SubmitSolutionRequest;
import org.springframework.boot.CommandLineRunner;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Service
public class ChallengeService implements CommandLineRunner {

    private final RestTemplate restTemplate;

    // API endpoint for generating the webhook
    private static final String GENERATE_WEBHOOK_URL = "https://bfhldevapigw.healthrx.co.in/hiring/generateWebhook/JAVA";

    public ChallengeService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /**
     * This method is executed automatically when the Spring Boot application starts.
     * It orchestrates the entire flow as required by the problem statement.
     */
    @Override
    public void run(String... args) throws Exception {
        System.out.println("=====================================================");
        System.out.println("      BAJAJ FINSERV HEALTH CHALLENGE STARTED         ");
        System.out.println("=====================================================");

        try {
            // Step 1: Generate the webhook and get the access token
            GenerateWebhookResponse webhookResponse = generateWebhook();

            if (webhookResponse != null && webhookResponse.getWebhookURL() != null) {
                System.out.println("Successfully received webhook URL and token.");
                System.out.println("Webhook URL: " + webhookResponse.getWebhookURL());
                
                // Step 2: Solve the SQL problem and submit the solution
                submitSolution(webhookResponse);
            } else {
                System.err.println("Failed to get webhook response. Aborting.");
            }

        } catch (RestClientException e) {
            System.err.println("An error occurred during the API communication: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("An unexpected error occurred: " + e.getMessage());
        }

        System.out.println("=====================================================");
        System.out.println("      CHALLENGE EXECUTION FINISHED                 ");
        System.out.println("=====================================================");
    }

    /**
     * Sends the initial POST request to generate the webhook URL and access token.
     * @return The response from the API containing the webhook URL and token.
     */
    private GenerateWebhookResponse generateWebhook() {
        System.out.println("\n[Step 1/2] Sending request to generate webhook...");

        // =================================================================
        // IMPORTANT: Replace these details with your own.
        // =================================================================
        GenerateWebhookRequest requestBody = new GenerateWebhookRequest(
            "Riya Kapoor",      // <-- Replace with your name
            "22BCE1965",       // <-- Replace with your registration number (must be odd for Question 1)
            "riyakapoor1329@gmail.com" // <-- Replace with your email
        );
        // =================================================================

        // Set headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<GenerateWebhookRequest> entity = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<GenerateWebhookResponse> response = restTemplate.postForEntity(
                GENERATE_WEBHOOK_URL,
                entity,
                GenerateWebhookResponse.class
            );

            System.out.println("API call successful. Status code: " + response.getStatusCode());
            return response.getBody();
        } catch (RestClientException e) {
            System.err.println("Error while generating webhook: " + e.getMessage());
            return null;
        }
    }

    /**
     * Constructs the final SQL query and submits it to the provided webhook URL.
     * @param webhookResponse The object containing the webhook URL and access token.
     */
    private void submitSolution(GenerateWebhookResponse webhookResponse) {
        System.out.println("\n[Step 2/2] Preparing and submitting the SQL solution...");

        // The SQL query for Question 1 (for odd registration numbers)
        String sqlQuery = "SELECT p.AMOUNT AS SALARY, e.FIRST_NAME || ' ' || e.LAST_NAME AS NAME, CAST(strftime('%Y', 'now') - strftime('%Y', e.DOB) AS INTEGER) AS AGE, d.DEPARTMENT_NAME FROM PAYMENTS p JOIN EMPLOYEE e ON p.EMP_ID = e.EMP_ID JOIN DEPARTMENT d ON e.DEPARTMENT = d.DEPARTMENT_ID WHERE p.AMOUNT = (SELECT MAX(AMOUNT) FROM PAYMENTS WHERE CAST(strftime('%d', PAYMENT_TIME) AS INTEGER) != 1) AND CAST(strftime('%d', p.PAYMENT_TIME) AS INTEGER) != 1 LIMIT 1;";

        SubmitSolutionRequest solutionRequestBody = new SubmitSolutionRequest(sqlQuery);

        // Prepare headers, including the JWT Authorization token
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(webhookResponse.getAccessToken());

        HttpEntity<SubmitSolutionRequest> entity = new HttpEntity<>(solutionRequestBody, headers);

        try {
            // The submission URL is dynamic, taken from the first API response
            String submissionUrl = webhookResponse.getWebhookURL();
            
            System.out.println("Submitting solution to: " + submissionUrl);

            ResponseEntity<String> response = restTemplate.exchange(
                submissionUrl,
                HttpMethod.POST,
                entity,
                String.class
            );

            System.out.println("Solution submitted successfully!");
            System.out.println("Submission response status: " + response.getStatusCode());
            System.out.println("Submission response body: " + response.getBody());

        } catch (RestClientException e) {
            System.err.println("Error while submitting solution: " + e.getMessage());
        }
    }
}