# 🤖 TechCoach.io - AI Interview Backend

## 💡 About TechCoach.io
TechCoach.io is a backend learning project designed to simulate real-world technical interviews using AI.
* **Secure Auth:** Implements stateless JWT authentication via HttpOnly cookies and Google OAuth2 integration.
* **AI-Powered:** Utilizes the Gemini LLM to dynamically generate role-specific technical questions and evaluate candidate answers.
* **Data Tracking:** Maintains a persistent history of user interview sessions, QnA records, and average scoring.

> **🔗 Frontend:** You can reach for the frontend at: https://github.com/sumant236/TechCoach.io-Frontend.git

## 🏗️ Tech Stack & Core Concepts
* **Framework & DB:** Built with Java 17, Spring Boot 3.x, Spring Security, and PostgreSQL.
* **External APIs:** Orchestrates synchronous REST calls to Google's Gemini API for generative AI workflows.
* **Stateless Security:** Enforces robust JWT validation and CORS configuration to mitigate XSS and CSRF vulnerabilities.

### Project Structure
```text
src/main/java/com/techcoach/
├── controller/      (REST API endpoints)
├── service/         (Business logic & AI orchestration)
├── security/        (JWT filters & OAuth2 handlers)
└── entity/          (PostgreSQL JPA models)

```

## 🚀 Getting Started

1. **Prerequisites:** Java 17 and PostgreSQL running on your machine.
2. **Setup:** Create a `.env` file in the root directory and configure your variables (`DB_URL`, `JWT_SECRET`, `GEMINI_API_KEY`, and Google OAuth credentials).
3. **Run:** Execute the following command to start the application:
```bash
mvn spring-boot:run

```


The backend will run on `http://localhost:8081`.

## 🔌 Key API Endpoints

| Method | Endpoint | Description |
| --- | --- | --- |
| POST | `/api/auth/register` | Create a new user account |
| POST | `/api/auth/login` | Authenticate and receive a JWT cookie |
| POST | `/api/interviews/start` | Initialize session and generate AI questions |
| POST | `/api/interviews/{id}/answer` | Submit a candidate answer for AI evaluation |
| GET | `/api/interviews/history` | Retrieve user's past interview performance |

```
