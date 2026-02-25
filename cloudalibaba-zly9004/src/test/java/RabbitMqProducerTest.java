import com.atjiao.cloud.Main9004;
import com.atjiao.cloud.rabbitMq.RabbitMQTest.producer.RabbitProducer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author 焦叶鹏
 * * @data 2025/10/9 11:28
 * @description: 生产者测试类
 **/

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,classes = Main9004.class)
public class RabbitMqProducerTest {

    // 注入生产者组件
    @Autowired
    private RabbitProducer producer;

    @Test
    public void test01SendMessage() throws InterruptedException {
        // 调用生产者发送消息
        producer.sendFanoutMessage();
        Thread.sleep(1000*15);
    }

}
