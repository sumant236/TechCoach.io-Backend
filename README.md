# 🤖 TechCoach.io - AI-Powered Technical Interview Platform

## 💡 About TechCoach.io

TechCoach.io is a full-stack technical interview preparation platform designed to simulate real-world technical interviews using generative AI.

* **Architecture:** Monolithic/Full-stack separation with a Spring Boot REST API backend and a React Single Page Application (SPA) frontend.
* **Secure Auth:** Implements stateless JSON Web Token (JWT) authentication using `localStorage` token management and Google OAuth2 social login integration.
* **AI-Powered:** Utilizes the Google Gemini LLM (`gemini-3.5-flash`) to dynamically generate role-specific technical questions and evaluate candidate responses with precise scoring and feedback.
* **Data Tracking:** Maintains a persistent history of user interview sessions, QnA records, performance history, and rolling average scoring.

> **🔗 Repositories:**
> * **Backend:** [GitHub Repository](https://www.google.com/search?q=https://github.com/sumant236/TechCoach.io)
> * **Frontend:** [GitHub Repository](https://github.com/sumant236/TechCoach.io-Frontend.git)
>
>

> **🚀 Live Deployment:**
> * **Backend API / App:** [https://techcoach-io.vercel.app](https://techcoach-io.vercel.app)
>
>

---

## 🏗️ Tech Stack & Architecture

### Backend Stack

* **Core Language & Framework:** Java 17, Spring Boot 3.x
* **Security & Auth:** Spring Security, JWT (`jjwt`), OAuth2 Client
* **Database & Persistence:** MySQL / PostgreSQL, Spring Data JPA, Hibernate, Flyway/DDL auto-update
* **AI Integration:** Spring Framework `RestClient` communicating with Google Gemini API
* **Build Tool:** Maven

### Frontend Stack

* **Library & Build System:** React 18+, Vite
* **Routing & State:** React Router v6, Context API (`AuthContext`)
* **HTTP & Interceptors:** Axios with automated Bearer token request injection
* **Styling & UI:** Tailwind CSS for fully responsive mobile-first UI components

---

## 🗄️ Database & Deployment Infrastructure

* **Database Hosting:** Cloud-hosted relational database (PostgreSQL/MySQL instance configured via environment variables on Render).
* **Entity Relationships:**
* `User` (1) ──< (`Interview`) (Many to One, cascade delete)
* `Interview` (1) ──< (`QnARecord`) (Many to One, cascade all & orphan removal)


* **Cloud Deployment:**
* Deployed on **Render** as a cloud web service environment.
* Environment variables securely manage production secrets (`DB_URL`, `DB_USERNAME`, `DB_PASSWORD`, `JWT_SECRET`, `JWT_EXPIRATION`, `GEMINI_API_KEY`, `FRONTEND_URL`, and Google OAuth client credentials).



---

## 🚀 Getting Started Locally

### 1. Backend Setup

1. **Prerequisites:** Java 17, Maven, and MySQL/PostgreSQL installed locally.
2. **Configuration:** Set up your `application.yml` or environment properties:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/techcoach
spring.datasource.username=root
spring.datasource.password=your_password
jwt.secret=your_base64_encoded_secret_key
jwt.expirationMs=86400000
gemini.api.key=your_gemini_api_key
frontend.url=http://localhost:5173

```


3. **Run Application:**
```bash
mvn spring-boot:run

```


The backend will run on `http://localhost:8081`.

### 2. Frontend Setup

1. **Prerequisites:** Node.js & npm installed.
2. **Configuration:** Create a `.env` file in the frontend root:
```env
VITE_API_BASE_URL=http://localhost:8081

```


3. **Install Dependencies & Run:**
```bash
npm install
npm run dev

```


The frontend SPA will run on `http://localhost:5173`.

---

## 🔌 Key API Endpoints

| Method | Endpoint | Description |
| --- | --- | --- |
| POST | `/api/auth/register` | Create a new user account and issue token |
| POST | `/api/auth/login` | Authenticate credentials and return JWT token |
| GET | `/api/auth/me` | Fetch currently authenticated user principal details |
| POST | `/api/interviews/start` | Initialize session and generate AI interview questions |
| POST | `/api/interviews/{id}/answer` | Submit a candidate answer for AI evaluation & scoring |
| GET | `/api/interviews/{id}` | Rehydrate active interview session state |
| GET | `/api/interviews/history` | Retrieve user's past interview performance and analytics |