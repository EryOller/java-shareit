package ru.practicum.shareit.booking.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@DataJpaTest
public class BookingRepositoryTest {
    @Autowired
    ItemRepository itemRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    BookingRepository bookingRepository;

    private Item item;
    private User owner;
    private User booker;
    private Booking booking;

    @BeforeEach
    public void init() {

        owner = User.builder()
                .name("user1")
                .email("user1@email.com")
                .build();

        item = Item.builder()
                .name("name")
                .description("description")
                .available(true)
                .owner(owner)
                .build();

        booker = User.builder()
                .name("user3")
                .email("user3@email.com")
                .build();

        booking = Booking.builder()
                .item(item)
                .booker(booker)
                .status(BookingStatus.APPROVED)
                .build();
    }

    @Test
    void findCurrentBookerBookings() {

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime start = now.minusDays(1);
        LocalDateTime end = now.plusDays(2);
        booking.setStart(start);
        booking.setEnd(end);

        User ownerInMemory = userRepository.save(owner);
        User bookerInMemory =userRepository.save(booker);
        item.setOwner(ownerInMemory);
        itemRepository.save(item);
        booking.setBooker(bookerInMemory);
        bookingRepository.save(booking);
        PageRequest pageRequest = PageRequest.of(0, 10);

        List<Booking> bookings = bookingRepository.findCurrentBookerBookings(bookerInMemory.getId(), now, pageRequest);

        assertThat(bookings.size(), equalTo(1));
        assertThat(bookings.get(0).getItem().getDescription(), equalTo(item.getDescription()));
    }

    @Test
    void findCurrentOwnerBookings() {

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime start = now.minusDays(1);
        LocalDateTime end = now.plusDays(2);

        booking.setStart(start);
        booking.setEnd(end);

        User ownerInMemory = userRepository.save(owner);
        User bookerInMemory =userRepository.save(booker);
        item.setOwner(ownerInMemory);
        itemRepository.save(item);
        booking.setBooker(bookerInMemory);
        bookingRepository.save(booking);

        PageRequest pageRequest = PageRequest.of(0, 10);

        List<Booking> bookings = bookingRepository.findCurrentOwnerBookings(ownerInMemory.getId(), now, pageRequest);
        assertThat(bookings.size(), equalTo(1));
        assertThat(bookings.get(0).getItem().getDescription(), equalTo(item.getDescription()));
    }

    @Test
    void findPastOwnerBookingsWithOutStatusRejected() {

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime start = now.minusDays(1);
        LocalDateTime end = now.plusDays(2);

        booking.setStart(start);
        booking.setEnd(end);

        User ownerInMemory = userRepository.save(owner);
        User bookerInMemory =userRepository.save(booker);
        item.setOwner(ownerInMemory);
        Item itemInMemory = itemRepository.save(item);
        booking.setBooker(bookerInMemory);
        bookingRepository.save(booking);

        List<Booking> bookings = bookingRepository.findPastOwnerBookings(itemInMemory.getId(), ownerInMemory.getId(), now);
        assertThat(bookings.size(), equalTo(1));
        assertThat(bookings.get(0).getItem().getDescription(), equalTo(item.getDescription()));
    }

    @Test
    void findPastOwnerBookingsWithStatusRejected() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime start = now.minusDays(1);
        LocalDateTime end = now.plusDays(2);

        booking.setStatus(BookingStatus.REJECTED);
        booking.setStart(start);
        booking.setEnd(end);

        User ownerInMemory = userRepository.save(owner);
        User bookerInMemory =userRepository.save(booker);
        item.setOwner(ownerInMemory);
        Item itemInMemory = itemRepository.save(item);
        booking.setBooker(bookerInMemory);
        bookingRepository.save(booking);

        List<Booking> bookings = bookingRepository.findPastOwnerBookings(itemInMemory.getId(), ownerInMemory.getId(), now);
        assertThat(bookings.size(), equalTo(0));
    }

    @Test
    void findFutureOwnerBookingsWithOutStatusRejected() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime start = now.plusDays(1);
        LocalDateTime end = now.plusDays(2);

        booking.setStart(start);
        booking.setEnd(end);

        User ownerInMemory = userRepository.save(owner);
        User bookerInMemory =userRepository.save(booker);
        item.setOwner(ownerInMemory);
        Item itemInMemory = itemRepository.save(item);
        booking.setBooker(bookerInMemory);
        bookingRepository.save(booking);

        List<Booking> bookings = bookingRepository.findFutureOwnerBookings(itemInMemory.getId(), ownerInMemory.getId(), now);
        assertThat(bookings.size(), equalTo(1));
        assertThat(bookings.get(0).getItem().getDescription(), equalTo(item.getDescription()));
    }

    @Test
    void findFutureOwnerBookingsWithStatusRejected() {

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime start = now.plusDays(1);
        LocalDateTime end = now.plusDays(2);

        booking.setStatus(BookingStatus.REJECTED);
        booking.setStart(start);
        booking.setEnd(end);

        User ownerInMemory = userRepository.save(owner);
        User bookerInMemory =userRepository.save(booker);
        item.setOwner(ownerInMemory);
        Item itemInMemory = itemRepository.save(item);
        booking.setBooker(bookerInMemory);
        bookingRepository.save(booking);

        List<Booking> bookings = bookingRepository.findFutureOwnerBookings(itemInMemory.getId(), ownerInMemory.getId(), now);
        assertThat(bookings.size(), equalTo(0));
    }

    @Test
    void findAllOwnerBookingsWithOutStatusRejected() {

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime start = now.plusDays(1);
        LocalDateTime end = now.plusDays(2);

        booking.setStart(start);
        booking.setEnd(end);

        User ownerInMemory = userRepository.save(owner);
        User bookerInMemory =userRepository.save(booker);
        item.setOwner(ownerInMemory);
        itemRepository.save(item);
        booking.setBooker(bookerInMemory);
        bookingRepository.save(booking);

        List<Booking> bookings = bookingRepository.findAllOwnerBookings(ownerInMemory.getId());
        assertThat(bookings.size(), equalTo(1));
        assertThat(bookings.get(0).getItem().getDescription(), equalTo(item.getDescription()));
    }

    @Test
    void findAllOwnerBookingsWithStatusRejected() {

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime start = now.plusDays(1);
        LocalDateTime end = now.plusDays(2);

        booking.setStatus(BookingStatus.REJECTED);
        booking.setStart(start);
        booking.setEnd(end);

        User ownerInMemory = userRepository.save(owner);
        User bookerInMemory =userRepository.save(booker);
        item.setOwner(ownerInMemory);
        itemRepository.save(item);
        booking.setBooker(bookerInMemory);
        bookingRepository.save(booking);

        List<Booking> bookings = bookingRepository.findAllOwnerBookings(ownerInMemory.getId());
        assertThat(bookings.size(), equalTo(0));
    }
}
