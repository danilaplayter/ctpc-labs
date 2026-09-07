<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <link rel="stylesheet" type="text/css" href="${pageContext.servletContext.contextPath}/css/style.css"/>
    <title>Вход в систему</title>
</head>
<body>
<div class="container">
    <section id="content">
        <p><font color="red">${errorMessage}</font></p>
        <form action="${pageContext.servletContext.contextPath}/controller?command=login" method="POST">
            <h1>Вход в систему</h1>
            <div>
                <input placeholder="Имя" required="" id="username" name="loginName" type="text"/>
            </div>
            <div>
                <input placeholder="Пароль" required="" id="password" name="password" type="password"/>
            </div>
            <div>
                <input type="submit" value="Войти"/>
            </div>
            <div>
                <a href="${pageContext.servletContext.contextPath}/controller?command=registration_page">Регистрация</a>
            </div>
        </form>
    </section>
</div>
</body>
</html>