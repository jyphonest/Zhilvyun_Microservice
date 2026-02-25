package com.atjiao.cloud.apis;

import com.atjiao.cloud.resp.ResultData;
import com.atjiao.cloud.resp.ReturnCodeEnum;
import org.springframework.stereotype.Component;

/**
 * @author jyp
 * * @data 2025/4/5
 **/
@Component
public class PayFeignSentinelApiFallBack implements PayFeignSentinelApi{

    @Override
    public ResultData getPayByOrderNo(String orderNo) {
        return ResultData.fail(ReturnCodeEnum.RC500.getCode(),"对方服务宕机或不可用，FallBack服务降级o(╥﹏╥)o");
    }
}
