<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%-- Đây là URI cho JSTL 3.0 (tương thích Tomcat 10) --%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Lịch sử xử lý ảnh</title>
    <meta http-equiv="refresh" content="10">
</head>
<body>
    <c:if test="${empty sessionScope.user}">
        <c:redirect url="login.jsp"/>
    </c:if>

    <h3>Chào, ${sessionScope.user.username}!</h3>
    <a href="logout">Đăng xuất</a> | <a href="upload.jsp">Upload ảnh mới</a>
    <hr>
    <h2>Lịch sử tác vụ</h2>
    <table border="1" width="100%" style="border-collapse: collapse;">
        <tr style="background-color: #f2f2f2;">
            <th>ID</th>
            <th>Tên file gốc</th>
            <th>Trạng thái</th>
            <th>Kết quả</th>
        </tr>
        <c:forEach var="job" items="${requestScope.jobs}">
            <tr>
                <td>${job.id}</td>
                <td>${job.originalFilename}</td>
                <td>
                    <c:choose>
                        <c:when test="${job.status == 'PENDING'}">Đang chờ...</c:when>
                        <c:when test="${job.status == 'PROCESSING'}">Đang xử lý...</c:when>
                        <c:when test="${job.status == 'COMPLETED'}">Hoàn thành</c:when>
                        <c:when test="${job.status == 'FAILED'}">Thất bại</c:when>
                    </c:choose>
                </td>
                <td>
                    <c:if test="${job.status == 'COMPLETED'}">
                        Đã lưu tại: ${job.watermarkedPath}
                    </c:if>
                </td>
            </tr>
        </c:forEach>
    </table>
</body>
</html>