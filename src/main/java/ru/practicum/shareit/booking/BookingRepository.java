package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Integer>, QuerydslPredicateExecutor<Booking> {

    List<Booking> findAllByBookerIdOrderByStartDesc(int id);

    List<Booking> findAllByBookerIdAndStatusOrderByStartDesc(int id, BookingStatus status);

    List<Booking> findAllByBookerIdAndEndBeforeOrderByStartDesc(int id, LocalDateTime now);

    List<Booking> findAllByBookerIdAndStartAfterOrderByStartDesc(int id, LocalDateTime now);

    @Query("select b from Booking b " +
            "where b.booker.id = :bookerId and " +
            "b.start < :time and " +
            "b.end > :time " +
            "order by b.start desc")
    List<Booking> findCurrentBookerBookings(@Param("bookerId") int bookerId, @Param("time") LocalDateTime now);

    List<Booking> findAllByItemOwnerIdOrderByStartDesc(int ownerId);

    List<Booking> findAllByItemOwnerIdAndStatusOrderByStartDesc(int ownerId, BookingStatus status);

    List<Booking> findAllByItemOwnerIdAndEndBeforeOrderByStartDesc(int ownerId, LocalDateTime now);

    List<Booking> findAllByItemOwnerIdAndStartAfterOrderByStartDesc(int ownerId, LocalDateTime now);

    @Query("select b from Booking b " +
            "where b.item.owner.id = :ownerId and " +
            "b.start < :time and " +
            "b.end > :time " +
            "order by b.start desc")
    List<Booking> findCurrentOwnerBookings(@Param("ownerId") int ownerId, @Param("time") LocalDateTime now);

    Integer countAllByItemIdAndBookerIdAndEndBefore(int itemId, int userId, LocalDateTime now);

    @Query("select b from Booking b " +
            "where b.item.id = :itemId and " +
            "b.item.owner.id = :ownerId and " +
            "b.status <> 'REJECTED' and " +
            "b.start < :time order by b.start desc")
    List<Booking> findPastOwnerBookings(@Param("itemId") int itemId, @Param("ownerId") int ownerId,
                                        @Param("time") LocalDateTime now);

    @Query("select b from Booking b " +
            "where b.item.id = :itemId and " +
            "b.item.owner.id = :ownerId and " +
            "b.status <> 'REJECTED' and " +
            "b.start > :time " +
            "order by b.start")
    List<Booking> findFutureOwnerBookings(@Param("itemId") int itemId, @Param("ownerId") int ownerId,
                                          @Param("time") LocalDateTime now);

    @Query("select b from Booking b " +
            "where b.item.owner.id = :ownerId and " +
            "b.status <> 'REJECTED' " +
            "order by b.start")
    List<Booking> findAllOwnerBookings(@Param("ownerId") int ownerId);
}
