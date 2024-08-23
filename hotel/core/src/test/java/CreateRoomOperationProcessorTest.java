
import com.tinqinacademy.hotel.api.errors.ErrorMapper;
import com.tinqinacademy.hotel.api.errors.Errors;
import com.tinqinacademy.hotel.api.errors.Error;
import com.tinqinacademy.hotel.api.operations.createroom.CreateRoomInput;
import com.tinqinacademy.hotel.api.operations.createroom.CreateRoomOutput;
import com.tinqinacademy.hotel.core.operations.CreateRoomOperationProcessor;
import com.tinqinacademy.hotel.persistence.entitites.Room;
import com.tinqinacademy.hotel.persistence.models.BathroomType;
import com.tinqinacademy.hotel.persistence.models.RoomStatus;
import com.tinqinacademy.hotel.persistence.repository.RoomRepository;
import io.vavr.control.Either;
import io.vavr.control.Try;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class CreateRoomOperationProcessorTest {

    @Mock
    private RoomRepository roomRepository;

    @Mock
    private ErrorMapper errorMapper;

    @InjectMocks
    private CreateRoomOperationProcessor createRoomOperationProcessor;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateRoom_Success() {
        CreateRoomInput input = CreateRoomInput.builder()
                .roomFloor(1)
                .roomNumber("101")
                .bathroomType(BathroomType.PRIVATE.name())
                .price(BigDecimal.valueOf(100))
                .build();

        Room room = Room.builder()
                .roomFloor(1)
                .roomNumber("101")
                .bathroomType(BathroomType.SHARED)
                .price(BigDecimal.valueOf(100.0))
                .status(RoomStatus.AVAILABLE)
                .build();

        when(roomRepository.save(any(Room.class))).thenReturn(room);

        Either<Errors, CreateRoomOutput> result = createRoomOperationProcessor.process(input);

        assertTrue(result.isRight(), "Expected result to be a right value containing the output");
        assertEquals("Room added successfully!", result.get().getMessage());
    }

    @Test
    void testCreateRoom_InvalidBathroomType() {
        CreateRoomInput input = CreateRoomInput.builder()
                .roomFloor(1)
                .roomNumber("101")
                .bathroomType("INVALID_TYPE")
                .price(BigDecimal.valueOf(100.0))
                .build();

        Either<Errors, CreateRoomOutput> result = createRoomOperationProcessor.process(input);

        assertTrue(result.isLeft(), "Expected result to be a left value containing an error");
        assertEquals("Invalid value for bathroomType: INVALID_TYPE", result.getLeft().getErrors().get(0).getMessage());
    }

    @Test
    void testCreateRoom_ExceptionDuringSave() {
        CreateRoomInput input = CreateRoomInput.builder()
                .roomFloor(1)
                .roomNumber("101")
                .bathroomType(BathroomType.UNKNOWN.name())
                .price(BigDecimal.valueOf(100.0))
                .build();

        when(roomRepository.save(any(Room.class))).thenThrow(new RuntimeException("Database error"));

        Either<Errors, CreateRoomOutput> result = createRoomOperationProcessor.process(input);

        assertTrue(result.isLeft(), "Expected result to be a left value containing an error");
        assertEquals("Database error", result.getLeft().getErrors().get(0).getMessage());
    }
}
