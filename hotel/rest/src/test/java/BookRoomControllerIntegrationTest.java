import com.tinqinacademy.hotel.api.contracts.RestApiRoutes;
import com.tinqinacademy.hotel.api.operations.bookroom.BookRoomInput;
import com.tinqinacademy.hotel.api.operations.bookroom.BookRoomOutput;
import com.tinqinacademy.hotel.api.errors.Errors;
import com.tinqinacademy.hotel.core.operations.BookRoomOperationProcessor;
import com.tinqinacademy.hotel.rest.HotelApplication;
import io.vavr.control.Either;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest(classes = HotelApplication.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class BookRoomControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookRoomOperationProcessor bookRoomOperationProcessor;

    @Test
    public void testBookRoomSuccess() throws Exception {
        String inputJson = "{ \"userId\": \"user123\", \"startDate\": \"2024-08-23\", \"endDate\": \"2024-08-25\" }";

        BookRoomOutput mockOutput = BookRoomOutput.builder()
                .bookingId("123")
                .build();

        Mockito.when(bookRoomOperationProcessor.process(any(BookRoomInput.class)))
                .thenReturn(Either.right(mockOutput));

        mockMvc.perform(post(RestApiRoutes.BOOK_ROOM, "123")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(inputJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.bookingId").value("123"));
    }

    @Test
    public void testBookRoomRoomOrUserNotFound() throws Exception {
        // Mocking a "Room or user not found" error
        Errors mockErrors = Errors.builder()
                .message("Room or user not found")
                .build();

        Mockito.when(bookRoomOperationProcessor.process(any(BookRoomInput.class)))
                .thenReturn(Either.left(mockErrors));

        // Provide input JSON with valid data
        String inputJson = "{ \"userId\": \"user123\", \"startDate\": \"2024-08-23\", \"endDate\": \"2024-08-25\" }";

        mockMvc.perform(post(RestApiRoutes.BOOK_ROOM, "123")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(inputJson))
                .andExpect(status().isBadRequest())  // Expecting 400 Bad Request
                .andExpect(jsonPath("$.message").value("Room or user not found"));  // Verifying the message
    }

    @Test
    public void testBookRoomInternalServerError() throws Exception {
        // Mocking an internal server error
        Mockito.when(bookRoomOperationProcessor.process(any(BookRoomInput.class)))
                .thenThrow(new RuntimeException("Internal server error"));

        String inputJson = "{ \"userId\": \"user123\", \"startDate\": \"2024-08-23\", \"endDate\": \"2024-08-25\" }";

        mockMvc.perform(post(RestApiRoutes.BOOK_ROOM, "123")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(inputJson))
                .andExpect(status().isInternalServerError());  // Expecting 500 Internal Server Error
    }
}
