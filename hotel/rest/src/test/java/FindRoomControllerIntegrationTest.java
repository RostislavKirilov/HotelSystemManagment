import com.tinqinacademy.hotel.api.contracts.RestApiRoutes;
import com.tinqinacademy.hotel.api.operations.findroom.FindRoomInput;
import com.tinqinacademy.hotel.api.operations.findroom.RoomId;
import com.tinqinacademy.hotel.core.operations.FindRoomOperationProcessor;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@SpringBootTest(classes = HotelApplication.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class FindRoomControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FindRoomOperationProcessor findRoomOperationProcessor;

    @Test
    public void testFindRoomByIdSuccess() throws Exception {
        RoomId mockRoom = RoomId.builder()
                .roomId("validRoomId")
                .price(BigDecimal.valueOf(100.00))
                .floor("1")
                .bedSize("Double")
                .bathroomType("Standard")
                .datesOccupied(List.of(LocalDateTime.now().minusDays(1), LocalDateTime.now()))
                .build();

        Mockito.when(findRoomOperationProcessor.process(any(FindRoomInput.class)))
                .thenReturn(Either.right(mockRoom));

        String roomId = UUID.randomUUID().toString();

        mockMvc.perform(get(RestApiRoutes.FIND_ROOM, roomId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())  // Очакваме 200 OK
                .andExpect(jsonPath("$.roomId").value("validRoomId"))
                .andExpect(jsonPath("$.price").value(100.00))
                .andExpect(jsonPath("$.floor").value("1"))
                .andExpect(jsonPath("$.bedSize").value("Double"))
                .andExpect(jsonPath("$.bathroomType").value("Standard"))
                .andExpect(jsonPath("$.datesOccupied").isArray());
    }
}
