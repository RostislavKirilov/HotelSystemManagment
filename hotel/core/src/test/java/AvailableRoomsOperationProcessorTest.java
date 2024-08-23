import com.tinqinacademy.hotel.api.errors.ErrorMapper;
import com.tinqinacademy.hotel.api.errors.ErrorOutput;
import com.tinqinacademy.hotel.api.errors.Errors;
import com.tinqinacademy.hotel.api.operations.checkavailablerooms.AvailableRoomsInput;
import com.tinqinacademy.hotel.api.operations.checkavailablerooms.AvailableRoomsOutput;
import com.tinqinacademy.hotel.core.operations.AvailableRoomsOperationProcessor;
import com.tinqinacademy.hotel.persistence.entitites.Room;
import com.tinqinacademy.hotel.persistence.repository.RoomRepository;
import io.vavr.control.Either;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AvailableRoomsOperationProcessorTest {

    @Mock
    private RoomRepository roomRepository;

    @Mock
    private ErrorMapper errorMapper;

    @InjectMocks
    private AvailableRoomsOperationProcessor availableRoomsOperationProcessor;

    @BeforeEach
    void setUp () {
        MockitoAnnotations.openMocks(this);
    }
}