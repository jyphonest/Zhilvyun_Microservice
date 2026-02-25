import com.atjiao.cloud.Main9004;
import com.atjiao.cloud.rabbitMq.RabbitMQTest.consumer.RabbitConsumer1;
import com.atjiao.cloud.rabbitMq.RabbitMQTest.consumer.RabbitConsumer2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author 焦叶鹏
 * * @data 2025/10/11 08:40
 * @description: 消费者测试类
 **/


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,classes = Main9004.class)
public class RabbitMqConsumerTest {

    //注入消费者1组件
    @Autowired
    private RabbitConsumer1 consumer1;

    //注入消费者2组件
    @Autowired
    private RabbitConsumer2 consumer2;

}
