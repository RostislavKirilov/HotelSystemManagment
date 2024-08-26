import com.fasterxml.jackson.databind.ObjectMapper;
import com.tinqinacademy.hotel.api.contracts.RestApiRoutes;
import com.tinqinacademy.hotel.api.operations.bookroom.BookRoomInput;
import com.tinqinacademy.hotel.api.operations.bookroom.BookRoomOutput;
import com.tinqinacademy.hotel.api.operations.createroom.CreateRoomInput;
import com.tinqinacademy.hotel.persistence.entitites.Booking;
import com.tinqinacademy.hotel.persistence.entitites.Room;
import com.tinqinacademy.hotel.persistence.entitites.User;
import com.tinqinacademy.hotel.persistence.models.BathroomType;
import com.tinqinacademy.hotel.persistence.models.RoomStatus;
import com.tinqinacademy.hotel.persistence.repository.BookingRepository;
import com.tinqinacademy.hotel.persistence.repository.RoomRepository;
import com.tinqinacademy.hotel.persistence.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;


import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ContextConfiguration(classes = {HotelTestConfig.class})
@ComponentScan(basePackages = {"com.tinqinacademy.hotel"})
class HotelControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private Room testRoom;
    private User testUser;

    private final UUID TEST_ROOM_ID = UUID.fromString("eb4928c6-3c01-478c-b0b7-675a87bd8a91");
    private final UUID TEST_USER_ID = UUID.fromString("d91ef2d7-ecfe-454a-8799-2df92264d0da");

    @BeforeEach
    void setUp () {
        // Създаване на тестова стая, която ще бъде използвана във всички тестове
        testRoom = new Room();
        testRoom.setId(UUID.randomUUID()); // Генерира се random ID, но това ще бъде използвано коректно в теста
        testRoom.setPrice(BigDecimal.valueOf(100.00));
        testRoom.setRoomFloor(2);
        testRoom.setRoomNumber("102");
        testRoom.setBathroomType(BathroomType.PRIVATE);
        testRoom.setStatus(RoomStatus.AVAILABLE);

        // Запазване на тестовата стая в базата данни
        roomRepository.save(testRoom);

        testUser = new User();
        testUser.setId(UUID.randomUUID());
        testUser.setFirstName("John");
        testUser.setLastName("Doe");
        testUser.setUserPass("password");
        testUser.setEmail("john.doe@example.com");
        testUser.setBirthday(LocalDate.of(1990, 1, 1));
        testUser.setPhoneNumber("1234567890");
        userRepository.save(testUser);
    }


    @Test
    void testAddRoom_Success () throws Exception {
        CreateRoomInput createRoomInput = CreateRoomInput.builder()
                .roomFloor(3)
                .roomNumber("203")
                .bathroomType(BathroomType.PRIVATE.name())
                .price(BigDecimal.valueOf(150.00))
                .bedSize("QUEEN")
                .bedSizes(List.of("SINGLE", "DOUBLE"))
                .build();

        MvcResult result = mockMvc.perform(post(RestApiRoutes.ADD_ROOM)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(createRoomInput)))
                .andExpect(status().isBadRequest())
                .andReturn();

        System.out.println(result.getResponse().getContentAsString());
    }

    @Test
    void testDeleteRoom_RoomNotFound () throws Exception {
        UUID nonExistentRoomId = UUID.randomUUID();
        mockMvc.perform(delete(RestApiRoutes.DELETE_ROOM, nonExistentRoomId))
                .andExpect(status().isNotFound());
    }

    @Test
    void testRemoveBooking_Success () throws Exception {
        String bookingId = "877a695b-4b20-49bc-ae55-80a3be391edd";

        mockMvc.perform(delete(RestApiRoutes.REMOVE_BOOKING, bookingId))
                .andExpect(status().isNoContent());
    }


    @Test
    void testRemoveBooking_NotFound () throws Exception {
        String nonExistentBookingId = "810392e3-dc6b-41ba-9eaf-b63a4ee7f072";

        mockMvc.perform(delete(RestApiRoutes.REMOVE_BOOKING, nonExistentBookingId))
                .andExpect(status().isNotFound())
                .andExpect(result -> {
                    String responseBody = result.getResponse().getContentAsString();
                    assertThat(responseBody).contains("Booking not found");
                });
    }

    @Test
    void testFindRoomById_Success () throws Exception {
        UUID roomId = UUID.fromString("39ca3bd3-9e7d-414e-842f-6cbb68145f98");

        MvcResult result = mockMvc.perform(get(RestApiRoutes.FIND_ROOM, roomId))
                .andExpect(status().isOk()) // Очакваме статус код 200, ако стаята съществува
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        assertThat(responseBody).contains(roomId.toString());
    }

    @Test
    void testFindRoomById_NotFound () throws Exception {
        UUID nonExistentRoomId = UUID.randomUUID(); // Създаваме произволно, несъществуващо ID

        mockMvc.perform(get(RestApiRoutes.FIND_ROOM, nonExistentRoomId))
                .andExpect(status().isNotFound()) // Очакваме статус код 404
                .andExpect(result -> {
                    String responseBody = result.getResponse().getContentAsString();
                    assertThat(responseBody).contains("Room not found"); // Проверка за съобщението в отговора
                });
    }

    @Test
    void testBookRoom_Success () throws Exception {
        BookRoomInput bookRoomInput = BookRoomInput.builder()
                .userId(TEST_USER_ID.toString())
                .startDate(LocalDate.now().plusDays(1))
                .endDate(LocalDate.now().plusDays(5))
                .build();

        MvcResult result = mockMvc.perform(post(RestApiRoutes.BOOK_ROOM, TEST_ROOM_ID)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(bookRoomInput)))
                .andExpect(status().isOk())
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        BookRoomOutput bookRoomOutput = objectMapper.readValue(responseBody, BookRoomOutput.class);

        assertThat(bookRoomOutput.getBookingId()).isNotNull();

        // Проверка на данните за резервиране
        Booking booking = bookingRepository.findById(UUID.fromString(bookRoomOutput.getBookingId())).orElse(null);
        assertThat(booking).isNotNull();
        assertThat(booking.getRoom().getId()).isEqualTo(TEST_ROOM_ID);
        assertThat(booking.getUser().getId()).isEqualTo(TEST_USER_ID);
        assertThat(booking.getStartDate()).isEqualTo(bookRoomInput.getStartDate());
        assertThat(booking.getEndDate()).isEqualTo(bookRoomInput.getEndDate());
    }

    @Test
    void testDeleteRoom_Success_WithSpecificId() throws Exception {
        // ID на стаята, която ще изтривате
        UUID roomIdToDelete = UUID.fromString("05dc73df-84d4-48bb-a5b6-fd487aca8dec");

        // Създаване на стая с конкретното ID
        Room roomToDelete = new Room();
        roomToDelete.setId(roomIdToDelete);
        roomToDelete.setPrice(BigDecimal.valueOf(100.00));
        roomToDelete.setRoomFloor(2);
        roomToDelete.setRoomNumber("102");
        roomToDelete.setBathroomType(BathroomType.PRIVATE);
        roomToDelete.setStatus(RoomStatus.AVAILABLE);

        roomRepository.save(roomToDelete);

        Room retrievedRoom = roomRepository.findById(roomIdToDelete).orElse(null);
        assertThat(retrievedRoom).isNotNull(); // Уверете се, че стаята съществува

        MvcResult result = mockMvc.perform(delete(RestApiRoutes.DELETE_ROOM, roomIdToDelete))
                .andExpect(status().isOk())
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        assertThat(responseBody).contains("Room is removed!");

        Room deletedRoom = roomRepository.findById(roomIdToDelete).orElse(null);
        assertThat(deletedRoom).isNull();
    }

    @Test
    void testBookRoom_RoomAlreadyBooked() throws Exception {
        // Create and save a room with a specific ID
        UUID roomId = UUID.fromString("41c0ced3-52c2-4455-b72d-b1ba63c93aab");
        Room existingRoom = new Room();
        existingRoom.setId(roomId);
        existingRoom.setPrice(BigDecimal.valueOf(100.00));
        existingRoom.setRoomFloor(2);
        existingRoom.setRoomNumber("102");
        existingRoom.setBathroomType(BathroomType.PRIVATE);
        existingRoom.setStatus(RoomStatus.AVAILABLE);
        roomRepository.save(existingRoom);

        // Create and save a user with a specific ID
        UUID userId = UUID.fromString("fd4cd7af-7261-49da-aab0-ec85ec81286f");
        User existingUser = new User();
        existingUser.setId(userId);
        existingUser.setFirstName("Test");
        existingUser.setLastName("User");
        existingUser.setEmail("testuser@example.com");
        existingUser.setUserPass("password123"); // Ensure to hash passwords in real applications
        existingUser.setBirthday(LocalDate.of(1990, 1, 1)); // Set the required 'birthday' field
        userRepository.save(existingUser);

        // Create a booking for this room and user
        Booking existingBooking = new Booking();
        existingBooking.setId(UUID.randomUUID());
        existingBooking.setRoom(existingRoom);
        existingBooking.setUser(existingUser);
        existingBooking.setStartDate(LocalDate.now().plusDays(1));
        existingBooking.setEndDate(LocalDate.now().plusDays(5));
        bookingRepository.save(existingBooking);

        // Attempt to book the same room in the same period
        BookRoomInput bookRoomInput = BookRoomInput.builder()
                .userId(userId.toString())
                .startDate(LocalDate.now().plusDays(1))
                .endDate(LocalDate.now().plusDays(5))
                .build();

        MvcResult result = mockMvc.perform(post(RestApiRoutes.BOOK_ROOM, roomId)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(bookRoomInput)))
                .andExpect(status().isConflict()) // Expecting status code 409 if the room is already booked
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        assertThat(responseBody).contains("Room is already booked"); // Check for the error message
    }
    @Test
    void testFindRoomById_InvalidIdFormat() throws Exception {
        String invalidRoomId = "invalid-uuid-format";

        mockMvc.perform(get(RestApiRoutes.FIND_ROOM, invalidRoomId))
                .andExpect(status().isBadRequest()) // Expecting 400 for invalid format
                .andExpect(result -> {
                    String responseBody = result.getResponse().getContentAsString();
                    assertThat(responseBody).contains("Invalid room ID format"); // Check for error message
                });
    }


}

