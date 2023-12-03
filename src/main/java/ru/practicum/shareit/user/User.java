package ru.practicum.shareit.user;

import lombok.*;

import javax.persistence.*;

@Entity
//@Data
@Setter
@Getter
//@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users", schema = "public")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(name = "name")
    private String name;
    @Column(name = "email")
    private String email;
}
