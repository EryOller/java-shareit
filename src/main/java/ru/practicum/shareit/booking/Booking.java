package ru.practicum.shareit.booking;

import lombok.*;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * TODO Sprint add-bookings.
 */

@Entity
//@Data
@Setter
@Getter
//@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "bookings", schema = "public")
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(name = "start_date")
    private LocalDateTime start;
    @Column(name = "end_date")
    private LocalDateTime end;
    @OneToOne(fetch = FetchType.LAZY)
   // @Column(name = "item_id")
    //private int itemId;
    private Item item;
    //@Column(name = "booker_id")
    //private int bookerId;
    @ManyToOne(fetch = FetchType.LAZY)
    private User broker;
    @Enumerated(EnumType.STRING)
    private BookingStatus status;
}
