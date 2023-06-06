package ru.practicum.shareit.booking.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.constants.Status;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookingDao  extends JpaRepository<Booking, Integer> {

    @Query("select b " +
            "from Booking as b "+
            "join fetch b.item as i join fetch b.booker as u " +
            "where b.id = ?1 and (u.id = ?2 or i.ownerId = ?2)")
    Optional<Booking> findBookingByIdAndBookerId(int bookingId, long bookerId);
    @Query("select b " +
            "from Booking as b "+
            "join b.booker as u " +
            "where u.id = ?1  order by b.start desc ")
    List<Booking> findAllByBooker(long bookerId);

    @Query("select b " +
            "from Booking as b "+
            "join b.booker as u " +
            "where u.id = ?1 and (b.start > CURRENT_TIMESTAMP or b.end > CURRENT_TIMESTAMP) order by b.start desc ")
    List<Booking> findFutureByBooker(long bookerId);

    @Query("select b " +
            "from Booking as b "+
            "join b.booker as u " +
            "where u.id = ?1 and b.end < CURRENT_TIMESTAMP order by b.start desc ")
    List<Booking> findPastByBooker(long bookerId);

    @Query("select b " +
            "from Booking as b "+
            "join b.booker as u " +
            "where u.id = ?1 and b.start > CURRENT_TIMESTAMP and b.end < CURRENT_TIMESTAMP order by b.start desc ")
    List<Booking> findCurrentByBooker(long bookerId);

    @Query("select b " +
            "from Booking as b "+
            "join b.booker as u " +
            "where u.id = ?1 and b.status = ?2 order by b.start desc ")
    List<Booking> findWaitingOrRejectedByBooker(long bookerId, Status state);

    @Query("select b " +
            "from Booking as b "+
            "join fetch b.item as i join fetch b.booker as u " +
            "where i.ownerId = ?1 order by b.start desc ")
    List<Booking> findAllByOwner(int ownerId);

    @Query("select b " +
            "from Booking as b "+
            "join fetch b.item as i join fetch b.booker as u " +
            "where i.ownerId = ?1 and (b.start > CURRENT_TIMESTAMP or b.end > CURRENT_TIMESTAMP) order by b.start desc ")
    List<Booking> findFutureByOwner(int ownerId);

    @Query("select b " +
            "from Booking as b "+
            "join fetch b.item as i join fetch b.booker as u " +
            "where i.ownerId = ?1 and b.end < CURRENT_TIMESTAMP order by b.start desc ")
    List<Booking> findPastByOwner(int ownerId);

    @Query("select b " +
            "from Booking as b "+
            "join fetch b.item as i join fetch b.booker as u " +
            "where i.ownerId = ?1 and b.start > CURRENT_TIMESTAMP and b.end < CURRENT_TIMESTAMP order by b.start desc ")
    List<Booking> findCurrentByOwner(int ownerId);

    @Query("select b " +
            "from Booking as b "+
            "join fetch b.item as i join fetch b.booker as u " +
            "where i.ownerId = ?1 and b.status = ?2 order by b.start desc ")
    List<Booking> findWaitingOrRejectedByOwner(int ownerId, Status state);

    @Query("select b " +
            "from Booking as b "+
            "join fetch b.item as i " +
            "where i.id = ?1 and b.start < CURRENT_TIMESTAMP order by b.start desc")
    List<Booking> findBookingWithLastNearestDateByItemId(int itemId);

    @Query("select b " +
            "from Booking as b "+
            "join fetch b.item as i " +
            "where i.id = ?1 and b.start > CURRENT_TIMESTAMP order by b.start ")
    List<Booking> findBookingWithNextNearestDateByItemId(int itemId);
}
