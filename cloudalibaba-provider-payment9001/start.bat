@echo off
echo 正在启动AI旅游云平台 - 旅游服务提供者...
echo 端口: 9001
echo 数据库: zhilvyun_jiaoyp
echo.

REM 检查Java环境
java -version >nul 2>&1
if errorlevel 1 (
    echo 错误: 未找到Java环境，请确保已安装JDK 17+
    pause
    exit /b 1
)

REM 检查Maven环境
mvn -version >nul 2>&1
if errorlevel 1 (
    echo 错误: 未找到Maven环境，请确保已安装Maven 3.6+
    pause
    exit /b 1
)

echo 正在编译项目...
call mvn clean compile -q

if errorlevel 1 (
    echo 错误: 项目编译失败
    pause
    exit /b 1
)

echo 正在启动服务...
call mvn spring-boot:run -Dspring-boot.run.jvmArguments="-Xms512m -Xmx1024m"

pause 