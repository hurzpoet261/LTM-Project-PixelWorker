<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Upload ảnh</title>
</head>
<body>
    <c:if test="${empty sessionScope.user}">
        <c:redirect url="login.jsp"/>
    </c:if>

    <h3>Chào, ${sessionScope.user.username}!</h3>
    <a href="logout">Đăng xuất</a> | <a href="history">Xem lịch sử xử lý ảnh</a>
    <hr>
    <h2>Upload ảnh để đóng dấu</h2>
    
    <form action="upload" method="post" enctype="multipart/form-data">
        Chọn ảnh (hoặc file ZIP): <input type="file" name="imageFile" accept="image/*,.zip">
        <input type="submit" value="Upload và Đóng dấu">
    </form>
</body>
</html>