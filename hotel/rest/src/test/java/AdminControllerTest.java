import com.fasterxml.jackson.databind.ObjectMapper;
import com.tinqinacademy.hotel.api.contracts.RestApiRoutes;
import com.tinqinacademy.hotel.api.operations.partialupdate.PartialUpdateInput;
import com.tinqinacademy.hotel.api.operations.partialupdate.PartialUpdateOutput;
import com.tinqinacademy.hotel.api.operations.updateroom.UpdateRoomInput;
import com.tinqinacademy.hotel.api.operations.visitorregistration.input.VisitorRegistrationInput;
import com.tinqinacademy.hotel.api.operations.visitorregistration.output.VisitorRegistrationOutput;
import com.tinqinacademy.hotel.persistence.entitites.Room;
import com.tinqinacademy.hotel.persistence.models.BathroomType;
import com.tinqinacademy.hotel.persistence.models.RoomStatus;
import com.tinqinacademy.hotel.persistence.repository.RoomRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ContextConfiguration(classes = {HotelTestConfig.class})
@ComponentScan(basePackages = {"com.tinqinacademy.hotel"})
class AdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private final UUID TEST_ROOM_ID = UUID.fromString("587ce90e-f191-4860-9d90-609652d43db2");

    @BeforeEach
    void setUp () {
        Room testRoom = new Room();
        testRoom.setId(TEST_ROOM_ID);
        testRoom.setPrice(BigDecimal.valueOf(100.00));
        testRoom.setRoomFloor(2);
        testRoom.setRoomNumber("102");
        testRoom.setBathroomType(BathroomType.PRIVATE);
        testRoom.setStatus(RoomStatus.AVAILABLE);

        roomRepository.save(testRoom);
    }

    @Test
    void testRegisterVisitor_Success () throws Exception {
        VisitorRegistrationInput input = VisitorRegistrationInput.builder()
                .roomId(TEST_ROOM_ID.toString())
                .startDate(LocalDate.now().plusDays(1))
                .endDate(LocalDate.now().plusDays(5))
                .firstName("Jane")
                .lastName("Doe")
                .phoneNo("0987654321")
                .idCardNo("ID123456789")
                .idCardValidity(LocalDate.now().plusYears(5))
                .idCardIssueAuthority("Authority")
                .idCardIssueDate(LocalDate.now().minusYears(1))
                .build();

        MvcResult result = mockMvc.perform(post(RestApiRoutes.REGISTER_VISITOR)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isOk())
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        VisitorRegistrationOutput output = objectMapper.readValue(responseBody, VisitorRegistrationOutput.class);

        assertThat(output.getGuestId()).isNotNull();
    }

    @Test
    void testUpdateRoom_Success () throws Exception {
        UpdateRoomInput updateRoomInput = UpdateRoomInput.builder()
                .room_floor(33)
                .room_number("203")
                .bathroomType(BathroomType.UNKNOWN.name())
                .price(BigDecimal.valueOf(150.00))
                .bedSize("KING")
                .build();

        MvcResult result = mockMvc.perform(put(RestApiRoutes.UPDATE_ROOM, TEST_ROOM_ID)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(updateRoomInput)))
                .andExpect(status().isOk())
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        assertThat(responseBody).contains("Room information successfully updated");
    }

    @Test
    void testUpdateRoom_NotFound () throws Exception {
        UUID nonExistentRoomId = UUID.randomUUID();

        UpdateRoomInput updateRoomInput = UpdateRoomInput.builder()
                .room_floor(3)
                .room_number("203")
                .bathroomType(BathroomType.PRIVATE.name())
                .price(BigDecimal.valueOf(150.00))
                .bedSize("KING")
                .build();

        MvcResult result = mockMvc.perform(put(RestApiRoutes.UPDATE_ROOM, nonExistentRoomId)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(updateRoomInput)))
                .andExpect(status().isNotFound())
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        assertThat(responseBody).contains("Room not found");
    }

    @Test
    void testPartialUpdateRoom_Success () throws Exception {
        PartialUpdateInput partialUpdateInput = PartialUpdateInput.builder()
                .price(BigDecimal.valueOf(175.00))
                .build();

        MvcResult result = mockMvc.perform(patch(RestApiRoutes.UPDATE_ROOM, TEST_ROOM_ID)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(partialUpdateInput)))
                .andExpect(status().isOk())
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        PartialUpdateOutput output = objectMapper.readValue(responseBody, PartialUpdateOutput.class);

        assertThat(output.getRoomId()).isEqualTo(TEST_ROOM_ID.toString());
    }

    @Test
    void testPartialUpdateRoom_NotFound () throws Exception {
        UUID nonExistentRoomId = UUID.randomUUID();

        PartialUpdateInput partialUpdateInput = PartialUpdateInput.builder()
                .price(BigDecimal.valueOf(175.00))
                .build();

        MvcResult result = mockMvc.perform(patch(RestApiRoutes.UPDATE_ROOM, nonExistentRoomId)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(partialUpdateInput)))
                .andExpect(status().isNotFound())
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        assertThat(responseBody).contains("Room not found");
    }

    @Test
    void testDeleteRoom_NotFound () throws Exception {
        UUID nonExistentRoomId = UUID.randomUUID();

        mockMvc.perform(delete(RestApiRoutes.DELETE_ROOM, nonExistentRoomId.toString()))
                .andExpect(status().isNotFound()) // Очаквайте статус код 404, ако стаята не е намерена
                .andExpect(content().string("Room not found!"));
    }

    @Test
    void testRegisterVisitor_BadRequest () throws Exception {
        // Създаване на VisitorRegistrationInput с невалидни данни
        VisitorRegistrationInput input = VisitorRegistrationInput.builder()
                .roomId(TEST_ROOM_ID.toString()) // Валиден ID на стая
                .startDate(LocalDate.now().plusDays(5))
                .endDate(LocalDate.now().plusDays(1)) // Невалидни дати (startDate > endDate)
                .firstName("") // Неправилен формат на името
                .build();

        mockMvc.perform(post(RestApiRoutes.REGISTER_VISITOR)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isBadRequest()) // Очаквайте статус код 400, ако данните са невалидни
                .andExpect(jsonPath("$.errors[0].message").value("Cannot invoke \"java.util.List.stream()\" because the return value of \"com.tinqinacademy.hotel.api.errors.Errors.getErrors()\" is null"));
    }

    @Test
    void testUpdateRoom_BadRequest() throws Exception {
        // Създаване на UpdateRoomInput с невалидни данни
        UpdateRoomInput updateRoomInput = UpdateRoomInput.builder()
                .room_floor(-1) // Невалиден етаж
                .room_number("203")
                .bathroomType("UNKNOWN") // Невалиден тип баня
                .price(BigDecimal.valueOf(-150.00)) // Невалидна цена
                .bedSize(null) // Липсваща стойност
                .build();

        mockMvc.perform(put(RestApiRoutes.UPDATE_ROOM, TEST_ROOM_ID)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(updateRoomInput)))
                .andDo(print()) // Отпечатване на отговора
                .andExpect(status().isBadRequest()) // Очаквайте статус код 400, ако данните са невалидни
                .andExpect(jsonPath("$.errors[0].message").value("Field 'room_floor': must be greater than 0"))
                .andExpect(jsonPath("$.errors[1].message").value("Field 'bedSize': must not be null"))
                .andExpect(jsonPath("$.errors[2].message").value("Field 'price': must be greater than 0"))
                .andExpect(jsonPath("$.errors[3].message").value("Invalid value for bathroomType: UNKNOWN"));
    }


}