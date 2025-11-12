<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Đăng ký</title>
</head>
<body>
    <h2>Đăng ký</h2>
    <form action="register" method="post">
        Tên đăng nhập: <input type="text" name="username"><br/>
        Mật khẩu: <input type="password" name="password"><br/>
        <input type="submit" value="Đăng ký">
    </form>
    <p><a href="login.jsp">Quay lại đăng nhập</a></p>
</body>
</html>