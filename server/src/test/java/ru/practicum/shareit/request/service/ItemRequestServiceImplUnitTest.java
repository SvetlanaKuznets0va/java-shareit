package ru.practicum.shareit.request.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dao.ItemDao;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dao.ItemRequestDao;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestRespDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
class ItemRequestServiceImplUnitTest {
    @Mock
    UserService mockUserService;
    @Mock
    ItemRequestDao mockItemRequestDao;
    @Mock
    ItemDao mockItemDao;

    LocalDateTime created = LocalDateTime.now();

    @Test
    void shouldAddItemRequest() {
        ItemRequestDto itemRequestDto = new ItemRequestDto(0, "description", null);
        ItemRequest itemRequest = new ItemRequest(1, "description", 1, created);
        ItemRequestServiceImpl service = new ItemRequestServiceImpl(mockUserService, mockItemRequestDao, mockItemDao);
        Mockito.when(mockUserService.isExist(1)).thenReturn(true);
        Mockito.when(mockItemRequestDao.save(any(ItemRequest.class))).thenReturn(itemRequest);

        ItemRequestDto result = service.addItemRequest(itemRequestDto, 1);

        Mockito.verify(mockUserService, Mockito.times(1)).isExist(1);
        Mockito.verify(mockItemRequestDao, Mockito.times(1)).save(any(ItemRequest.class));
        assertEquals(1, result.getId());
        assertEquals("description", result.getDescription());
        assertEquals(created, result.getCreated());
    }

    @Test
    void shouldThrowNotFoundExceptionWhenUserIdIsNotExist() {
        ItemRequestDto itemRequestDto = new ItemRequestDto(0, "description", null);
        ItemRequestServiceImpl service = new ItemRequestServiceImpl(mockUserService, mockItemRequestDao, mockItemDao);
        Mockito.when(mockUserService.isExist(1)).thenReturn(false);

        assertThrows(NotFoundException.class, () -> service.addItemRequest(itemRequestDto, 1), "Такого владельца нет");
    }

    @Test
    void shouldReturnOwnItemRequests() {
        List<ItemRequest> requests = new ArrayList<>();
        requests.add(new ItemRequest(1, "description1", 1, created.plusHours(5)));
        requests.add(new ItemRequest(2, "description2", 1, created.plusHours(4)));
        requests.add(new ItemRequest(3, "description3", 1, created.plusHours(3)));
        requests.add(new ItemRequest(4, "description4", 1, created.plusHours(2)));
        requests.add(new ItemRequest(5, "description5", 1, created.plusHours(1)));

        ItemRequestServiceImpl service = new ItemRequestServiceImpl(mockUserService, mockItemRequestDao, mockItemDao);
        Mockito.when(mockUserService.isExist(1)).thenReturn(true);
        Mockito.when(mockItemRequestDao.findItemRequestsByRequestorIdOrderByCreatedDesc(1)).thenReturn(requests);

        List<ItemRequestRespDto> result = service.getOwnItemRequests(1);

        Mockito.verify(mockUserService, Mockito.times(1)).isExist(1);
        Mockito.verify(mockItemRequestDao, Mockito.times(1))
                .findItemRequestsByRequestorIdOrderByCreatedDesc(1);
        assertEquals(5, result.size());
    }

    @Test
    void shouldReturnEmptyOwnItemRequests() {
        ItemRequestServiceImpl service = new ItemRequestServiceImpl(mockUserService, mockItemRequestDao, mockItemDao);
        Mockito.when(mockUserService.isExist(1)).thenReturn(true);
        Mockito.when(mockItemRequestDao.findItemRequestsByRequestorIdOrderByCreatedDesc(1)).thenReturn(Collections.emptyList());

        List<ItemRequestRespDto> result = service.getOwnItemRequests(1);

        Mockito.verify(mockUserService, Mockito.times(1)).isExist(1);
        Mockito.verify(mockItemRequestDao, Mockito.times(1))
                .findItemRequestsByRequestorIdOrderByCreatedDesc(1);
        assertEquals(0, result.size());
    }

    @Test
    void shouldNotFoundExceptionInGetOwnItemRequestsWhenUserIsNotExist() {
        ItemRequestServiceImpl service = new ItemRequestServiceImpl(mockUserService, mockItemRequestDao, mockItemDao);
        Mockito.when(mockUserService.isExist(1)).thenReturn(false);

        assertThrows(NotFoundException.class, () -> service.getOwnItemRequests(1), "Такого владельца нет");
    }


    @Test
    void shouldReturnItemRequests() {
        List<ItemRequest> requests = new ArrayList<>();
        requests.add(new ItemRequest(1, "description1", 1, created.plusHours(5)));
        requests.add(new ItemRequest(2, "description2", 2, created.plusHours(4)));
        requests.add(new ItemRequest(3, "description3", 3, created.plusHours(3)));
        requests.add(new ItemRequest(4, "description4", 1, created.plusHours(2)));
        requests.add(new ItemRequest(5, "description5", 5, created.plusHours(1)));

        ItemRequestServiceImpl service = new ItemRequestServiceImpl(mockUserService, mockItemRequestDao, mockItemDao);
        Mockito.when(mockItemRequestDao.findAllByOrderByCreatedDesc()).thenReturn(requests);

        List<ItemRequestRespDto> result = service.getItemRequests();

        Mockito.verify(mockItemRequestDao, Mockito.times(1)).findAllByOrderByCreatedDesc();
        assertEquals(5, result.size());
    }

    @Test
    void shouldReturnEmptyItemRequests() {
        ItemRequestServiceImpl service = new ItemRequestServiceImpl(mockUserService, mockItemRequestDao, mockItemDao);
        Mockito.when(mockItemRequestDao.findAllByOrderByCreatedDesc()).thenReturn(Collections.emptyList());

        List<ItemRequestRespDto> result = service.getItemRequests();

        Mockito.verify(mockItemRequestDao, Mockito.times(1)).findAllByOrderByCreatedDesc();
        assertEquals(0, result.size());
    }


    @Test
    void shouldReturnAllItemRequestsForUserWithPagination() {
        int from = 0;
        int size = 2;
        List<Item> items = new ArrayList<>();
        items.add(new Item(1, 2, "item1", "item1", true, 1));
        items.add(new Item(2, 2, "item2", "item2", true, null));
        items.add(new Item(3, 2, "item3", "item3", true, 2));
        items.add(new Item(4, 2, "item4", "item4", true, 3));
        items.add(new Item(5, 2, "item5", "item5", true, 1));

        List<ItemRequest> requests = new ArrayList<>();
        requests.add(new ItemRequest(1, "description1", 1, created.plusHours(5)));
        requests.add(new ItemRequest(2, "description2", 2, created.plusHours(4)));
        requests.add(new ItemRequest(3, "description3", 2, created.plusHours(3)));
        requests.add(new ItemRequest(4, "description4", 4, created.plusHours(2)));
        requests.add(new ItemRequest(5, "description5", 1, created.plusHours(1)));

        Pageable pagebale = PageRequest.of(from > 0 ? from / size : 0, size, Sort.by("created").descending());
        Page<ItemRequest> pages = new PageImpl<>(requests, pagebale, requests.size());

        ItemRequestServiceImpl service = new ItemRequestServiceImpl(mockUserService, mockItemRequestDao, mockItemDao);
        Mockito.when(mockItemDao.findAll()).thenReturn(items);
        Mockito.when(mockItemRequestDao.findAll(pagebale)).thenReturn(pages);

        List<ItemRequestRespDto> result = service.getAllItemRequests(from, size, 1);

        assertEquals(3, result.size());
        assertEquals(2, result.get(0).getId());
        assertEquals(3, result.get(1).getId());
        assertEquals(4, result.get(2).getId());
    }


    @Test
    void shouldReturnItemRequestById() {
        List<Item> items = new ArrayList<>();
        items.add(new Item(1, 2, "item1", "item1", true, 2));
        items.add(new Item(2, 2, "item2", "item2", true, 2));
        items.add(new Item(3, 2, "item3", "item3", true, 2));
        items.add(new Item(4, 2, "item4", "item4", true, 2));
        items.add(new Item(5, 2, "item5", "item5", true, 2));

        List<ItemRequest> requests = new ArrayList<>();
        requests.add(new ItemRequest(1, "description1", 1, created.plusHours(5)));
        requests.add(new ItemRequest(2, "description2", 2, created.plusHours(4)));
        requests.add(new ItemRequest(3, "description3", 2, created.plusHours(3)));
        requests.add(new ItemRequest(4, "description4", 4, created.plusHours(2)));
        requests.add(new ItemRequest(5, "description5", 1, created.plusHours(1)));

        ItemRequestServiceImpl service = new ItemRequestServiceImpl(mockUserService, mockItemRequestDao, mockItemDao);
        Mockito.when(mockUserService.isExist(Mockito.anyInt())).thenReturn(true);
        Mockito.when(mockItemDao.findItemsByRequestIds(Collections.singletonList(2))).thenReturn(items);
        Mockito.when(mockItemRequestDao.findById(2)).thenReturn(Optional.ofNullable(requests.get(1)));

        ItemRequestRespDto result = service.getItemRequestById(1, 2);

        assertEquals(2, result.getId());
        assertEquals(5, result.getItems().size());
    }

    @Test
    void shouldNotFoundExceptionInGetItemRequestByIdWhenUserIsNotExist() {
        ItemRequestServiceImpl service = new ItemRequestServiceImpl(mockUserService, mockItemRequestDao, mockItemDao);
        Mockito.when(mockUserService.isExist(Mockito.anyInt())).thenReturn(false);

        assertThrows(NotFoundException.class, () -> service.getItemRequestById(1, 2));
    }

    @Test
    void shouldNotFoundExceptionInGetItemRequestByIdWhenRequestIsNotExist() {
        ItemRequestServiceImpl service = new ItemRequestServiceImpl(mockUserService, mockItemRequestDao, mockItemDao);
        Mockito.when(mockUserService.isExist(Mockito.anyInt())).thenReturn(true);
        Mockito.when(mockItemRequestDao.findById(2)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> service.getItemRequestById(1, 2));
    }
}