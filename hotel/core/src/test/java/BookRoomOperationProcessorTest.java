
import com.tinqinacademy.hotel.api.errors.ErrorMapper;
import com.tinqinacademy.hotel.api.errors.Errors;
import com.tinqinacademy.hotel.api.messages.ExceptionMessages;
import com.tinqinacademy.hotel.api.operations.bookroom.BookRoomInput;
import com.tinqinacademy.hotel.api.operations.bookroom.BookRoomOutput;
import com.tinqinacademy.hotel.core.operations.BookRoomOperationProcessor;
import com.tinqinacademy.hotel.persistence.entitites.Booking;
import com.tinqinacademy.hotel.persistence.entitites.Room;
import com.tinqinacademy.hotel.persistence.entitites.User;
import com.tinqinacademy.hotel.persistence.models.RoomStatus;
import com.tinqinacademy.hotel.persistence.repository.BookingRepository;
import com.tinqinacademy.hotel.persistence.repository.RoomRepository;
import com.tinqinacademy.hotel.persistence.repository.UserRepository;
import io.vavr.control.Either;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.core.convert.ConversionService;

import java.time.LocalDate;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class BookRoomOperationProcessorTest {

    @InjectMocks
    private BookRoomOperationProcessor bookRoomOperationProcessor;

    @Mock
    private RoomRepository roomRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private Validator validator;

    @Mock
    private ConversionService conversionService;

    @Mock
    private ErrorMapper errorMapper;

    @BeforeEach
    public void setup() {
        // Ръчна инициализация на Mockito анотации
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testBookRoom_Success() {
        // Генериране на валиден UUID
        UUID roomId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        // Създаване на BookRoomInput с валидни UUID
        BookRoomInput input = BookRoomInput.builder()
                .roomId(roomId.toString())  // Използвайте валиден UUID
                .userId(userId.toString())  // Използвайте валиден UUID
                .startDate(LocalDate.now())
                .endDate(LocalDate.now().plusDays(3))
                .build();

        // Създаване на Room и User обекти
        Room room = new Room();
        room.setStatus(RoomStatus.AVAILABLE);
        User user = new User();

        // Mock-ване на поведението на roomRepository, userRepository и bookingRepository
        Mockito.when(roomRepository.findById(roomId)).thenReturn(Optional.of(room));
        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        Mockito.when(bookingRepository.findConflictingBookings(Mockito.any(UUID.class), Mockito.any(LocalDate.class), Mockito.any(LocalDate.class)))
                .thenReturn(Collections.emptyList());

        // Създаване на Booking обект
        Booking booking = Booking.builder()
                .id(UUID.randomUUID())
                .room(room)
                .user(user)
                .startDate(LocalDate.now())
                .endDate(LocalDate.now().plusDays(3))
                .build();

        // Mock-ване на поведението на bookingRepository при save
        Mockito.when(bookingRepository.save(Mockito.any(Booking.class))).thenReturn(booking);

        Either<Errors, BookRoomOutput> result = bookRoomOperationProcessor.process(input);

        assertTrue(result.isRight());
        assertEquals(result.get().getBookingId(), String.valueOf(booking.getId()));
    }
}
