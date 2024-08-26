
import com.tinqinacademy.hotel.api.contracts.RestApiRoutes;
import com.tinqinacademy.hotel.api.errors.Error;
import com.tinqinacademy.hotel.api.errors.Errors;
import com.tinqinacademy.hotel.api.operations.deleteroom.DeleteRoomInput;
import com.tinqinacademy.hotel.api.operations.deleteroom.DeleteRoomOutput;
import com.tinqinacademy.hotel.core.operations.DeleteRoomOperationProcessor;
import com.tinqinacademy.hotel.rest.controllers.HotelController;
import io.vavr.control.Either;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

class DeleteRoomControllerIntegrationTest {


    private MockMvc mockMvc;
//не трябва да моквам DeleteRoomOperationProcessor, трябва да викам ендпойнта в MockMvc метода перформ и ендпойта, инжектвам Репото и проверявам дали наистина е станало, трябва да вкарам данни в теста
    @Mock
    private DeleteRoomOperationProcessor deleteRoomOperationProcessor;

    @InjectMocks
    private HotelController hotelController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(hotelController).build();
    }

    @Test
    void testDeleteRoom_Success() throws Exception {
        String roomId = "validRoomId";
        DeleteRoomOutput output = new DeleteRoomOutput("Room is removed!");
        when(deleteRoomOperationProcessor.process(any(DeleteRoomInput.class)))
                .thenReturn(Either.right(output));

        mockMvc.perform(delete(RestApiRoutes.DELETE_ROOM, roomId))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"message\":\"Room is removed!\"}"));
    }

    @Test
    void testDeleteRoom_InvalidInput() throws Exception {
        String roomId = "invalidRoomId";
        Errors errors = new Errors(List.of(new Error("Invalid data")));
        when(deleteRoomOperationProcessor.process(any(DeleteRoomInput.class)))
                .thenReturn(Either.left(errors));

        mockMvc.perform(delete("/api/v1/hotel/deleteRoomById/{id}", roomId))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Invalid data"));
    }
}
