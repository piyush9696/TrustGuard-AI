# TrustGuard

TrustGuard is a backend application for detecting potentially fraudulent and
phishing messages using AI.

The project is being built with Spring Boot and PostgreSQL, with Google Gemini
used for the initial AI-based analysis. The main goal is to develop a system
that does more than simply ask an AI model whether a message is a scam. Future
versions will combine AI analysis with rule-based signals, URL analysis, and
threat intelligence to produce a more reliable risk assessment.

## Current Features

The current version of TrustGuard provides:

- User registration and login
- BCrypt password hashing
- JWT-based authentication
- Protected REST APIs
- PostgreSQL persistence
- AI-based analysis of suspicious messages
- Scam category and confidence estimation
- Detection of potential red flags
- Explanation and recommendation for the user

## How It Works

A user first creates an account and logs in. After authentication, the server
provides a JWT which is required for accessing protected endpoints.

For scam analysis, the authenticated user sends a suspicious message to the
analysis endpoint. TrustGuard sends the message to Google Gemini and converts
the model's response into a structured result containing the predicted scam
status, confidence, category, red flags, explanation, and recommendation.

The AI result is currently the first layer of the detection system. The later
stages of the project will add additional signals so that the final decision
does not depend entirely on an AI model.

## Tech Stack

- Java
- Spring Boot
- Spring Security
- Spring Data JPA
- Hibernate
- PostgreSQL
- Lombok
- Jakarta Validation
- JWT
- BCrypt
- Google Gemini API
- Google GenAI Java SDK
- Maven
- Git / GitHub
- Postman

## API

### Register

```http
POST /api/auth/register
