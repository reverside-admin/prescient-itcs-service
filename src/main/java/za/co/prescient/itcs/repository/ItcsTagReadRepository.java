package za.co.prescient.itcs.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import za.co.prescient.itcs.model.ItcsTagRead;

import java.util.Date;
import java.util.List;

@Repository
public interface ItcsTagReadRepository extends JpaRepository<ItcsTagRead, Long> {

   /* //return all cards in the supplied zoneid
    List<ItcsTagRead> findByZoneId(String zoneId);*/

    //@Query("select itr from ItcsTagRead itr where hex(itr.guestCard)=?1 and itr.tagReadDatetime =(select max(itr.tagReadDatetime) from ItcsTagRead itr where itr.tagReadDatetime between '2010-04-01 00:00:00' and current_timestamp and hex(itr.guestCard)=?1) order by desc")
   /* @Query("select itr from ItcsTagRead itr where hex(itr.guestCard)=?1 order by itr.tagReadDatetime desc")
    List<ItcsTagRead> findByGuestCardOrderByTagReadDatetimeDesc(String guestCardId);*/

    @Query("select itr from ItcsTagRead itr where itr.tagReadDatetime between ?1 and ?2 and itr.zoneId like ?3 order by itr.tagReadDatetime asc")
    List<ItcsTagRead> findCurrentCardsWithLocationInZone(Date startTime, Date endTime, String zoneId);

    //added for testing replacement of the second one above.applying the timing constraint.
    @Query("select itr from ItcsTagRead itr where hex(itr.guestCard)=?1 and itr.tagReadDatetime between (current_timestamp -  5) and current_timestamp order by itr.tagReadDatetime desc")
    List<ItcsTagRead> findByGuestCardOrderByTagReadDatetimeDesc(String guestCardId);

    //added for replacement of first one above.appling timing constraint.
    @Query("select itr from ItcsTagRead itr where itr.zoneId like ?1 and tagReadDatetime  between  (current_timestamp - 5) and current_timestamp")
    List<ItcsTagRead> findByZoneId(String zoneId);

}
