package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.EditForbiddenException;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.comment.CommentMapper;
import ru.practicum.shareit.item.comment.CommentRepository;
import ru.practicum.shareit.item.comment.dto.CommentDtoRq;
import ru.practicum.shareit.item.comment.dto.CommentDtoRs;
import ru.practicum.shareit.item.comment.model.Comment;
import ru.practicum.shareit.item.dto.ItemDtoRs;
import ru.practicum.shareit.item.dto.ItemSaveDtoRq;
import ru.practicum.shareit.item.dto.ItemUpdateDtoRq;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserService;

import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    @Autowired
    private final ItemRepository itemRepository;
    @Autowired
    private final UserService userService;
    @Autowired
    private final ItemMapper itemMapper;
    @Autowired
    private final BookingRepository bookingRepository;
    @Autowired
    private final CommentMapper commentMapper;
    @Autowired
    private final CommentRepository commentRepository;

    @Transactional
    @Override
    public ItemDtoRs save(int ownerId, ItemSaveDtoRq itemDto) {
        if (userService.isValidId(ownerId)) {
            Item item = itemMapper.toItem(itemDto);
            item.setOwner(userService.findUserById(ownerId));
            return itemMapper.toItemDtoRs(itemRepository.save(item));
        } else {
            throw new NotFoundException("Владелец вещи с id " + ownerId + " не найден");
        }
    }

    @Transactional
    @Override
    public ItemDtoRs update(int userId, int itemId, ItemUpdateDtoRq itemDto) {
        Item itemFromRepository = itemRepository.findById(itemId).get();
        if (itemFromRepository.getOwner().getId() == userId) {
            itemDto.setOwner(userService.findUserById(userId));
            Item item = itemMapper.toItem(itemDto);
            return updateItemByField(itemFromRepository, item);
        } else {
            throw new EditForbiddenException("Edit is forbidden for user with id " + userId);
        }
    }

    @Override
    public ItemDtoRs getItemById(int userId, int itemId) {
        Item item = itemRepository.findById(itemId)
                    .orElseThrow(() -> new NotFoundException("Вещь с id " + itemId + " не найдена"));
        Booking lastBooking = bookingRepository.findPastOwnerBookings(item.getId(), item.getOwner().getId(),
                        LocalDateTime.now()).stream()
                .filter(b -> b.getItem().getOwner().getId() == userId)
                .filter(b -> !BookingStatus.REJECTED.equals(b.getStatus()))
                .findFirst()
                .orElse(null);
        item.setLastBooking(lastBooking);
        Booking nextBooking = bookingRepository.findFutureOwnerBookings(item.getId(), item.getOwner().getId(),
                        LocalDateTime.now())
                .stream()
                .filter(b -> b.getItem().getOwner().getId() == userId)
                .filter(b -> !BookingStatus.REJECTED.equals(b.getStatus()))
                .findFirst()
                .orElse(null);
        item.setNextBooking(nextBooking);
        item.setComments(commentRepository.findCommentsByItemId(itemId));
        ItemDtoRs itemDtoRs = itemMapper.toItemDtoRs(item);
        itemDtoRs.setComments(commentMapper.toListCommentDtoRs(item.getComments()));
        return itemDtoRs;
    }

    @Override
    public List<ItemDtoRs> getAllItemsByUserId(int userId) {
        List<Item> items = itemRepository.getListItemsByOwnerIdOrderByIdAsc(userId).stream()
                        .peek(item -> {
                            Booking lastBooking = bookingRepository.findPastOwnerBookings(item.getId(),
                                            item.getOwner().getId(), LocalDateTime.now()).stream()
                        .filter(b -> b.getItem().getOwner().getId() == userId)
                        .filter(b -> !BookingStatus.REJECTED.equals(b.getStatus()))
                        .findFirst()
                        .orElse(null);
                    item.setLastBooking(lastBooking);

                    Booking nextBooking = bookingRepository.findFutureOwnerBookings(item.getId(),
                                    item.getOwner().getId(), LocalDateTime.now())
                            .stream()
                            .filter(b -> b.getItem().getOwner().getId() == userId)
                            .filter(b -> !BookingStatus.REJECTED.equals(b.getStatus()))
                            .findFirst()
                            .orElse(null);
                    item.setNextBooking(nextBooking);
                    item.setComments(commentRepository.findCommentsByItemId(item.getId()));
                }).collect(Collectors.toList());
                return itemMapper.toListItemDtoRs(items);
    }

    @Override
    public List<ItemDtoRs> searchItemByText(String text) {
        if (text.isEmpty() || text.isBlank()) {
            return new ArrayList<>(0);
        } else {
            return itemMapper.toListItemDtoRs(itemRepository.getItemByText(text));
        }
    }

    @Override
    public Item findItemById(int itemId) {
        return itemRepository.findById(itemId).get();
    }

    @Override
    public boolean isValid(int itemId) {
        return itemRepository.existsById(itemId);
    }

    @Override
    public CommentDtoRs createComment(CommentDtoRq commentDtoRq, int itemId, int userId) {
        if (commentDtoRq.getText() == null || commentDtoRq.getText().equals("")) {
            throw new BadRequestException("Отзыв не может быть пустым");
        }
        Long bookingsCount = bookingRepository.countAllByItemIdAndBookerIdAndEndBefore(itemId, userId,
                LocalDateTime.now());
        if (bookingsCount == null || bookingsCount == 0) {
            throw new BadRequestException("сначала надо взять эту вещь");
        }
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NoSuchElementException("вещь c идентификатором " + itemId + " не существует"));
        User user = userService.findUserById(userId);
        Comment comment = commentMapper.toComment(commentDtoRq);
        comment.setItem(item);
        comment.setAuthor(user);
        comment.setCreated(LocalDateTime.now());
        return commentMapper.toCommentDtoRs(commentRepository.save(comment));
    }

    @Transactional
    private ItemDtoRs updateItemByField(Item item, Item itemUpdate) {
        if (itemUpdate.getName() != null && !"".equals(itemUpdate.getName())) {
            item.setName(itemUpdate.getName());
        }
        if (itemUpdate.getDescription() != null && !"".equals(itemUpdate.getDescription())) {
            item.setDescription(itemUpdate.getDescription());
        }
        if (itemUpdate.getAvailable() != null) {
            item.setAvailable(itemUpdate.getAvailable());
        }
        return itemMapper.toItemDtoRs(item);
    }
}