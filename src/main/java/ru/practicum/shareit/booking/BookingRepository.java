package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Integer>, QuerydslPredicateExecutor<Booking> {

    List<Booking> findAllByBookerIdOrderByStartDesc(int id);

    List<Booking> findAllByBookerIdAndStatusOrderByStartDesc(int id, BookingStatus status);

    List<Booking> findAllByBookerIdAndEndBeforeOrderByStartDesc(int id, LocalDateTime now);

    List<Booking> findAllByBookerIdAndStartAfterOrderByStartDesc(int id, LocalDateTime now);

    @Query("select b from Booking b " +
            "where b.booker.id = ?1 and " +
            "b.start < ?2 and " +
            "b.end > ?2 " +
            "order by b.start desc")
    List<Booking> findCurrentBookerBookings(int bookerId, LocalDateTime now);

    List<Booking> findAllByItemOwnerIdOrderByStartDesc(int ownerId);

    List<Booking> findAllByItemOwnerIdAndStatusOrderByStartDesc(int ownerId, BookingStatus status);

    List<Booking> findAllByItemOwnerIdAndEndBeforeOrderByStartDesc(int ownerId, LocalDateTime now);

    List<Booking> findAllByItemOwnerIdAndStartAfterOrderByStartDesc(int ownerId, LocalDateTime now);

    @Query("select b from Booking b " +
            "where b.item.owner.id = ?1 and " +
            "b.start < ?2 and " +
            "b.end > ?2 " +
            "order by b.start desc")
    List<Booking> findCurrentOwnerBookings(int ownerId, LocalDateTime now);

    Long countAllByItemIdAndBookerIdAndEndBefore(int itemId, int userId, LocalDateTime now);

    @Query("select b from Booking b " +
            "where b.item.id = ?1 and " +
            "b.item.owner.id = ?2 and " +
            "b.start < ?3 order by b.start desc")
    List<Booking> findPastOwnerBookings(int itemId, int ownerId, LocalDateTime now);

    @Query("select b from Booking b " +
            "where b.item.id = ?1 and " +
            "b.item.owner.id = ?2 and " +
            "b.start > ?3 " +
            "order by b.start asc")
    List<Booking> findFutureOwnerBookings(int itemId, int ownerId, LocalDateTime now);
}
