
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Hospital Management System</title>

    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css"
          rel="stylesheet">
</head>

<body class="bg-light">

<div class="container mt-5">

    <div class="text-center mb-5">
        <h1 class="fw-bold">Hospital Management System</h1>
        <p class="text-muted">
            DBMS and JDBC Connectivity Project
        </p>
    </div>

    <div class="row justify-content-center g-4">

        <div class="col-md-5">
            <div class="card shadow-sm text-center p-4 h-100">
                <h3>Patient Management</h3>
                <p class="text-muted">
                    Register, view, search, update and delete patient records.
                </p>
                <a href="${pageContext.request.contextPath}/patients"
                   class="btn btn-primary mt-auto">
                    Open Patients
                </a>
            </div>
        </div>

        <div class="col-md-5">
            <div class="card shadow-sm text-center p-4 h-100">
                <h3>Doctor Management</h3>
                <p class="text-muted">
                    Add, view, search, update and delete doctor records.
                </p>
                <a href="${pageContext.request.contextPath}/doctors"
                   class="btn btn-success mt-auto">
                    Open Doctors
                </a>
            </div>
        </div>

    </div>

    <div class="text-center mt-5">
        <p class="text-success fw-semibold">
            Java + JDBC + MySQL 8 + Servlets + JSP + Bootstrap
        </p>
        <p class="text-muted small">
            Demonstrating CRUD operations, primary keys, foreign keys,
            constraints, SQL JOIN and PreparedStatement.
        </p>
    </div>

</div>

</body>
</html>
