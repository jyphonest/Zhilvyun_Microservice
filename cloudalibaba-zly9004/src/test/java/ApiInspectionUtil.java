/**
 * @author 焦叶鹏
 * * @data 2025/8/20 15:50
 * @description: TODO
 **/
import io.restassured.RestAssured;
import io.restassured.specification.RequestSpecification;
import io.restassured.http.ContentType;
import org.springframework.boot.web.server.LocalServerPort;

import static io.restassured.RestAssured.given;

public class ApiInspectionUtil {

    private  String token;
    private  int port;

    public ApiInspectionUtil(int port) {
        this.port = port;
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = port;
        this.token = loginAndGetToken();
    }

    // 自动登录获取 token
    private String loginAndGetToken() {
        return given()
                .contentType(ContentType.JSON)
                .body("{\"accountNumber\":\"zhangsan\",\"password\":\"123456\"}")
                .when()
                .post("/userInformation/doLogin")
                .then()
                .statusCode(200)
                .extract()
                .jsonPath()
                .getString("data.token");
    }

    // 带认证请求
    public RequestSpecification withAuth() {
        return given().header("Authorization", "Bearer " + token);
    }

    // GET 请求
    public void get(String path) {
        withAuth()
                .when()
                .get(path)
                .then()
                .statusCode(200);
    }

    // POST 请求
    public void post(String path, String body) {
        withAuth()
                .contentType(ContentType.JSON)
                .body(body)
                .when()
                .post(path)
                .then()
                .statusCode(200);
    }

    // PUT 请求
    public void put(String path, String body) {
        withAuth()
                .contentType(ContentType.JSON)
                .body(body)
                .when()
                .put(path)
                .then()
                .statusCode(200);
    }

    // DELETE 请求
    public void delete(String path) {
        withAuth()
                .when()
                .delete(path)
                .then()
                .statusCode(200);
    }

}

