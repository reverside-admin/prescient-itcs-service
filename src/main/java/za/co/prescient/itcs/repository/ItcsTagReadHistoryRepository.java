package za.co.prescient.itcs.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import za.co.prescient.itcs.model.ItcsTagReadHistory;

import java.util.Date;
import java.util.List;

public interface ItcsTagReadHistoryRepository extends JpaRepository<ItcsTagReadHistory, Long> {

    @Query("select itr from ItcsTagReadHistory itr " +
            "where hex(itr.guestCard)=?1 and itr.tagReadDatetime in (select max(itr2.tagReadDatetime) " +
            "from ItcsTagReadHistory itr2 where itr2.tagReadDatetime " +
            "between ?2 and current_timestamp and " +
            "hex(itr2.guestCard)=?1 group by itr2.zoneId) group by zoneId ")
    List<ItcsTagReadHistory> findGuestLocation(String guestCardId,Date arrivalTime);

}
