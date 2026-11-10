<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">

    <title>Patient Management | Hospital Management System</title>

    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css"
          rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css"
          rel="stylesheet">

    <style>
        body {
            background: #f4f6f9;
            font-family: Arial, sans-serif;
        }

        .container {
            margin-top: 30px;
            margin-bottom: 40px;
        }

        .card {
            border: none;
            border-radius: 12px;
            box-shadow: 0 3px 12px rgba(0,0,0,0.08);
        }

        h2 {
            color: #164e63;
        }

        .table th {
            white-space: nowrap;
        }

        .google-btn {
            background-color: #ffffff;
            color: #3c4043;
            border: 1px solid #dadce0;
            font-weight: 500;
            transition: background-color .2s, box-shadow .2s;
        }

        .google-btn:hover {
            background-color: #f8f9fa;
            border-color: #dadce0;
            color: #202124;
            box-shadow: 0 1px 3px rgba(60,64,67,.3);
        }

        .avatar-img {
            width: 48px;
            height: 48px;
            border-radius: 50%;
            object-fit: cover;
        }

        .table-avatar {
            width: 32px;
            height: 32px;
            border-radius: 50%;
            object-fit: cover;
        }
    </style>
</head>

<body>

<div class="container">

    <!-- TOP HEADER & NAVIGATION -->
    <div class="d-flex justify-content-between align-items-center mb-4 flex-wrap gap-2">
        <div>
            <h2 class="mb-0">Hospital Management System</h2>
            <p class="text-muted small mb-0">Patient Registration and Records</p>
        </div>
        <div class="d-flex gap-2">
            <a href="${pageContext.request.contextPath}/index.jsp" class="btn btn-outline-secondary btn-sm">
                <i class="bi bi-house-door"></i> Home
            </a>
            <a href="${pageContext.request.contextPath}/doctors" class="btn btn-outline-primary btn-sm">
                <i class="bi bi-person-badge"></i> Doctors
            </a>
        </div>
    </div>

    <!-- FLASH NOTIFICATIONS -->
    <c:if test="${not empty patientSuccess}">
        <div class="alert alert-success alert-dismissible fade show shadow-sm" role="alert">
            <i class="bi bi-check-circle-fill me-2"></i>
            <c:out value="${patientSuccess}"/>
            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
        </div>
    </c:if>

    <c:if test="${not empty patientWarning}">
        <div class="alert alert-warning alert-dismissible fade show shadow-sm" role="alert">
            <i class="bi bi-exclamation-triangle-fill me-2"></i>
            <c:out value="${patientWarning}"/>
            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
        </div>
    </c:if>

    <c:if test="${not empty patientError}">
        <div class="alert alert-danger alert-dismissible fade show shadow-sm" role="alert">
            <i class="bi bi-x-circle-fill me-2"></i>
            <c:out value="${patientError}"/>
            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
        </div>
    </c:if>

    <!-- PATIENT REGISTRATION / EDIT FORM -->
    <div class="card p-4 mb-4">

        <c:choose>
            <c:when test="${not empty patientToEdit}">
                <h4 class="mb-3 text-warning-emphasis">
                    <i class="bi bi-pencil-square me-2"></i>Edit Patient
                </h4>
            </c:when>

            <c:otherwise>
                <h4 class="mb-3">
                    <i class="bi bi-person-plus-fill me-2 text-primary"></i>Register New Patient
                </h4>

                <!-- GOOGLE OAUTH SECTION -->
                <c:choose>
                    <c:when test="${not empty googleUser}">
                        <!-- Google Verified User Banner -->
                        <div class="card border-primary-subtle bg-primary-subtle bg-opacity-25 p-3 mb-4">
                            <div class="d-flex align-items-center justify-content-between flex-wrap gap-2">
                                <div class="d-flex align-items-center gap-3">
                                    <c:choose>
                                        <c:when test="${not empty googleUser.picture}">
                                            <img src="${googleUser.picture}" alt="Google Profile" class="avatar-img border border-2 border-primary shadow-sm" referrerpolicy="no-referrer">
                                        </c:when>
                                        <c:otherwise>
                                            <div class="rounded-circle bg-primary text-white d-flex align-items-center justify-content-center avatar-img">
                                                <i class="bi bi-person fs-4"></i>
                                            </div>
                                        </c:otherwise>
                                    </c:choose>
                                    <div>
                                        <div class="fw-bold text-dark d-flex align-items-center gap-2">
                                            <c:out value="${googleUser.name}"/>
                                            <span class="badge bg-success text-white small">
                                                <i class="bi bi-patch-check-fill me-1"></i>Google Verified
                                            </span>
                                        </div>
                                        <div class="text-muted small"><c:out value="${googleUser.email}"/></div>
                                    </div>
                                </div>
                                <div>
                                    <a href="${pageContext.request.contextPath}/oauth2/google/clear"
                                       class="btn btn-outline-secondary btn-sm">
                                        <i class="bi bi-x-circle me-1"></i>Cancel Google Sign-In
                                    </a>
                                </div>
                            </div>
                        </div>
                    </c:when>

                    <c:otherwise>
                        <!-- Continue with Google Button -->
                        <div class="text-center my-2">
                            <a href="${pageContext.request.contextPath}/oauth2/google/login"
                               class="btn google-btn d-inline-flex align-items-center gap-2 py-2 px-4 rounded-pill shadow-sm">
                                <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 48 48">
                                    <path fill="#EA4335" d="M24 9.5c3.54 0 6.71 1.22 9.21 3.6l6.85-6.85C35.9 2.38 30.47 0 24 0 14.62 0 6.51 5.38 2.56 13.22l7.98 6.19C12.43 13.72 17.74 9.5 24 9.5z"/>
                                    <path fill="#4285F4" d="M46.98 24.55c0-1.57-.15-3.09-.38-4.55H24v9.02h12.94c-.58 2.96-2.26 5.48-4.78 7.18l7.73 6c4.51-4.18 7.09-10.36 7.09-17.65z"/>
                                    <path fill="#FBBC05" d="M10.53 28.59c-.48-1.45-.76-2.99-.76-4.59s.27-3.14.76-4.59l-7.98-6.19C.92 16.46 0 20.12 0 24c0 3.88.92 7.54 2.56 10.78l7.97-6.19z"/>
                                    <path fill="#34A853" d="M24 48c6.48 0 11.93-2.13 15.89-5.81l-7.73-6c-2.15 1.45-4.92 2.3-8.16 2.3-6.26 0-11.57-4.22-13.47-9.91l-7.98 6.19C6.51 42.62 14.62 48 24 48z"/>
                                </svg>
                                <span>Continue with Google</span>
                            </a>
                            <div class="position-relative my-3">
                                <hr class="border-secondary-subtle">
                                <span class="position-absolute top-50 start-50 translate-middle bg-white px-3 text-muted small">
                                    or enter details manually
                                </span>
                            </div>
                        </div>
                    </c:otherwise>
                </c:choose>
            </c:otherwise>
        </c:choose>

        <form action="${pageContext.request.contextPath}/patients"
              method="post"
              id="patientForm">

            <!-- Hidden fields used during editing -->
            <c:if test="${not empty patientToEdit}">
                <input type="hidden" name="action" value="update">
                <input type="hidden" name="patientId" value="<c:out value='${patientToEdit.patientId}'/>">
            </c:if>

            <div class="row">

                <!-- FULL NAME (Pre-filled from Google if available) -->
                <div class="col-md-6 mb-3">
                    <label class="form-label fw-semibold">Full Name <span class="text-danger">*</span></label>
                    <input type="text"
                           name="fullName"
                           id="fullNameInput"
                           class="form-control"
                           maxlength="150"
                           value="<c:out value='${not empty patientToEdit ? patientToEdit.fullName : (not empty googleUser ? googleUser.name : "")}'/>"
                           required>
                </div>

                <!-- EMAIL (From Google or Read-only) -->
                <c:choose>
                    <c:when test="${not empty googleUser}">
                        <div class="col-md-6 mb-3">
                            <label class="form-label fw-semibold">
                                Email <span class="badge bg-success-subtle text-success ms-1">Google Verified</span>
                            </label>
                            <input type="email"
                                   class="form-control bg-light"
                                   value="<c:out value='${googleUser.email}'/>"
                                   readonly>
                        </div>
                    </c:when>
                    <c:when test="${not empty patientToEdit.email}">
                        <div class="col-md-6 mb-3">
                            <label class="form-label fw-semibold">Linked Email</label>
                            <input type="email"
                                   class="form-control bg-light"
                                   value="<c:out value='${patientToEdit.email}'/>"
                                   readonly>
                        </div>
                    </c:when>
                </c:choose>

                <!-- GENDER -->
                <div class="col-md-6 mb-3">
                    <label class="form-label fw-semibold">Gender <span class="text-danger">*</span></label>
                    <select name="gender"
                            class="form-select"
                            required>
                        <option value="">Select Gender</option>
                        <option value="Male" <c:if test="${patientToEdit.gender eq 'Male'}">selected</c:if>>Male</option>
                        <option value="Female" <c:if test="${patientToEdit.gender eq 'Female'}">selected</c:if>>Female</option>
                        <option value="Other" <c:if test="${patientToEdit.gender eq 'Other'}">selected</c:if>>Other</option>
                    </select>
                </div>

                <!-- DATE OF BIRTH with Dynamic Age Calculation -->
                <div class="col-md-6 mb-3">
                    <div class="d-flex justify-content-between align-items-center mb-1">
                        <label class="form-label fw-semibold mb-0">Date of Birth</label>
                        <span id="calculatedAgeBadge" class="badge bg-info-subtle text-dark border border-info" style="display:none;">
                            <i class="bi bi-hourglass-split me-1"></i>Age: <strong id="calculatedAgeText"></strong>
                        </span>
                    </div>
                    <input type="date"
                           name="dateOfBirth"
                           id="dateOfBirthInput"
                           class="form-control"
                           value="${patientToEdit.dateOfBirth}">
                </div>

                <!-- PHONE / CONTACT NUMBER -->
                <div class="col-md-6 mb-3">
                    <label class="form-label fw-semibold">Contact Number <span class="text-danger">*</span></label>
                    <input type="tel"
                           name="phone"
                           class="form-control"
                           maxlength="15"
                           value="<c:out value='${patientToEdit.phone}'/>"
                           placeholder="e.g. 9876543210"
                           required>
                </div>

                <!-- ADDRESS -->
                <div class="col-12 mb-3">
                    <label class="form-label fw-semibold">Address</label>
                    <textarea name="address"
                              class="form-control"
                              maxlength="255"
                              rows="2"
                              placeholder="Enter residential address"><c:out value="${patientToEdit.address}"/></textarea>
                </div>

            </div>

            <!-- FORM BUTTONS -->
            <c:choose>
                <c:when test="${not empty patientToEdit}">
                    <button type="submit" class="btn btn-warning px-4">
                        <i class="bi bi-check-lg me-1"></i>Update Patient
                    </button>
                    <a href="${pageContext.request.contextPath}/patients" class="btn btn-secondary px-4 ms-2">
                        Cancel
                    </a>
                </c:when>

                <c:otherwise>
                    <button type="submit" class="btn btn-primary px-4">
                        <i class="bi bi-person-check me-1"></i>Register Patient
                    </button>
                </c:otherwise>
            </c:choose>

        </form>

    </div>

    <!-- PATIENT RECORDS -->
    <div class="card p-4">

        <h4 class="mb-3">
            <i class="bi bi-table me-2 text-primary"></i>Patient Records
        </h4>

        <!-- SEARCH FORM -->
        <form action="${pageContext.request.contextPath}/patients"
              method="get"
              class="d-flex gap-2 mb-3">

            <input type="text"
                   name="search"
                   class="form-control"
                   placeholder="Search by patient name, phone or email"
                   value="<c:out value='${search}'/>">

            <button type="submit" class="btn btn-primary">
                <i class="bi bi-search me-1"></i>Search
            </button>

            <a href="${pageContext.request.contextPath}/patients" class="btn btn-secondary">
                Reset
            </a>
        </form>

        <!-- PATIENT TABLE -->
        <div class="table-responsive">
            <table class="table table-bordered table-striped align-middle">
                <thead class="table-dark">
                    <tr>
                        <th>ID</th>
                        <th>Patient Name</th>
                        <th>Gender</th>
                        <th>Date of Birth / Age</th>
                        <th>Phone</th>
                        <th>Address</th>
                        <th>Account</th>
                        <th>Actions</th>
                    </tr>
                </thead>

                <tbody>
                    <c:forEach var="patient" items="${patients}">
                        <tr>
                            <td><c:out value="${patient.patientId}"/></td>

                            <td>
                                <div class="d-flex align-items-center gap-2">
                                    <c:choose>
                                        <c:when test="${not empty patient.profilePicture}">
                                            <img src="${patient.profilePicture}" alt="Avatar" class="table-avatar border" referrerpolicy="no-referrer">
                                        </c:when>
                                        <c:otherwise>
                                            <div class="table-avatar bg-secondary-subtle text-secondary d-flex align-items-center justify-content-center rounded-circle small">
                                                <i class="bi bi-person-fill"></i>
                                            </div>
                                        </c:otherwise>
                                    </c:choose>
                                    <div>
                                        <div class="fw-semibold"><c:out value="${patient.fullName}"/></div>
                                        <c:if test="${not empty patient.email}">
                                            <div class="text-muted small"><c:out value="${patient.email}"/></div>
                                        </c:if>
                                    </div>
                                </div>
                            </td>

                            <td><c:out value="${patient.gender}"/></td>

                            <td>
                                <c:choose>
                                    <c:when test="${not empty patient.dateOfBirth}">
                                        <c:out value="${patient.dateOfBirth}"/>
                                        <c:if test="${not empty patient.age}">
                                            <span class="badge bg-secondary-subtle text-dark border ms-1">
                                                ${patient.age} yrs
                                            </span>
                                        </c:if>
                                    </c:when>
                                    <c:otherwise>
                                        <span class="text-muted small">Not provided</span>
                                    </c:otherwise>
                                </c:choose>
                            </td>

                            <td><c:out value="${patient.phone}"/></td>

                            <td><c:out value="${patient.address}"/></td>

                            <td>
                                <c:choose>
                                    <c:when test="${not empty patient.googleId}">
                                        <span class="badge bg-success-subtle text-success border border-success-subtle">
                                            <i class="bi bi-google me-1"></i>Google
                                        </span>
                                    </c:when>
                                    <c:otherwise>
                                        <span class="badge bg-light text-muted border">
                                            Manual
                                        </span>
                                    </c:otherwise>
                                </c:choose>
                            </td>

                            <td>
                                <div class="d-flex gap-1">
                                    <a href="${pageContext.request.contextPath}/patients?editId=${patient.patientId}"
                                       class="btn btn-sm btn-warning">
                                        Edit
                                    </a>

                                    <form action="${pageContext.request.contextPath}/patients"
                                          method="post"
                                          style="display:inline;"
                                          onsubmit="return confirm('Are you sure you want to delete this patient?');">
                                        <input type="hidden" name="action" value="delete">
                                        <input type="hidden" name="patientId" value="<c:out value='${patient.patientId}'/>">
                                        <button type="submit" class="btn btn-sm btn-danger">
                                            Delete
                                        </button>
                                    </form>
                                </div>
                            </td>
                        </tr>
                    </c:forEach>

                    <c:if test="${empty patients}">
                        <tr>
                            <td colspan="8" class="text-center text-muted py-4">
                                <i class="bi bi-inbox fs-3 d-block mb-1"></i>
                                No patient records found.
                            </td>
                        </tr>
                    </c:if>
                </tbody>
            </table>
        </div>

    </div>

</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>

<!-- Dynamic Age Calculation Script -->
<script>
    (function () {
        const dobInput = document.getElementById('dateOfBirthInput');
        const badge = document.getElementById('calculatedAgeBadge');
        const text = document.getElementById('calculatedAgeText');

        function updateAge() {
            if (!dobInput || !dobInput.value) {
                if (badge) badge.style.display = 'none';
                return;
            }
            const dob = new Date(dobInput.value);
            if (isNaN(dob.getTime())) {
                if (badge) badge.style.display = 'none';
                return;
            }
            const today = new Date();
            let age = today.getFullYear() - dob.getFullYear();
            const monthDiff = today.getMonth() - dob.getMonth();
            if (monthDiff < 0 || (monthDiff === 0 && today.getDate() < dob.getDate())) {
                age--;
            }

            if (age >= 0 && age <= 130) {
                text.textContent = age + ' yrs';
                badge.style.display = 'inline-block';
            } else {
                badge.style.display = 'none';
            }
        }

        if (dobInput) {
            dobInput.addEventListener('input', updateAge);
            dobInput.addEventListener('change', updateAge);
            updateAge();
        }
    })();
</script>

</body>
</html>
