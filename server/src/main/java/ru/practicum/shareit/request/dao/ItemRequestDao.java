package ru.practicum.shareit.request.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

@Repository
public interface ItemRequestDao  extends JpaRepository<ItemRequest, Integer> {
    List<ItemRequest> findItemRequestsByRequestorIdOrderByCreatedDesc(int requestorId);

    List<ItemRequest> findAllByOrderByCreatedDesc();
}
