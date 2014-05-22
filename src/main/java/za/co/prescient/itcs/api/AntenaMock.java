package za.co.prescient.itcs.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import za.co.prescient.itcs.model.ItcsTagRead;
import za.co.prescient.itcs.model.ItcsTagReadHistory;
import za.co.prescient.itcs.repository.ItcsTagReadHistoryRepository;
import za.co.prescient.itcs.repository.ItcsTagReadRepository;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

@RestController
public class AntenaMock {

    private static Thread mocker;

    @Autowired
    private ItcsTagReadRepository itcsTagReadRepository;

    @Autowired
    private ItcsTagReadHistoryRepository itcsTagReadHistoryRepository;

    @RequestMapping(value = "mock/start")
    public void startMock() {
        if (mocker == null) {
            mocker = new Mocker(itcsTagReadRepository, itcsTagReadHistoryRepository);
            mocker.start();
            System.out.println("Started Mocker");
        }

    }

    @RequestMapping(value = "mock/stop")
    public void stopMock() {
        if (mocker != null) {
            if (mocker.isAlive()) {
                mocker.stop();
                mocker = null;
                System.out.println("Stopped Mocker");
            }
        }
    }

    public static class Mocker extends Thread {

        private ItcsTagReadRepository itcsTagReadRepository;
        private ItcsTagReadHistoryRepository itcsTagReadHistoryRepository;
        private static final Float MAX_X = 6.3f;
        private static final Float MAX_Y = 8.2f;
        private static final Integer DELAY  = 1000;

        public Mocker(ItcsTagReadRepository itcsTagReadRepository, ItcsTagReadHistoryRepository itcsTagReadHistoryRepository) {
            this.itcsTagReadRepository = itcsTagReadRepository;
            this.itcsTagReadHistoryRepository = itcsTagReadHistoryRepository;
        }

        public void run() {
            int count = 0;
            while (true) {
                System.out.println("Mocking Antena ....");
                count++;
                insert(count);
                try {
                    sleep(DELAY);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if(count>60) count = 0;
                clear();
            }
        }

        @Transactional
        public void insert(int count) {
            if(count < 30 ) {
                itcsTagReadRepository.save(getItcsTagRead("Breakwater_Restaurant", "40000565"));
            } else {
                itcsTagReadRepository.save(getItcsTagRead("Flood_Bar", "40000565"));
            }

            itcsTagReadRepository.save(getItcsTagRead("Fountain_Coffee_Bar", "300833B2DDD9014000000000"));

            itcsTagReadRepository.save(getItcsTagRead("Breakwater_Restaurant", "40000753"));
            itcsTagReadRepository.save(getItcsTagRead("Breakwater_Restaurant", "40000754"));
            itcsTagReadRepository.save(getItcsTagRead("Breakwater_Restaurant", "40000755"));
            itcsTagReadRepository.save(getItcsTagRead("Breakwater_Restaurant", "40000756"));

//            itcsTagReadRepository.save(getItcsTagRead("TP1", "40000565"));
//            itcsTagReadRepository.save(getItcsTagRead("TP1", "40000565"));

        }

        @Transactional
        public void clear() {
            List<ItcsTagReadHistory> history = new ArrayList<ItcsTagReadHistory>();
            for(ItcsTagRead itcsTagRead : itcsTagReadRepository.findAll()){
                history.add(getItcsTagReadHistory(itcsTagRead));
            }
            itcsTagReadHistoryRepository.save(history);
            itcsTagReadRepository.deleteAll();        }


        private ItcsTagRead getItcsTagRead(String zoneId, String cardId) {
            ItcsTagRead itcsTagRead = new ItcsTagRead();
            itcsTagRead.setZoneId(zoneId);
            itcsTagRead.setGuestCard(cardId);
            itcsTagRead.setTagReadDatetime(new Date());
            int x = new Random().nextInt(MAX_X.intValue());
            int y = new Random().nextInt(MAX_Y.intValue());
            itcsTagRead.setXCoordRead((float)x);
            itcsTagRead.setYCoordRead((float)y);
            return itcsTagRead;
        }

        private ItcsTagReadHistory getItcsTagReadHistory(ItcsTagRead itcsTagRead) {
            ItcsTagReadHistory itcsTagReadHistory = new ItcsTagReadHistory();
            itcsTagReadHistory.setZoneId(itcsTagRead.getZoneId());
            itcsTagReadHistory.setGuestCard(itcsTagRead.getGuestCard());
            itcsTagReadHistory.setTagReadDatetime(itcsTagRead.getTagReadDatetime());
            itcsTagReadHistory.setXCoordRead(itcsTagRead.getXCoordRead());
            itcsTagReadHistory.setYCoordRead(itcsTagRead.getYCoordRead());
            return itcsTagReadHistory;
        }

    }

}
