package ru.practicum.shareit.booking.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.BookingController;
import ru.practicum.shareit.booking.BookingService;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingDtoRs;
import ru.practicum.shareit.booking.dto.BookingSaveDtoRq;
import ru.practicum.shareit.item.dto.ItemDtoRs;
import ru.practicum.shareit.user.dto.UserDtoRs;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookingController.class)
public class BookingControllerTest {

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private BookingService bookingService;

    @Autowired
    private MockMvc mvc;

    private BookingSaveDtoRq bookingSaveDtoRq;

    private BookingDtoRs bookingDtoRs;

    private ItemDtoRs item;

    private UserDtoRs booker;

    private LocalDateTime start;

    private LocalDateTime end;

    @BeforeEach
    void setUp() {
        start = LocalDateTime.now().plusDays(1);

        end = LocalDateTime.now().plusDays(2);

        bookingSaveDtoRq = BookingSaveDtoRq.builder()
                .start(start)
                .end(end)
                .itemId(1)
                .build();

        item = ItemDtoRs.builder()
                .id(1)
                .name("item created")
                .build();

        booker = UserDtoRs.builder()
                .id(1)
                .build();

        bookingDtoRs = BookingDtoRs.builder()
                .id(1)
                .start(start)
                .end(end)
                .item(item)
                .booker(booker)
                .status(BookingStatus.WAITING)
                .build();
    }

    @AfterEach
    void tearDown() {
        start = null;
        end = null;
        bookingSaveDtoRq = null;
        item = null;
        booker = null;
    }

    @Test
    void createBooking() throws Exception {
        when(bookingService.save(anyInt(), any(BookingSaveDtoRq.class)))
                .thenReturn(bookingDtoRs);

        mvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(bookingSaveDtoRq))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingDtoRs.getId()), Integer.class))
                .andExpect(jsonPath("$.start",
                        is(bookingDtoRs.getStart().format(DateTimeFormatter.ISO_DATE_TIME))))
                .andExpect(jsonPath("$.end",
                        is(bookingDtoRs.getEnd().format(DateTimeFormatter.ISO_DATE_TIME))))
                .andExpect(jsonPath("$.item.id", is(bookingDtoRs.getItem().getId()), Integer.class))
                .andExpect(jsonPath("$.item.name", is(bookingDtoRs.getItem().getName())))
                .andExpect(jsonPath("$.booker.id", is(bookingDtoRs.getBooker().getId()), Integer.class))
                .andExpect(jsonPath("$.status", is(bookingDtoRs.getStatus().toString())));
    }

    @Test
    void approveBooking() throws Exception {
        bookingDtoRs.setStatus(BookingStatus.APPROVED);

        when(bookingService.approve(anyInt(), anyInt(), anyBoolean()))
                .thenReturn(bookingDtoRs);

        mvc.perform(patch("/bookings/1?approved=true")
                        .header("X-Sharer-User-Id", 2)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingDtoRs.getId()), Integer.class))
                .andExpect(jsonPath("$.start",
                        is(bookingDtoRs.getStart().format(DateTimeFormatter.ISO_DATE_TIME))))
                .andExpect(jsonPath("$.end", is(bookingDtoRs.getEnd().format(DateTimeFormatter.ISO_DATE_TIME))))
                .andExpect(jsonPath("$.item.id", is(bookingDtoRs.getItem().getId()), Integer.class))
                .andExpect(jsonPath("$.item.name", is(bookingDtoRs.getItem().getName())))
                .andExpect(jsonPath("$.booker.id", is(bookingDtoRs.getBooker().getId()), Integer.class))
                .andExpect(jsonPath("$.status", is(bookingDtoRs.getStatus().toString())));
    }

    @Test
    void getBooking() throws Exception {
        when(bookingService.getBookingById(anyInt(), anyInt()))
                .thenReturn(bookingDtoRs);

        mvc.perform(get("/bookings/1")
                        .header("X-Sharer-User-Id", 1)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingDtoRs.getId()), Integer.class))
                .andExpect(jsonPath("$.start",
                        is(bookingDtoRs.getStart().format(DateTimeFormatter.ISO_DATE_TIME))))
                .andExpect(jsonPath("$.end", is(bookingDtoRs.getEnd().format(DateTimeFormatter.ISO_DATE_TIME))))
                .andExpect(jsonPath("$.item.id", is(bookingDtoRs.getItem().getId()), Integer.class))
                .andExpect(jsonPath("$.item.name", is(bookingDtoRs.getItem().getName())))
                .andExpect(jsonPath("$.booker.id", is(bookingDtoRs.getBooker().getId()), Integer.class))
                .andExpect(jsonPath("$.status", is(bookingDtoRs.getStatus().toString())));
    }

    @Test
    void getAllBookings() throws Exception {
        when(bookingService.getBookingListWithPagination(1, "ALL", 0, 10))
                .thenReturn(List.of(bookingDtoRs));

        mvc.perform(get("/bookings/")
                        .header("X-Sharer-User-Id", 1)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(bookingDtoRs.getId()), Integer.class))
                .andExpect(jsonPath("$[0].start",
                        is(bookingDtoRs.getStart().format(DateTimeFormatter.ISO_DATE_TIME))))
                .andExpect(jsonPath("$[0].end", is(bookingDtoRs.getEnd().format(DateTimeFormatter.ISO_DATE_TIME))))
                .andExpect(jsonPath("$[0].item.id", is(bookingDtoRs.getItem().getId()), Integer.class))
                .andExpect(jsonPath("$[0].item.name", is(bookingDtoRs.getItem().getName())))
                .andExpect(jsonPath("$[0].booker.id", is(bookingDtoRs.getBooker().getId()), Integer.class))
                .andExpect(jsonPath("$[0].status", is(bookingDtoRs.getStatus().toString())));
    }

    @Test
    void getByOwner() throws Exception {
        when(bookingService.getByOwner(1, "ALL", 0, 10))
                .thenReturn(List.of(bookingDtoRs));

        mvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", 1)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(bookingDtoRs.getId()), Integer.class))
                .andExpect(jsonPath("$[0].start",
                        is(bookingDtoRs.getStart().format(DateTimeFormatter.ISO_DATE_TIME))))
                .andExpect(jsonPath("$[0].end",
                        is(bookingDtoRs.getEnd().format(DateTimeFormatter.ISO_DATE_TIME))))
                .andExpect(jsonPath("$[0].item.id", is(bookingDtoRs.getItem().getId()), Integer.class))
                .andExpect(jsonPath("$[0].item.name", is(bookingDtoRs.getItem().getName())))
                .andExpect(jsonPath("$[0].booker.id", is(bookingDtoRs.getBooker().getId()), Integer.class))
                .andExpect(jsonPath("$[0].status", is(bookingDtoRs.getStatus().toString())));
    }
}
