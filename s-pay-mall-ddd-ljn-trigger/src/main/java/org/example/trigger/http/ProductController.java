package org.example.trigger.http;

import lombok.extern.slf4j.Slf4j;
import org.example.api.response.Response;
import org.example.domain.order.adapter.port.IProductPort;
import org.example.domain.order.model.entity.ProductEntity;
import org.example.types.common.Constants;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author dabuw
 * @description 商品查询接口（当前为演示目录，后续可替换为真实商品服务）
 * @date 8/11/26
 */
@Slf4j
@RestController
@CrossOrigin("*")
@RequestMapping("/api/v1/product/")
public class ProductController {

    @Resource
    private IProductPort productPort;

    /**
     * http://localhost:8080/api/v1/product/list
     */
    @GetMapping("list")
    public Response<List<ProductEntity>> queryProductList() {
        try {
            return Response.<List<ProductEntity>>builder()
                    .code(Constants.ResponseCode.SUCCESS.getCode())
                    .info(Constants.ResponseCode.SUCCESS.getInfo())
                    .data(productPort.queryProductList())
                    .build();
        } catch (Exception e) {
            log.error("查询商品列表失败", e);
            return Response.<List<ProductEntity>>builder()
                    .code(Constants.ResponseCode.UN_ERROR.getCode())
                    .info(Constants.ResponseCode.UN_ERROR.getInfo())
                    .build();
        }
    }

}
