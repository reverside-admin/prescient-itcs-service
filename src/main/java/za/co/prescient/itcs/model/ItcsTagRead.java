package za.co.prescient.itcs.model;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.codec.binary.Hex;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name= "tag_blink")
public class ItcsTagRead {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Getter
    @Setter
    @Column(name = "id")
    private Integer id;

    @Column(name = "tag_id")
    private byte[] guestCard;

    public String getGuestCard() {
        return new String(Hex.encodeHex(guestCard));
    }

    public void setGuestCard(String guestCard) {
        try{
            this.guestCard = Hex.decodeHex(guestCard.toCharArray());
        } catch (Exception e) {
            this.guestCard = new byte[0];
        }
    }

    @Getter
    @Setter
    @Column(name = "zone_id")
    private String zoneId;

    @Getter
    @Setter
    @Column(name = "x_location")
    private Float xCoordRead;

    @Getter
    @Setter
    @Column(name = "y_location")
    private Float yCoordRead;

    @Getter
    @Setter
    @Column(name = "locate_time")
    private Date tagReadDatetime;

}
