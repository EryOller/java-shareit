package ru.practicum.shareit.item.model;

import lombok.*;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;

import javax.persistence.*;

@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "items", schema = "public")
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(name = "name")
    private String name;
    @Column(name = "description")
    private String description;
    @Column(name = "is_available")
    private Boolean available;
    @ManyToOne(fetch = FetchType.LAZY)
   // @Column(name = "owner_id")
    //private int ownerId; // User
    private User owner;
//    @ManyToOne(fetch = FetchType.LAZY)
//   //  @Column(name = "request_id")
//    //private int requestId; //ItemRequest
//    private ItemRequest request;
}
