package za.co.prescient.itcs.api;

import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import za.co.prescient.itcs.model.ItcsTagRead;
import za.co.prescient.itcs.model.ItcsTagReadHistory;
import za.co.prescient.itcs.repository.ItcsTagReadHistoryRepository;
import za.co.prescient.itcs.repository.ItcsTagReadRepository;

import java.util.*;

@RestController
@Log
public class Services {

    @Autowired
    private ItcsTagReadRepository itcsTagReadRepository;

    @Autowired
    private ItcsTagReadHistoryRepository itcsTagReadHistoryRepository;

    int aMaxX = 6300;
    int aMaxY = 8200;

//    int uMaxX = 841;
//    int uMaxY = 646;

    float xOffset = 2.7f;
    float yOffset = 0.6f;


    /**
     * Find Tags Currently Present In A Touch Point
     *
     * @param touchPointId touch point id
     * @param uMaxX         max x co-ordinate value of the touch point
     * @param uMaxY         max y co-ordinate value of the touch point
     * @return list of tags found currently in touch point
     */
    @RequestMapping(value = "touchpoints/{touchPointId}/tags/now/{maxX}/{maxY}")
    public Collection<ItcsTagRead> findLocationOfCurrentlyPresentGuestCardsInTouchPoint(@PathVariable("touchPointId") String touchPointId,
                                                                                        @PathVariable("maxX") Integer uMaxX,
                                                                                        @PathVariable("maxY") Integer uMaxY) {
        Collection<ItcsTagRead> itcsTagReads = itcsTagReadRepository.findCurrentCardsWithLocationInZone(getTime(new Date(), -10), getTime(new Date(), +1), touchPointId);
        Map<String, ItcsTagRead> itcsTagReadMap = new HashMap<String, ItcsTagRead>();
        // Filtering out duplicate cards in the tag list
        for (ItcsTagRead itcsTagRead : itcsTagReads) {
            itcsTagReadMap.put(itcsTagRead.getGuestCard(), itcsTagRead);
        }

        // Map the x and y co-ordinate values in the ItcsTagRead within maxX, maxy

        Collection<ItcsTagRead> cards = itcsTagReadMap.values();
        log.info("cards present "+cards.size());

        //test start

        for (ItcsTagRead tagLocationLog : cards) {
            Float aXDouble = 0.0f;

            log.info("X-Coordinate From Antenna : " + tagLocationLog.getXCoordRead());
            log.info("Y-Coordinate From Antenna : " + tagLocationLog.getYCoordRead());

            aXDouble = (tagLocationLog.getXCoordRead() + xOffset) * 1000;
            if(aXDouble > aMaxX) aXDouble = new Float(aMaxX);

            Float aYDouble = (tagLocationLog.getYCoordRead() - yOffset) * 1000;

            Float uXDouble;
            if (aXDouble.intValue() > aMaxX) {
                uXDouble = new Float(uMaxX);
            } else {
                uXDouble = (uMaxX * aXDouble) / aMaxX;
            }

            Float uYDouble;
            if (aYDouble.intValue() > aMaxY) {
                uYDouble = new Float(uMaxY);
            } else {
                uYDouble = (uMaxY * aYDouble) / aMaxY;
            }

            tagLocationLog.setXCoordRead(new Float(uXDouble.intValue()));
            tagLocationLog.setYCoordRead(new Float(uYDouble.intValue()));

            log.info("X-Coordinate after Calculation : " + tagLocationLog.getXCoordRead());
            log.info("Y-Coordinate after Calculation : " + tagLocationLog.getYCoordRead());
        }

        //test end

        return cards;
    }

    @RequestMapping(value = "tags/{tagId}/history")
    public List<ItcsTagReadHistory> getGuestHistory(@PathVariable("tagId") String tagId) {
        List<ItcsTagReadHistory> itc = itcsTagReadHistoryRepository.findGuestLocation(tagId);
        log.info("return list size::" + itc.size());
        return itc;
    }

    @RequestMapping(value = "tags/{tagId}/now")
    public ItcsTagRead getGuestCardHistory(@PathVariable("tagId") String tagId) {
         List<ItcsTagRead> itcsTagRead= itcsTagReadRepository.findByGuestCardOrderByTagReadDatetimeDesc(tagId);
        return itcsTagRead.get(0);
    }

    @RequestMapping(value = "touchpoints/{touchPointId}/tags")
    public Set<String> findCurrentlyPresentGuestCardsInTouchPoint(@PathVariable("touchPointId") String touchPointId) {
        List<ItcsTagRead> cardList = itcsTagReadRepository.findByZoneId(touchPointId);
        Set<String> cardIdList = new HashSet<String>();
        // Extract card id and filter duplicates
        for(ItcsTagRead card : cardList){
            cardIdList.add(card.getGuestCard());
        }
        return cardIdList;
    }

    private Date getTime(Date date, int intervalSeconds) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.SECOND, intervalSeconds);
        return calendar.getTime();
    }

}
