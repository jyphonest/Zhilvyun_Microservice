import com.atjiao.cloud.Main9004;
import io.qameta.allure.Description;
import io.qameta.allure.Feature;
import io.qameta.allure.Step;
import io.qameta.allure.Story;
import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import io.restassured.http.ContentType;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.test.context.TestPropertySource;

/**
 * @author 焦叶鹏
 * * @data 2025/8/1 10:37
 * @description: zly9004自动化接口测试
 **/
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = Main9004.class)
@TestPropertySource(locations = "classpath:bootstrap.yml")
@Feature("商品管理模块") // 功能模块描述
public class Zly9004Test {
    @LocalServerPort
    private int port; // 注入随机端口

    private ApiInspectionUtil apiUtil;

    @BeforeEach
    void setUp() {
        apiUtil = new ApiInspectionUtil(port);
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = port;
    }

    @Test
    @DisplayName("巡检商品查询接口测试")
    @Story("商品查询功能") // 具体功能点
    @Description("测试商品列表查询接口，验证是否能正确返回分页商品数据。" +
            "请求参数：pageNum=1，pageSize=5，无请求体。" +
            "预期结果：接口返回200状态码，包含正确的分页信息和商品列表")
    void testGoodsInfoList() {
        queryGoodsList(1, 5);
    }

    @Test
    @DisplayName("巡检商品新增接口测试")
    @Story("商品新增功能") // 具体功能点
    @Description("测试商品新增接口，验证是否能正确添加商品信息到系统。" +
            "请求参数：包含商品名称、价格、图片、库存、描述、状态和分类编码等信息。" +
            "预期结果：接口返回200状态码，返回成功信息，新商品被正确添加到系统")
    void testGoodsInfoAdd() {
        addNewGoods("张丽华", 25.08,
                "https://loremflickr.com/400/400?lock=2534256647839750",
                97, "爱过后是。阿萨帝哦吼。就是哈哈安徽设定。大气司机好哦。关反律劳声科无光想。撒都啊。耦合好。",
                1, 401);
    }

    @Step("查询商品列表，页码：{pageNum}，每页条数：{pageSize}") // 测试步骤描述
    private void queryGoodsList(int pageNum, int pageSize) {
        apiUtil.post("/goodsInfo/list?pageNum=" + pageNum + "&pageSize=" + pageSize, "{}");
    }

    @Step("新增商品，名称：{goodsName}，价格：{goodsPrice}，分类：{categoryCode}") // 测试步骤描述
    private void addNewGoods(String goodsName, double goodsPrice, String goodsImage,
                             int goodsStock, String goodsDescription, int goodsStatus,
                             int categoryCode) {
        String requestBody = String.format("""
                {
                    "goodsName": "%s",
                    "goodsPrice": %s,
                    "goodsImage": "%s",
                    "goodsStock": %d,
                    "goodsDescription": "%s",
                    "goodsStatus": %d,
                    "categoryCode": %d
                }
                """, goodsName, goodsPrice, goodsImage, goodsStock, goodsDescription, goodsStatus, categoryCode);

        apiUtil.post("/goodsInfo/saveOrUpdate", requestBody);
    }
}