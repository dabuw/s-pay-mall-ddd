package org.example.infrastructure.gateway;

import org.example.infrastructure.gateway.dto.ProductDTO;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class ProductRPC {

    /**
     * 演示用商品目录，后续可替换为真实的商品服务（RPC / DB）
     */
    private static final List<ProductDTO> PRODUCT_CATALOG = new ArrayList<>();

    static {
        PRODUCT_CATALOG.add(ProductDTO.builder()
                .productId("10000124")
                .productName("机械键盘 · SKU-001")
                .productDesc("87 键热插拔轴体，PBT 键帽，Type-C 有线连接")
                .price(new BigDecimal("1.68"))
                .build());
        PRODUCT_CATALOG.add(ProductDTO.builder()
                .productId("10000125")
                .productName("无线鼠标 · SKU-002")
                .productDesc("2.4G 无线连接，静音微动，续航 12 个月")
                .price(new BigDecimal("2.68"))
                .build());
        PRODUCT_CATALOG.add(ProductDTO.builder()
                .productId("10000126")
                .productName("桌面显示器支架 · SKU-003")
                .productDesc("铝合金材质，单臂悬停，适配 17-32 英寸显示器")
                .price(new BigDecimal("3.68"))
                .build());
    }

    public ProductDTO queryProductByProductId(String productId) {
        for (ProductDTO productDTO : PRODUCT_CATALOG) {
            if (productDTO.getProductId().equals(productId)) {
                return productDTO;
            }
        }
        // 未命中时返回第一个演示商品，保证联调不中断
        return PRODUCT_CATALOG.get(0);
    }

    public List<ProductDTO> queryProductList() {
        return new ArrayList<>(PRODUCT_CATALOG);
    }

}
