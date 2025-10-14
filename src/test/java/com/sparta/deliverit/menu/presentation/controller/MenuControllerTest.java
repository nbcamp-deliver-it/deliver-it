package com.sparta.deliverit.menu.presentation.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparta.deliverit.global.exception.GlobalExceptionHandler;
import com.sparta.deliverit.global.exception.MenuException;
import com.sparta.deliverit.global.exception.RestaurantException;
import com.sparta.deliverit.menu.application.service.MenuService;
import com.sparta.deliverit.menu.domain.entity.Menu;
import com.sparta.deliverit.menu.domain.entity.MenuStatus;
import com.sparta.deliverit.menu.presentation.dto.MenuUpdateRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.math.BigDecimal;
import java.util.List;

import static com.sparta.deliverit.global.response.code.MenuResponseCode.MENU_DUPLICATED;
import static com.sparta.deliverit.global.response.code.MenuResponseCode.REQUEST_EMPTY_LIST;
import static com.sparta.deliverit.global.response.code.RestaurantResponseCode.RESTAURANT_NOT_FOUND;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MenuController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
class MenuControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private MenuService menuService;

    @Test
    @DisplayName("메뉴 조회 성공")
    void successGetMenuByRestaurantId() throws Exception {
        List<Menu> menuList = List.of(
                Menu.builder()
                        .id("1")
                        .name("파스타")
                        .price(BigDecimal.valueOf(10000))
                        .status(MenuStatus.SELLING)
                        .build()
        );

        when(menuService.getMenuByRestaurantId("1")).thenReturn(menuList);

        mockMvc.perform(MockMvcRequestBuilders.get("/v1/restaurants/{restaurantId}/menu", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("파스타"));

        verify(menuService).getMenuByRestaurantId("1");
    }

    @Test
    @DisplayName("메뉴 조회 실패 : 없는 식당 아이디로 요청")
    void failCreateMenuIem_notExistRestaurantId() throws Exception {
        when(menuService.getMenuByRestaurantId("invalidId"))
                .thenThrow(new RestaurantException(RESTAURANT_NOT_FOUND));

        mockMvc.perform(get("/v1/restaurants/{restaurantId}/menu", "invalidId"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("존재하지 않는 식당입니다."));
    }

    @Test
    @DisplayName("메뉴 생성 성공")
    void successCreateMenuItem() throws Exception {
        List<Menu> menuList = List.of(
                Menu.builder()
                        .id("1")
                        .name("파스타")
                        .price(BigDecimal.valueOf(10000))
                        .status(MenuStatus.SELLING)
                        .build()
        );

        List<String> menuIdList = List.of("1", "2");

        doNothing().when(menuService).deleteMenuItem(eq("1"), eq(menuIdList));

        mockMvc.perform(post("/v1/restaurants/{restaurantId}/menu", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(menuList)))
                .andExpect(status().isCreated());

        Mockito.verify(menuService).createMenuItem(eq("1"), any(List.class));
    }

    @Test
    @DisplayName("메뉴 생성 실패 : 중복된 메뉴 요청")
    void failCreateMenuItem_duplicateMenu() throws Exception {
        List<Menu> menuList = List.of(
                Menu.builder()
                        .id("1")
                        .name("카레")
                        .build()
        );

        doThrow(new MenuException(MENU_DUPLICATED))
                .when(menuService).createMenuItem(anyString(), anyList());

        mockMvc.perform(post("/v1/restaurants/{restaurantId}/menu", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(menuList)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("메뉴 삭제 성공")
    void successDeleteMenuItem() throws Exception{
        List<String> menuIdList = List.of("1", "2");

        doNothing().when(menuService).deleteMenuItem(eq("1"), eq(menuIdList));

        mockMvc.perform(delete("/v1/restaurants/{restaurantId}/menu", "1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(menuIdList)))
                .andExpect(status().isNoContent());

        verify(menuService).deleteMenuItem(eq("1"), eq(menuIdList));
    }

    @Test
    @DisplayName("메뉴 삭제 실패 : 메뉴 아이디 리스트가 비어있을 때")
    void failDeleteMenuItem_emptyMenuIdList() throws Exception {
        List<String> emptyList = List.of();

        doThrow(new MenuException(REQUEST_EMPTY_LIST))
                .when(menuService).deleteMenuItem(anyString(), anyList());

        mockMvc.perform(delete("/v1/restaurants/{restaurantId}/menu", "1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(emptyList)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("메뉴 수정 성공")
    void successUpdateMenuItem() throws Exception {
        List<MenuUpdateRequest> updateList = List.of(
                MenuUpdateRequest.builder()
                        .id("1")
                        .name("치킨 카레")
                        .price(BigDecimal.valueOf(12000))
                        .description("치킨이 들어간 카레입니다.")
                        .status(MenuStatus.SELLING)
                        .build()
        );

        doNothing().when(menuService).updateMenuItem(eq("1"), any(List.class));

        mockMvc.perform(patch("/v1/restaurants/{restaurantId}/menu", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateList)))
                .andExpect(status().isNoContent());

        Mockito.verify(menuService).updateMenuItem(eq("1"), any(List.class));
    }

    @Test
    @DisplayName("메뉴 수정 실패 : 빈 메뉴 수정 요청")
    void failUpdateMenuItem_emptyMenuUpdateRequest() throws Exception {
        List<MenuUpdateRequest> reqList = List.of();

        doThrow(new MenuException(REQUEST_EMPTY_LIST))
                .when(menuService).updateMenuItem(anyString(), anyList());

        mockMvc.perform(patch("/v1/restaurants/{restaurantId}/menu", "1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(reqList)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("메뉴 수정 실패 : 잘못된 메뉴 수정 요청")
    void failUpdateMenuItem_invalidMenuUpdateRequest() throws Exception {
        List<MenuUpdateRequest> reqList = List.of(
                MenuUpdateRequest.builder()
                        .id("1")
                        .name("소고기 카레")
                        .price(BigDecimal.valueOf(10000))
                        .status(MenuStatus.SELLING)
                        .build()
        );

        doThrow(new MenuException(REQUEST_EMPTY_LIST))
                .when(menuService).updateMenuItem(anyString(), anyList());

        mockMvc.perform(patch("/v1/restaurants/{restaurantId}/menu", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reqList)))
                .andExpect(status().isBadRequest());
    }
}