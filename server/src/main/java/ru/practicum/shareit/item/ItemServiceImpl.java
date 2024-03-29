package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.EditForbiddenException;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.item.comment.CommentMapper;
import ru.practicum.shareit.item.comment.CommentRepository;
import ru.practicum.shareit.item.comment.dto.CommentDtoRq;
import ru.practicum.shareit.item.comment.dto.CommentDtoRs;
import ru.practicum.shareit.item.comment.model.Comment;
import ru.practicum.shareit.item.dto.ItemDtoRs;
import ru.practicum.shareit.item.dto.ItemSaveDtoRq;
import ru.practicum.shareit.item.dto.ItemUpdateDtoRq;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.pagination_manager.PaginationManager;
import ru.practicum.shareit.request.ItemRequestService;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserService;

import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserService userService;
    private final ItemMapper itemMapper;
    private final BookingRepository bookingRepository;
    private final CommentMapper commentMapper;
    private final CommentRepository commentRepository;
    private final ItemRequestService itemRequestService;

    @Transactional
    @Override
    public ItemDtoRs save(int ownerId, ItemSaveDtoRq itemDto) {
         if (userService.isValidId(ownerId)) {
            Item item = itemMapper.toItem(itemDto);
            item.setRequest(itemRequestService.getItemRequestById(itemDto.getRequestId()));
            item.setOwner(userService.findUserById(ownerId));
            return itemMapper.toItemDtoRs(itemRepository.save(item));
        } else {
            throw new EntityNotFoundException("Владелец вещи с id " + ownerId + " не найден");
        }
    }

    @Transactional
    @Override
    public ItemDtoRs update(int userId, int itemId, ItemUpdateDtoRq itemDto) {
        if (userService.isValidId(userId)) {
            Item itemFromRepository = itemRepository.findById(itemId).get();
            if (itemFromRepository.getOwner().getId() == userId) {
                itemDto.setOwner(userService.findUserById(userId));
                Item item = itemMapper.toItem(itemDto);
                return updateItemByField(itemFromRepository, item);
            } else {
                throw new EditForbiddenException("Edit is forbidden for user with id " + userId);
            }
        } else {
            throw new EntityNotFoundException("Пользователь с id " + userId + " не найден");
        }
    }

    @Override
    public ItemDtoRs getItemById(int userId, int itemId) {
        if (userService.isValidId(userId)) {
            Item item = itemRepository.findById(itemId)
                    .orElseThrow(() -> new EntityNotFoundException("Пользователь с id " + userId + " не найден"));
            Booking lastBooking = bookingRepository.findPastOwnerBookings(item.getId(), userId,
                            LocalDateTime.now())
                    .stream()
                    .findFirst()
                    .orElse(null);
            item.setLastBooking(lastBooking);
            Booking nextBooking = bookingRepository.findFutureOwnerBookings(item.getId(), userId,
                            LocalDateTime.now())
                    .stream()
                    .findFirst()
                    .orElse(null);
            item.setNextBooking(nextBooking);
            item.setComments(commentRepository.findCommentsByItemId(itemId));
            ItemDtoRs itemDtoRs = itemMapper.toItemDtoRs(item);
            itemDtoRs.setComments(commentMapper.toListCommentDtoRs(item.getComments()));
            return itemDtoRs;
        } else {
            throw new EntityNotFoundException("Пользователь с id " + userId + " не найден");
        }
    }

    @Override
    public List<ItemDtoRs> getAllItemWithPagination(int userId, int from, int size) {
        if (userService.isValidId(userId)) {
            PageRequest pageReq = PaginationManager.form(from, size, Sort.Direction.DESC, "id");
            List<Item> items = itemRepository.getListItemsByOwnerIdOrderByIdAsc(userId, pageReq);
            List<Booking> bookings = bookingRepository
                    .findAllOwnerBookings(userId);
            List<Comment> comments = commentRepository.findAllCommentsByItemOwnerId(userId);
            return itemMapper.toListItemDtoRs(
                    items.stream()
                            .peek(item -> {
                                item.setNextBooking(bookings.stream()
                                        .filter(booking -> booking.getStart().isAfter(LocalDateTime.now())
                                                && booking.getItem().getId() == item.getId())
                                        .findFirst().orElse(null));
                                item.setLastBooking(bookings.stream()
                                        .filter(booking -> booking.getStart().isBefore(LocalDateTime.now())
                                                && booking.getItem().getId() == item.getId())
                                        .findFirst().orElse(null));
                                item.setComments(
                                        comments.stream()
                                                .filter(comment -> comment.getItem().getId() == item.getId())
                                                .collect(Collectors.toList()));

                            }).collect(Collectors.toList())
            );
        } else {
            throw new EntityNotFoundException("Пользователь с id " + userId + " не найден");
        }
    }

    @Override
    public List<ItemDtoRs> searchItemByTextWithPagination(String text, int from, int size) {

        if (text.isEmpty() || text.isBlank()) {
            return new ArrayList<>(0);
        } else {
            PageRequest pageReq = PaginationManager.form(from, size);
            return itemMapper.toListItemDtoRs(itemRepository.getItemByText(text, pageReq));
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
    public boolean isAvailableItem(int userId, int itemId) {
        return getItemById(userId, itemId).getAvailable();
    }

    @Override
    public CommentDtoRs createComment(CommentDtoRq commentDtoRq, int itemId, int userId) {
        if (userService.isValidId(userId)) {
            if (commentDtoRq.getText() == null || commentDtoRq.getText().equals("")) {
                throw new BadRequestException("Отзыв не может быть пустым");
            }
            Integer bookingsCount = bookingRepository.countAllByItemIdAndBookerIdAndEndBefore(itemId, userId,
                    LocalDateTime.now());
            if (bookingsCount == null || bookingsCount == 0) {
                throw new BadRequestException("Вещь еще ниразу не бронировалась");
            }
            Item item = itemRepository.findById(itemId)
                    .orElseThrow(() -> new NoSuchElementException("вещь c идентификатором " + itemId + " не существует"));
            User user = userService.findUserById(userId);
            Comment comment = commentMapper.toComment(commentDtoRq);
            comment.setItem(item);
            comment.setAuthor(user);
            comment.setCreated(LocalDateTime.now());
            return commentMapper.toCommentDtoRs(commentRepository.save(comment));
        } else {
            throw new EntityNotFoundException("Пользователь с идентификатором " + userId + " не найден");
        }
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