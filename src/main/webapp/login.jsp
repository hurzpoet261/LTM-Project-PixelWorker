<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Đăng nhập</title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>

    <div class="container">
        <h2>Đăng nhập</h2>
        
        <c:if test="${param.error != null}">
            <p style="color: red; text-align: center; font-weight: bold;">
                Tên đăng nhập hoặc mật khẩu không đúng!
            </p>
        </c:if>

        <form action="login" method="post">
            Tên đăng nhập: 
            <input type="text" name="username" required>
            
            Mật khẩu: 
            <input type="password" name="password" required>
            
            <input type="submit" value="Đăng nhập">
        </form>
        
        <p style="text-align: center; margin-top: 20px;">
            Chưa có tài khoản? <a href="register.jsp">Đăng ký ngay</a>
        </p>
    </div>

</body>
</html>