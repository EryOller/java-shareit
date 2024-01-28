package ru.practicum.shareit.request.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.ItemDtoRs;
import ru.practicum.shareit.request.ItemRequestController;
import ru.practicum.shareit.request.ItemRequestService;
import ru.practicum.shareit.request.dto.ItemRequestDtoRq;
import ru.practicum.shareit.request.dto.ItemRequestDtoRs;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemRequestController.class)
public class ItemRequestControllerTest {

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private ItemRequestService itemRequestService;

    @Autowired
    MockMvc mvc;

    ItemRequestDtoRq itemRequestDtoCreateTest;

    ItemRequestDtoRs itemRequestDtoCreated;

    ItemDtoRs itemDto;

    @BeforeEach
    void setUp() {
        itemDto = ItemDtoRs.builder()
                .id(1)
                .name("nameCreate")
                .description("create description")
                .available(true)
                .requestId(1)
                .build();

        itemRequestDtoCreateTest = ItemRequestDtoRq.builder()
                .description("need smth")
                .build();

        itemRequestDtoCreated = ItemRequestDtoRs.builder()
                .id(1)
                .description("need smth")
                .created(LocalDateTime.now())
                .build();
    }

    @AfterEach
    void tearDown() {
        itemRequestDtoCreateTest = null;
        itemRequestDtoCreated = null;
        itemDto = null;
    }

    @Test
    void create() throws Exception {
        when(itemRequestService.createItemRequest(2, itemRequestDtoCreateTest))
                .thenReturn(itemRequestDtoCreated);

        mvc.perform(post("/requests")
                        .content(mapper.writeValueAsString(itemRequestDtoCreateTest))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 2)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemRequestDtoCreated.getId()), Integer.class))
                .andExpect(jsonPath("$.description", is(itemRequestDtoCreated.getDescription())))
                .andExpect(jsonPath("$.created",
                        is(itemRequestDtoCreated.getCreated().format(DateTimeFormatter.ISO_DATE_TIME))));
    }

    @Test
    void getOwnRequest() throws Exception {
        itemRequestDtoCreated.setItems(List.of(itemDto));

        when(itemRequestService.getListItemRequest(2))
                .thenReturn(List.of(itemRequestDtoCreated));

        mvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", 2)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(itemRequestDtoCreated.getId()), Integer.class))
                .andExpect(jsonPath("$[0].description", is(itemRequestDtoCreated.getDescription())))
                .andExpect(jsonPath("$[0].created",
                        is(itemRequestDtoCreated.getCreated().format(DateTimeFormatter.ISO_DATE_TIME))))
                .andExpect(jsonPath("$[0].items[0].id",
                        is(itemRequestDtoCreated.getItems().get(0).getId()), Integer.class))
                .andExpect(jsonPath("$[0].items[0].name",
                        is(itemRequestDtoCreated.getItems().get(0).getName())))
                .andExpect(jsonPath("$[0].items[0].description",
                        is(itemRequestDtoCreated.getItems().get(0).getDescription())))
                .andExpect(jsonPath("$[0].items[0].available",
                        is(itemRequestDtoCreated.getItems().get(0).getAvailable())))
                .andExpect(jsonPath("$[0].items[0].requestId",
                        is(itemRequestDtoCreated.getItems().get(0).getRequestId()), Integer.class));
    }

    @Test
    void getAll() throws Exception {
        itemRequestDtoCreated.setItems(List.of(itemDto));

        when(itemRequestService.getListItemRequestWithPagination(3, 0, 10))
                .thenReturn(List.of(itemRequestDtoCreated));

        mvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", 3)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(itemRequestDtoCreated.getId()), Integer.class))
                .andExpect(jsonPath("$[0].description", is(itemRequestDtoCreated.getDescription())))
                .andExpect(jsonPath("$[0].created",
                        is(itemRequestDtoCreated.getCreated().format(DateTimeFormatter.ISO_DATE_TIME))))
                .andExpect(jsonPath("$[0].items[0].id",
                        is(itemRequestDtoCreated.getItems().get(0).getId()), Integer.class))
                .andExpect(jsonPath("$[0].items[0].name",
                        is(itemRequestDtoCreated.getItems().get(0).getName())))
                .andExpect(jsonPath("$[0].items[0].description",
                        is(itemRequestDtoCreated.getItems().get(0).getDescription())))
                .andExpect(jsonPath("$[0].items[0].available",
                        is(itemRequestDtoCreated.getItems().get(0).getAvailable())))
                .andExpect(jsonPath("$[0].items[0].requestId",
                        is(itemRequestDtoCreated.getItems().get(0).getRequestId()), Integer.class));
    }

    @Test
    void getRequestById() throws Exception {
        itemRequestDtoCreated.setItems(List.of(itemDto));

        when(itemRequestService.getItemRequestById(3, 1))
                .thenReturn(itemRequestDtoCreated);

        mvc.perform(get("/requests/1")
                        .header("X-Sharer-User-Id", 3)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemRequestDtoCreated.getId()), Integer.class))
                .andExpect(jsonPath("$.description", is(itemRequestDtoCreated.getDescription())))
                .andExpect(jsonPath("$.created",
                        is(itemRequestDtoCreated.getCreated().format(DateTimeFormatter.ISO_DATE_TIME))))
                .andExpect(jsonPath("$.items[0].id",
                        is(itemRequestDtoCreated.getItems().get(0).getId()), Integer.class))
                .andExpect(jsonPath("$.items[0].name",
                        is(itemRequestDtoCreated.getItems().get(0).getName())))
                .andExpect(jsonPath("$.items[0].description",
                        is(itemRequestDtoCreated.getItems().get(0).getDescription())))
                .andExpect(jsonPath("$.items[0].available",
                        is(itemRequestDtoCreated.getItems().get(0).getAvailable())));
    }
}
