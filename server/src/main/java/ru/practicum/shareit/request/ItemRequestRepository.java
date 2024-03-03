package ru.practicum.shareit.request;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ItemRequestRepository extends JpaRepository<ItemRequest, Integer> {

    List<ItemRequest> findAllByRequesterIdOrderByCreatedDesc(Integer userId);

    Optional<ItemRequest> findItemRequestById(Integer id);

    List<ItemRequest> findAllByRequesterIdIsNot(Integer userId, PageRequest pageRequest);
}