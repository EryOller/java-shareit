package ru.practicum.shareit.item.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.ItemController;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.comment.dto.CommentDtoRq;
import ru.practicum.shareit.item.comment.dto.CommentDtoRs;
import ru.practicum.shareit.item.dto.ItemDtoRs;
import ru.practicum.shareit.item.dto.ItemSaveDtoRq;
import ru.practicum.shareit.item.dto.ItemUpdateDtoRq;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemController.class)
public class ItemControllerTest {
    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private ItemService itemService;
    @Autowired
    private MockMvc mvc;

    private ItemSaveDtoRq itemDtoCreateTest;

    private ItemDtoRs itemDtoCreated;

    private ItemUpdateDtoRq itemDtoUpdateTest;

    private ItemDtoRs itemDtoUpdated;

    private CommentDtoRq commentDtoCreateTest;

    private CommentDtoRs commentDtoCreated;

    @BeforeEach
    void setUp() {
        commentDtoCreateTest = CommentDtoRq.builder()
                .text("comment")
                .authorId(1)
                .itemId(1)
                .build();

        commentDtoCreated = CommentDtoRs.builder()
                .id(1)
                .text("comment")
                .authorName("nameCreate")
                .created(LocalDateTime.now())
                .build();

        itemDtoCreateTest = ItemSaveDtoRq.builder()
                .name("nameCreate")
                .description("create description")
                .available(true)
                .build();

        itemDtoCreated = ItemDtoRs.builder()
                .id(1)
                .name("nameCreate")
                .description("create description")
                .available(true)
                .comments(List.of(commentDtoCreated))
                .build();

        itemDtoUpdateTest = ItemUpdateDtoRq.builder()
                .description("update description")
                .comments(List.of(commentDtoCreateTest))
                .build();

        itemDtoUpdated = ItemDtoRs.builder()
                .id(2)
                .name("nameCreate")
                .description("update description")
                .available(true)
                .build();
    }

    @AfterEach
    void tearDown() {
        itemDtoCreateTest = null;
        itemDtoCreated = null;
        itemDtoUpdateTest = null;
        itemDtoUpdated = null;
    }

    @Test
    void createItem() throws Exception {
        when(itemService.save(anyInt(), any(ItemSaveDtoRq.class)))
                .thenReturn(itemDtoCreated);

        mvc.perform(post("/items")
                        .content(mapper.writeValueAsString(itemDtoCreateTest))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDtoCreated.getId()), Integer.class))
                .andExpect(jsonPath("$.name", is(itemDtoCreated.getName())))
                .andExpect(jsonPath("$.description", is(itemDtoCreated.getDescription())))
                .andExpect(jsonPath("$.available", is(itemDtoCreated.getAvailable())));
    }

    @Test
    void updateItem() throws Exception {
        when(itemService.update(anyInt(), anyInt(), any(ItemUpdateDtoRq.class)))
                .thenReturn(itemDtoUpdated);

        mvc.perform(patch("/items/1")
                        .content(mapper.writeValueAsString(itemDtoUpdateTest))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDtoUpdated.getId()), Integer.class))
                .andExpect(jsonPath("$.name", is(itemDtoUpdated.getName())))
                .andExpect(jsonPath("$.description", is(itemDtoUpdated.getDescription())))
                .andExpect(jsonPath("$.available", is(itemDtoUpdated.getAvailable())));
    }

    @Test
    void getItemById() throws Exception {
        when(itemService.getItemById(anyInt(), anyInt()))
                .thenReturn(itemDtoUpdated);

        mvc.perform(get("/items/1")
                        .header("X-Sharer-User-Id", 1)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDtoUpdated.getId()), Integer.class))
                .andExpect(jsonPath("$.name", is(itemDtoUpdated.getName())))
                .andExpect(jsonPath("$.description", is(itemDtoUpdated.getDescription())))
                .andExpect(jsonPath("$.available", is(itemDtoUpdated.getAvailable())));
    }

    @Test
    void getAllItems() throws Exception {
        List<ItemDtoRs> itemsDtoRs = new ArrayList<>();
        itemsDtoRs.add(itemDtoCreated);
        itemsDtoRs.add(itemDtoUpdated);
        when((itemService.getAllItemWithPagination(anyInt(), anyInt(), anyInt())))
                .thenReturn(itemsDtoRs);

        mvc.perform(get("/items/")
                        .header("X-Sharer-User-Id", 1)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(itemDtoCreated.getId()), Integer.class))
                .andExpect(jsonPath("$[0].name", is(itemDtoCreated.getName())))
                .andExpect(jsonPath("$[0].description", is(itemDtoCreated.getDescription())))
                .andExpect(jsonPath("$[0].available", is(itemDtoCreated.getAvailable())))
                .andExpect(jsonPath("$[1].id", is(itemDtoUpdated.getId()), Integer.class))
                .andExpect(jsonPath("$[1].name", is(itemDtoUpdated.getName())))
                .andExpect(jsonPath("$[1].description", is(itemDtoUpdated.getDescription())))
                .andExpect(jsonPath("$[1].available", is(itemDtoUpdated.getAvailable())));
    }

    @Test
    void searchByText() throws Exception {
        List<ItemDtoRs> itemsDtoRs = new ArrayList<>();
        itemsDtoRs.add(itemDtoCreated);
        itemsDtoRs.add(itemDtoUpdated);

        when(itemService.searchItemByTextWithPagination(anyString(), anyInt(), anyInt()))
                .thenReturn(itemsDtoRs);

        mvc.perform(get("/items/search?text=update")
                        .header("X-Sharer-User-Id", 1)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(itemDtoCreated.getId()), Integer.class))
                .andExpect(jsonPath("$[0].name", is(itemDtoCreated.getName())))
                .andExpect(jsonPath("$[0].description", is(itemDtoCreated.getDescription())))
                .andExpect(jsonPath("$[0].available", is(itemDtoCreated.getAvailable())))
                .andExpect(jsonPath("$[1].id", is(itemDtoUpdated.getId()), Integer.class))
                .andExpect(jsonPath("$[1].name", is(itemDtoUpdated.getName())))
                .andExpect(jsonPath("$[1].description", is(itemDtoUpdated.getDescription())))
                .andExpect(jsonPath("$[1].available", is(itemDtoUpdated.getAvailable())));
    }

    @Test
    void comment() throws Exception {
        when(itemService.createComment(any(CommentDtoRq.class), anyInt(), anyInt()))
                .thenReturn(commentDtoCreated);

        mvc.perform(post("/items/1/comment")
                        .content(mapper.writeValueAsString(commentDtoCreateTest))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(commentDtoCreated.getId()), Integer.class))
                .andExpect(jsonPath("$.text", is(commentDtoCreated.getText())))
                .andExpect(jsonPath("$.authorName", is(commentDtoCreated.getAuthorName())))
                .andExpect(jsonPath("$.created",
                        is(commentDtoCreated.getCreated().format(DateTimeFormatter.ISO_DATE_TIME))));
    }
}
