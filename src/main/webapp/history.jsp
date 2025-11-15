<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Lịch sử xử lý ảnh</title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>

    <c:if test="${empty sessionScope.user}">
        <c:redirect url="login.jsp"/>
    </c:if>

    <div class="container">
        <div style="display: flex; justify-content: space-between; align-items: center; border-bottom: 1px solid #eee; padding-bottom: 10px;">
            <h3>Chào, ${sessionScope.user.username}!</h3>
            <div>
                <a href="upload.jsp" class="button">Upload ảnh mới</a>
                <a href="logout" class="button" style="background-color: #dc3545;">Đăng xuất</a>
            </div>
        </div>

        <h2 style="margin-top: 30px;">Lịch sử tác vụ</h2>
        
        <table>
            <thead>
                <tr>
                    <th>ID</th>
                    <th>File Đầu Vào</th>
                    <th>Loại Job</th>
                    <th>Trạng Thái</th>
                    <th>Kết Quả</th>
                </tr>
            </thead>
            <tbody>
                <c:if test="${empty requestScope.jobs}">
                    <tr>
                        <td colspan="5" style="text-align: center;">Bạn chưa upload file nào.</td>
                    </tr>
                </c:if>

                <c:forEach var="job" items="${requestScope.jobs}">
                    <tr id="job-row-${job.id}" data-job-status="${job.status}">
                        <td>${job.id}</td>
                        <td>${job.inputFilename}</td>
                        <td>${job.jobType}</td>
                        
                        <td class="status-cell">
                            <c:choose>
                                <c:when test="${job.status == 'PENDING'}">
                                    <span class="status-pending">Đang chờ...</span>
                                </c:when>
                                <c:when test="${job.status == 'PROCESSING'}">
                                    <span class="status-processing">Đang xử lý...</span>
                                </c:when>
                                <c:when test="${job.status == 'COMPLETED'}">
                                    <span class="status-completed">Hoàn thành</span>
                                </c:when>
                                <c:when test="${job.status == 'FAILED'}">
                                    <span class="status-failed">Thất bại</span>
                                </c:when>
                            </c:choose>
                        </td>
                        
                        <td class="result-cell">
                            <c:if test="${job.status == 'COMPLETED'}">
                                <a href="${pageContext.request.contextPath}/download?jobId=${job.id}" class="button">Tải về</a>
                            </c:if>
                        </td>
                    </tr>
                </c:forEach>
            </tbody>
        </table>
    </div>

    <script>
        const checkJobsInterval = setInterval(checkPendingJobs, 5000);

        function checkPendingJobs() {
            const rowsToCheck = document.querySelectorAll('tr[data-job-status="PENDING"], tr[data-job-status="PROCESSING"]');
            
            if (rowsToCheck.length === 0) {
                return;
            }

            rowsToCheck.forEach(row => {
                const jobId = row.id.replace('job-row-', '');
                
                fetch('${pageContext.request.contextPath}/jobStatus?jobId=' + jobId)
                    .then(response => {
                        if (!response.ok) {
                            throw new Error('Network response was not ok');
                        }
                        return response.json();
                    })
                    .then(data => {
                        updateRow(row, data);
                    })
                    .catch(error => {
                        console.error('Lỗi khi kiểm tra job ' + jobId + ':', error);
                    });
            });
        }
        
        function updateRow(row, data) {
            if (row.dataset.jobStatus === data.status) {
                return; 
            }
            
            row.dataset.jobStatus = data.status; 
            const statusCell = row.querySelector('.status-cell');
            const resultCell = row.querySelector('.result-cell');

            if (data.status === 'COMPLETED') {
                statusCell.innerHTML = '<span class="status-completed">Hoàn thành</span>';
                resultCell.innerHTML = `<a href="${data.downloadUrl}" class="button">Tải về</a>`;
            } else if (data.status === 'FAILED') {
                statusCell.innerHTML = '<span class="status-failed">Thất bại</span>';
                resultCell.innerHTML = ''; 
            } else if (data.status === 'PROCESSING') {
                 statusCell.innerHTML = '<span class="status-processing">Đang xử lý...</span>';
            }
        }
        
        document.addEventListener('DOMContentLoaded', checkPendingJobs);
    </script>
</body>
</html>