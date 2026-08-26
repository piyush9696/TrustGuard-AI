# TrustGuard AI

TrustGuard AI is a Spring Boot backend for detecting potential scam and phishing messages using a combination of Gemini AI analysis and deterministic rule-based analysis.

## Features

- User registration and login
- JWT-based authentication
- Protected API endpoints
- Gemini AI-based scam analysis
- Rule-based scam detection
- Risk score calculation
- Combined AI and rule-based red flags
- Swagger/OpenAPI documentation and testing

## Scam Analysis

The `/api/analyze` endpoint accepts a message and analyzes it using two approaches.

### Gemini AI Analysis

Gemini analyzes the message for:

- Scam classification
- Confidence
- Scam category
- Red flags
- Explanation
- Recommendation

### Rule-Based Analysis

The rule-based analyzer currently checks for:

- Urgency language
- Account threats
- Credential or sensitive information requests

The results from both approaches are combined into the final analysis.

## Risk Scoring

The final risk score combines:

- Rule-based analysis: 60%
- AI analysis: 40%

AI confidence contributes to the risk score only when Gemini classifies the message as a scam.

## Security

Spring Security is used with JWT authentication.

Authentication endpoints are publicly accessible, while other API endpoints require a valid JWT.

The application uses stateless sessions.

## Swagger / OpenAPI

Swagger/OpenAPI is configured for API documentation and testing.

Swagger UI can be used to:

- View available endpoints
- Authorize using a JWT
- Test protected endpoints
- View API responses

## Testing

JUnit tests have been added for:

- Rule-based scam detection
- Risk assessment

The risk assessment tests also cover the case where Gemini has high confidence that a message is not a scam, ensuring that safe messages do not receive a high risk score.
