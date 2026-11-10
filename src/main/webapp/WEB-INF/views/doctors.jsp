
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">

    <title>Doctor Management</title>

    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css"
          rel="stylesheet">
</head>

<body class="bg-light">

<div class="container mt-4 mb-5">

    <div class="d-flex justify-content-between align-items-center mb-4">
        <h2>Doctor Management</h2>
        <a href="${pageContext.request.contextPath}/patients"
           class="btn btn-outline-primary">Patient Management</a>
    </div>

    <!-- Doctor Form -->
    <div class="card shadow-sm mb-4">
        <div class="card-header bg-primary text-white">
            <c:choose>
                <c:when test="${not empty doctorToEdit}">
                    Edit Doctor
                </c:when>
                <c:otherwise>
                    Add New Doctor
                </c:otherwise>
            </c:choose>
        </div>

        <div class="card-body">

            <form action="${pageContext.request.contextPath}/doctors"
                  method="post">

                <c:if test="${not empty doctorToEdit}">
                    <input type="hidden" name="action" value="update">
                    <input type="hidden" name="doctorId"
                           value="<c:out value='${doctorToEdit.doctorId}'/>">
                </c:if>

                <div class="row">

                    <div class="col-md-6 mb-3">
                        <label class="form-label">Doctor Name</label>
                        <input type="text" name="fullName"
                               class="form-control"
                               maxlength="150" required
                               value="<c:out value='${doctorToEdit.fullName}'/>">
                    </div>

                    <div class="col-md-6 mb-3">
                        <label class="form-label">Specialization</label>
                        <input type="text" name="specialization"
                               class="form-control"
                               maxlength="100" required
                               value="<c:out value='${doctorToEdit.specialization}'/>">
                    </div>

                    <div class="col-md-6 mb-3">
                        <label class="form-label">License Number</label>
                        <input type="text" name="licenseNumber"
                               class="form-control"
                               maxlength="50" required
                               value="<c:out value='${doctorToEdit.licenseNumber}'/>">
                    </div>

                    <div class="col-md-6 mb-3">
                        <label class="form-label">Department</label>

                        <select name="departmentId"
                                class="form-select" required>

                            <option value="">Select Department</option>

                            <c:forEach var="department"
                                       items="${departments}">

                                <option value="${department.key}"
                                    <c:if test="${not empty doctorToEdit and doctorToEdit.departmentId == department.key}">
                                        selected
                                    </c:if>>
                                    <c:out value="${department.value}"/>
                                </option>

                            </c:forEach>

                        </select>
                    </div>

                    <div class="col-md-6 mb-3">
                        <label class="form-label">Consultation Fee</label>
                        <input type="number" name="consultationFee"
                               class="form-control"
                               min="0" max="99999999.99"
                               step="0.01" required
                               value="<c:out value='${doctorToEdit.consultationFee}'/>">
                    </div>

                </div>

                <c:choose>
                    <c:when test="${not empty doctorToEdit}">
                        <button type="submit" class="btn btn-success">
                            Update Doctor
                        </button>

                        <a href="${pageContext.request.contextPath}/doctors"
                           class="btn btn-secondary">Cancel</a>
                    </c:when>

                    <c:otherwise>
                        <button type="submit" class="btn btn-primary">
                            Add Doctor
                        </button>
                    </c:otherwise>
                </c:choose>

            </form>

        </div>
    </div>

    <!-- Search -->
    <div class="card shadow-sm mb-4">
        <div class="card-body">

            <form action="${pageContext.request.contextPath}/doctors"
                  method="get" class="d-flex gap-2">

                <input type="text" name="search"
                       class="form-control"
                       placeholder="Search by name, specialization or license number"
                       value="<c:out value='${search}'/>">

                <button type="submit" class="btn btn-primary">
                    Search
                </button>

                <a href="${pageContext.request.contextPath}/doctors"
                   class="btn btn-secondary">Reset</a>

            </form>

        </div>
    </div>

    <!-- Doctor Records -->
    <div class="card shadow-sm">

        <div class="card-header bg-dark text-white">
            Doctor Records
        </div>

        <div class="card-body table-responsive">

            <table class="table table-bordered table-striped table-hover">

                <thead class="table-dark">
                    <tr>
                        <th>ID</th>
                        <th>Name</th>
                        <th>Specialization</th>
                        <th>Department</th>
                        <th>License Number</th>
                        <th>Consultation Fee</th>
                        <th>Actions</th>
                    </tr>
                </thead>

                <tbody>

                    <c:forEach var="doctor" items="${doctors}">

                        <tr>
                            <td><c:out value="${doctor.doctorId}"/></td>
                            <td><c:out value="${doctor.fullName}"/></td>
                            <td><c:out value="${doctor.specialization}"/></td>
                            <td><c:out value="${doctor.departmentName}"/></td>
                            <td><c:out value="${doctor.licenseNumber}"/></td>
                            <td>₹<c:out value="${doctor.consultationFee}"/></td>

                            <td>
                                <a href="${pageContext.request.contextPath}/doctors?editId=${doctor.doctorId}"
                                   class="btn btn-sm btn-warning mb-1">
                                    Edit
                                </a>

                                <form action="${pageContext.request.contextPath}/doctors"
                                      method="post"
                                      style="display:inline;"
                                      onsubmit="return confirm('Are you sure you want to delete this doctor?');">

                                    <input type="hidden" name="action"
                                           value="delete">

                                    <input type="hidden" name="doctorId"
                                           value="<c:out value='${doctor.doctorId}'/>">

                                    <button type="submit"
                                            class="btn btn-sm btn-danger mb-1">
                                        Delete
                                    </button>

                                </form>
                            </td>
                        </tr>

                    </c:forEach>

                    <c:if test="${empty doctors}">
                        <tr>
                            <td colspan="7" class="text-center">
                                No doctor records found.
                            </td>
                        </tr>
                    </c:if>

                </tbody>

            </table>

        </div>
    </div>

    <div class="text-center mt-4">
        <a href="${pageContext.request.contextPath}/patients"
           class="btn btn-outline-primary">Go to Patient Management</a>
    </div>

</div>

</body>
</html>
