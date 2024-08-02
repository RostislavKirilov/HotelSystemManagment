package com.tinqinacademy.hotel.api.operations.partialupdate;

import com.tinqinacademy.hotel.api.base.OperationInput;
import com.tinqinacademy.hotel.persistence.entitites.BedEntity;
import com.tinqinacademy.hotel.persistence.entitites.Room;
import com.tinqinacademy.hotel.persistence.models.BathroomType;
import com.tinqinacademy.hotel.persistence.models.Bed;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class PartialUpdateInput implements OperationInput {
    private String roomId;
    private String bed_size;
    private String bathroomType;
    private Integer floor;
    private Integer roomNo;
    private Double price;

    public Room toRoomEntity( Room existingRoom) {
        if (bed_size != null && !bed_size.isEmpty()) {
            Bed bed = Bed.getByCode(bed_size.toLowerCase());
            if (bed == Bed.UNKNOWN) {
                throw new IllegalArgumentException("Invalid bed size: " + bed_size);
            }
            existingRoom.getBeds().clear();
            BedEntity bedEntity = new BedEntity();
            bedEntity.setId(UUID.randomUUID());
            bedEntity.setBed(bed);
            bedEntity.setRoom(existingRoom);
            existingRoom.getBeds().add(bedEntity);
        }

        if (bathroomType != null && !bathroomType.isEmpty()) {
            BathroomType type = BathroomType.getByCode(bathroomType.toLowerCase());
            if (type == BathroomType.UNKNOWN) {
                throw new IllegalArgumentException("Invalid bathroom type: " + bathroomType);
            }
            existingRoom.setBathroomType(type);
        }

        if (floor != null) {
            existingRoom.setRoomFloor(floor);
        }

        if (roomNo != null) {
            existingRoom.setRoomNumber(String.valueOf(roomNo));
        }

        return existingRoom;
    }
}
