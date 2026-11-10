# 🏥 Hospital Management System

A simple, web-based Hospital Management System built using Java and MySQL to make basic hospital operations easier to manage.

This project focuses on two important parts of a hospital: **Patient Management** and **Doctor Management**. It provides a clean interface where users can manage patient and doctor information without having to work directly with database tables.

The project was developed as a practical learning experience to understand how Java web applications work, how databases connect with applications, and how a complete application can be deployed online.

## 🌐 Live Application

**Live Website:** https://hospital-management-system-143o.onrender.com/HospitalManagement/

**GitHub Repository:** https://github.com/Vihan-narcissistic/Hospital_Management_System

## ✨ Features

### 1. Patient Management
- Register new patients.
- View all registered patients.
- Search for patients.
- Update patient information.
- Delete patient records.
- Store patient details such as name, gender, date of birth, phone number, and address.

### 2. Doctor Management
- Add new doctors.
- View doctor records.
- Search for doctors.
- Update doctor information.
- Delete doctor records.
- Manage doctor specialization and department information.

### 3. Google OAuth Integration
- Register using a Google account.
- Retrieve basic profile information such as name, email, and profile picture.
- Automatically prefill available registration details.
- Collect additional patient information through the registration form.

### 4. Database Management
- MySQL database integration using JDBC.
- Prepared statements for database operations.
- Database relationships and foreign keys.
- Triggers for audit logging.
- Stored procedure for appointment booking.
- A view for patient and appointment details.

## 🛠️ Technologies Used

| Technology | Purpose |
|---|---|
| Java | Backend programming |
| JDBC | Database connectivity |
| MySQL 8 | Database management |
| Jakarta Servlets | Request handling |
| JSP | Dynamic web pages |
| Bootstrap | Responsive user interface |
| HTML, CSS, JavaScript | Frontend development |
| Maven | Dependency and build management |
| Apache Tomcat 10.1 | Web application server |
| Google OAuth 2.0 | Google-based authentication |
| Aiven | Cloud MySQL hosting |
| Render | Application deployment |
| Git & GitHub | Version control and project hosting |

## 📂 Project Structure

```text
Hospital_Management_System/
│
├── src/
│   └── main/
│       ├── java/
│       │   └── com/hospital/
│       │       ├── controller/
│       │       ├── dao/
│       │       ├── model/
│       │       └── util/
│       │
│       ├── resources/
│       │   └── db.properties
│       │
│       └── webapp/
│           ├── WEB-INF/
│           │   └── views/
│           ├── index.jsp
│           └── ...
│
├── database/
├── Dockerfile
├── .dockerignore
├── .gitignore
├── pom.xml
└── README.md
```

## ⚙️ Running the Project Locally

### Prerequisites

Make sure you have installed:

- JDK 17 or later
- Apache Maven
- MySQL 8
- Apache Tomcat 10.1
- MySQL Workbench (recommended)
- VS Code or another Java IDE

### Step 1: Clone the repository

```bash
git clone https://github.com/Vihan-narcissistic/Hospital_Management_System.git
```

Move into the project directory:

```bash
cd Hospital_Management_System
```

### Step 2: Configure the database

1. Start your local MySQL server.
2. Create a database named `hospital_management`.
3. Import the required database SQL scripts.
4. Make sure the required tables, relationships, triggers, stored procedure, and view are available.

### Step 3: Configure database credentials

Create or configure:

`src/main/resources/db.properties`

Use the following format:

```properties
db.url=jdbc:mysql://localhost:3306/hospital_management
db.username=root
db.password=YOUR_MYSQL_PASSWORD
```

Replace the example credentials with your own local MySQL credentials.

**Important:** Never upload `db.properties` or actual passwords to GitHub.

### Step 4: Build the application

Open the terminal in the project directory and run:

```bash
mvn clean package
```

After a successful build, Maven generates:

```text
target/HospitalManagement.war
```

### Step 5: Deploy on Tomcat

Copy `HospitalManagement.war` into Tomcat's `webapps` directory and start Tomcat.

Open the application:

```text
http://localhost:8080/HospitalManagement/
```

## 🔐 Google OAuth Configuration

The application supports Google OAuth 2.0 for patient registration.

To enable Google Sign-In:

1. Create a project in Google Cloud Console.
2. Configure Google Auth Platform.
3. Create an OAuth 2.0 Web Client.
4. Configure the authorized redirect URI:

```text
http://localhost:8080/HospitalManagement/oauth2callback
```

5. Configure the Google credentials using environment variables:

```text
GOOGLE_CLIENT_ID
GOOGLE_CLIENT_SECRET
GOOGLE_REDIRECT_URI
```

For local development, the redirect URI should use localhost. For production, use your deployed HTTPS callback URL.

## ☁️ Deployment

The application is deployed using:

- **Render:** Hosts the Java web application using Docker and Apache Tomcat.
- **Aiven:** Hosts the cloud MySQL database.
- **GitHub:** Stores the project source code.

Production environment variables:

```text
DB_URL
DB_USERNAME
DB_PASSWORD
GOOGLE_CLIENT_ID
GOOGLE_CLIENT_SECRET
GOOGLE_REDIRECT_URI
```

Do not store actual credentials in the repository. Configure them through Render's Environment settings.

## 🔒 Security Considerations

- Database operations use JDBC prepared statements.
- Sensitive configuration files are excluded from Git.
- Database credentials are supplied through environment variables in production.
- Google OAuth credentials are kept outside the source repository.
- Database backups should be stored securely.

## 🎯 Project Objectives

The main purpose of this project was to build a practical Java-based web application while learning:

- Java web development.
- Servlet and JSP architecture.
- JDBC and MySQL integration.
- CRUD operations.
- Database relationships and SQL programming.
- Google OAuth authentication.
- Maven-based project management.
- Docker-based deployment.
- Cloud hosting and environment configuration.
- Git and GitHub workflows.

## 🚀 Future Improvements

Some features that could be added in future versions include:

- Appointment management.
- Patient medical history.
- Billing and payment management.
- Admin and staff dashboards.
- Role-based access control.
- Better reporting and analytics.
- Improved authentication and authorization.

## 👨‍💻 About the Project

This Hospital Management System is a learning-oriented project created to bring together Java programming, database management, frontend development, and cloud deployment in one practical application.

It started as a simple local application and was later extended with Google OAuth authentication and cloud hosting.

The goal was not just to build something that works, but also to understand the complete development process—from writing code and connecting databases to deploying an application that can be accessed online.

---

**Made with Java, MySQL, and a lot of learning. ❤️**
