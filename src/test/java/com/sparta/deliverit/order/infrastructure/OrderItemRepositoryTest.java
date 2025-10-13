package com.sparta.deliverit.order.infrastructure;

import com.sparta.deliverit.order.domain.entity.OrderItem;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;
import java.util.Set;

@DataJpaTest
@ActiveProfiles("test")
@Sql({"/sql/user_restaurant_insert.sql", "/sql/menu_insert.sql", "/sql/order_insert.sql", "/sql/orderitem_insert.sql"})
class OrderItemRepositoryTest {

    @Autowired
    OrderItemRepository orderItemRepository;
    
    @DisplayName("주문과 관련된 주문아이템 목록을 가져온다.")
    @Test
    void findAllByOrderInTest() {
        // when
        List<OrderItem> orderItemList = orderItemRepository.findAllByOrderIn(Set.of("00000000-0000-0000-0000-000000000001", "00000000-0000-0000-0000-000000000002"));

        // then
        Assertions.assertThat(orderItemList).size().isEqualTo(4);
        Assertions.assertThat(orderItemList.get(0).getMenu().getId()).isEqualTo("3dfebfd6-c60c-4dcf-ab76-1946b3641dc7");
        Assertions.assertThat(orderItemList.get(0).getMenuNameSnapshot()).isEqualTo("짜장면");
        Assertions.assertThat(orderItemList.get(0).getMenuPriceSnapshot()).isEqualByComparingTo("6000");
        Assertions.assertThat(orderItemList.get(0).getQuantity()).isEqualTo(2);
        Assertions.assertThat(orderItemList.get(3).getMenu().getId()).isEqualTo("3f2faf2f-ec74-4e78-b3ea-1acbaf1cbd29");
        Assertions.assertThat(orderItemList.get(3).getMenuNameSnapshot()).isEqualTo("착한 탕수육");
        Assertions.assertThat(orderItemList.get(3).getMenuPriceSnapshot()).isEqualByComparingTo("21000");
        Assertions.assertThat(orderItemList.get(3).getQuantity()).isEqualTo(2);
    }
}