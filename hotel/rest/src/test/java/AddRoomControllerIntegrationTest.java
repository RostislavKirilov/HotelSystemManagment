import com.tinqinacademy.hotel.api.contracts.RestApiRoutes;
import com.tinqinacademy.hotel.api.operations.createroom.CreateRoomInput;
import com.tinqinacademy.hotel.api.operations.createroom.CreateRoomOutput;
import com.tinqinacademy.hotel.core.operations.CreateRoomOperationProcessor;
import com.tinqinacademy.hotel.rest.HotelApplication;
import io.vavr.control.Either;
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
public class AddRoomControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CreateRoomOperationProcessor createRoomOperationProcessor;

    @Test
    public void testAddRoomSuccess() throws Exception {
        // Mock successful room creation
        CreateRoomOutput mockOutput = CreateRoomOutput.builder()
                .message("Room added successfully!")
                .build();

        Mockito.when(createRoomOperationProcessor.process(any(CreateRoomInput.class)))
                .thenReturn(Either.right(mockOutput));

        String inputJson = "{ \"roomFloor\": 1, \"roomNumber\": \"101\", \"bathroomType\": \"STANDARD\", \"price\": 100.00 }";

        mockMvc.perform(post(RestApiRoutes.ADD_ROOM)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(inputJson))
                .andExpect(status().isCreated())  // Expecting 201 Created
                .andExpect(jsonPath("$.message").value("Room added successfully!"));  // Verify response body
    }

    @Test
    public void testAddRoomInternalServerError() throws Exception {
        // Mocking an internal server error
        Mockito.when(createRoomOperationProcessor.process(any(CreateRoomInput.class)))
                .thenThrow(new RuntimeException("Internal server error"));

        String inputJson = "{ \"roomFloor\": 1, \"roomNumber\": \"101\", \"bathroomType\": \"STANDARD\", \"price\": 100.00 }";

        mockMvc.perform(post(RestApiRoutes.ADD_ROOM)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(inputJson))
                .andExpect(status().isInternalServerError());  // Expecting 500 Internal Server Error
    }
}