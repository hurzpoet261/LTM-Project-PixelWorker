<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Đăng nhập</title>
</head>
<body>
    <h2>Đăng nhập</h2>
    <form action="login" method="post">
        Tên đăng nhập: <input type="text" name="username"><br/>
        Mật khẩu: <input type="password" name="password"><br/>
        <input type="submit" value="Đăng nhập">
    </form>
    <p><a href="register.jsp">Đăng ký tài khoản mới</a></p>
</body>
</html>