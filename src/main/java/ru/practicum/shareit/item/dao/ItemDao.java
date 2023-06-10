package ru.practicum.shareit.item.dao;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

@Repository
public interface ItemDao extends JpaRepository<Item, Integer> {
    List<Item> findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(String text1, String text2, Pageable pagebale);

    List<Item> findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(String text1, String text2);

    @Query("select i from Item as i where i.requestId in ?1")
    List<Item> findItemsByRequestIds(List<Integer> listRequestId);
}
