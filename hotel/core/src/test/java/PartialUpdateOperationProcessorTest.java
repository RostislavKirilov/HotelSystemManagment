import com.tinqinacademy.hotel.api.errors.ErrorMapper;
import com.tinqinacademy.hotel.api.errors.Errors;
import com.tinqinacademy.hotel.api.operations.partialupdate.PartialUpdateInput;
import com.tinqinacademy.hotel.api.operations.partialupdate.PartialUpdateOutput;
import com.tinqinacademy.hotel.core.operations.PartialUpdateOperationProcessor;
import com.tinqinacademy.hotel.persistence.entitites.Room;
import com.tinqinacademy.hotel.persistence.models.BathroomType;
import com.tinqinacademy.hotel.persistence.models.Bed;
import com.tinqinacademy.hotel.persistence.repository.RoomRepository;
import io.vavr.control.Either;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.core.convert.ConversionService;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class PartialUpdateOperationProcessorTest {

    @Mock
    private RoomRepository roomRepository;

    @Mock
    private Validator validator;

    @Mock
    private ConversionService conversionService;

    @Mock
    private ErrorMapper errorMapper;

    @InjectMocks
    private PartialUpdateOperationProcessor partialUpdateOperationProcessor;

    @BeforeEach
    void setUp () {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testProcess_Success () {
        // Мокваме съществуваща стая
        UUID roomId = UUID.randomUUID();
        Room room = new Room();
        room.setId(roomId);
        room.setRoomFloor(2);
        room.setBathroomType(BathroomType.PRIVATE);
        room.setPrice(BigDecimal.valueOf(100.00));
        room.setBedSize(Bed.QUEEN_SIZE);
        room.setRoomNumber("101");

        when(roomRepository.findById(roomId)).thenReturn(Optional.of(room));
        when(roomRepository.save(any(Room.class))).thenReturn(room);

        PartialUpdateInput input = PartialUpdateInput.builder()
                .roomId(roomId.toString())
                .floor(3)
                .bathroomType("SHARED")
                .price(BigDecimal.valueOf(120.00))
                .bed_size("king")
                .build();

        Either<Errors, PartialUpdateOutput> result = partialUpdateOperationProcessor.process(input);

        assertTrue(result.isRight());
        assertNotNull(result.get());
        assertEquals(roomId.toString(), result.get().getRoomId());
        assertEquals(3, room.getRoomFloor());
        assertEquals(BathroomType.SHARED, room.getBathroomType());
        assertEquals(BigDecimal.valueOf(120.00), room.getPrice());
        assertEquals(Bed.KING_SIZE, room.getBedSize());
    }

    @Test
    void testProcess_RuntimeException() {
        UUID roomId = UUID.randomUUID();
        when(roomRepository.findById(roomId)).thenThrow(new RuntimeException("Unexpected error"));

        PartialUpdateInput input = PartialUpdateInput.builder()
                .roomId(roomId.toString())
                .build();

        Either<Errors, PartialUpdateOutput> result = partialUpdateOperationProcessor.process(input);

        assertTrue(result.isLeft());
        assertEquals("Unexpected error!", result.getLeft().getErrors().get(0).getMessage());
    }

    @Test
    void testProcess_InvalidInput() {
        doThrow(new IllegalArgumentException("Invalid input")).when(validator).validate(any());

        PartialUpdateInput input = PartialUpdateInput.builder()
                .roomId(UUID.randomUUID().toString())
                .build();

        Either<Errors, PartialUpdateOutput> result = partialUpdateOperationProcessor.process(input);

        assertTrue(result.isLeft());
        assertEquals("Invalid input data!", result.getLeft().getErrors().get(0).getMessage());
    }

    @Test
    void testProcess_RoomNotFound() {
        UUID roomId = UUID.randomUUID();
        when(roomRepository.findById(roomId)).thenReturn(Optional.empty());

        PartialUpdateInput input = PartialUpdateInput.builder()
                .roomId(roomId.toString())
                .build();

        Either<Errors, PartialUpdateOutput> result = partialUpdateOperationProcessor.process(input);

        assertTrue(result.isLeft());
        assertEquals("Room not found! for ID: " + roomId, result.getLeft().getErrors().get(0).getMessage());
    }
}