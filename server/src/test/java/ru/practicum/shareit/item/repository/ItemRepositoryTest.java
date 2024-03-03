package ru.practicum.shareit.item.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@DataJpaTest
public class ItemRepositoryTest {

    @Autowired
    ItemRepository itemRepository;

    @Autowired
    UserRepository userRepository;

    private Item itemOne;
    private Item itemTwo;
    private User userOne;

    @BeforeEach
    public void addUsers() {
        userOne = User.builder()
                .email("mail1@mail.ru")
                .name("name1")
                .build();
        userRepository.save(userOne);

        itemOne = Item.builder()
                .name("Утюг")
                .description("Угольный утюг")
                .owner(userOne)
                .available(true)
                .build();

        itemTwo = Item.builder()
                .name("Тряпка половая")
                .description("Тряпка для мытья полов")
                .owner(userOne)
                .available(true)
                .build();

        itemRepository.save(itemOne);
        itemRepository.save(itemTwo);
    }


    @Test
    void searchAvailableByText() {

        PageRequest pageRequest = PageRequest.of(0, 10);
        List<Item> items = itemRepository.getItemByText("утюг", pageRequest);

        assertThat(items.size(), equalTo(1));
        assertThat(items.get(0).getDescription(), equalTo(itemOne.getDescription()));
    }
}
