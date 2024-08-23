import com.fasterxml.jackson.databind.ObjectMapper;
import com.tinqinacademy.hotel.api.errors.Error;
import com.tinqinacademy.hotel.api.errors.Errors;
import com.tinqinacademy.hotel.api.operations.visitorregistration.input.VisitorRegistrationInput;
import com.tinqinacademy.hotel.api.operations.visitorregistration.output.VisitorRegistrationOutput;
import com.tinqinacademy.hotel.core.operations.VisitorRegistrationOperationProcessor;
import com.tinqinacademy.hotel.rest.HotelApplication;
import io.vavr.control.Either;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

@SpringBootTest(classes = HotelApplication.class)
@AutoConfigureMockMvc
class VisitorRegistrationControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private VisitorRegistrationOperationProcessor visitorRegistrationOperationProcessor;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testRegisterVisitor_Success() throws Exception {
        VisitorRegistrationInput input = VisitorRegistrationInput.builder()
                .roomId("587ce90e-f191-4860-9d90-609652d43db2")
                .startDate(LocalDate.of(2024, 8, 1))
                .endDate(LocalDate.of(2024, 8, 10))
                .firstName("John")
                .lastName("Doe")
                .phoneNo("1234567890")
                .idCardNo("ID123456")
                .idCardValidity(LocalDate.of(2025, 1, 1))
                .idCardIssueAuthority("Authority")
                .idCardIssueDate(LocalDate.of(2023, 1, 1))
                .build();

        VisitorRegistrationOutput output = new VisitorRegistrationOutput("guestId123");
        when(visitorRegistrationOperationProcessor.process(any(VisitorRegistrationInput.class)))
                .thenReturn(Either.right(output));

        mockMvc.perform(post("/api/v1/hotel/registerVisitor")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"guestId\":\"guestId123\"}"));
    }

    @Test
    void testRegisterVisitor_InvalidInput() throws Exception {
        VisitorRegistrationInput input = VisitorRegistrationInput.builder()
                .roomId("587ce90e-f191-4860-9d90-609652d43db2")
                .startDate(null)
                .endDate(LocalDate.of(2024, 8, 10))
                .firstName("John")
                .lastName("Doe")
                .phoneNo("1234567890")
                .idCardNo("ID123456")
                .idCardValidity(LocalDate.of(2025, 1, 1))
                .idCardIssueAuthority("Authority")
                .idCardIssueDate(LocalDate.of(2023, 1, 1))
                .build();

        Errors errors = new Errors(List.of(new Error("Invalid data provided")));
        when(visitorRegistrationOperationProcessor.process(any(VisitorRegistrationInput.class)))
                .thenReturn(Either.left(errors));

        mockMvc.perform(post("/api/v1/system/register")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isBadRequest())
                .andExpect(content().json("{\"message\":\"Invalid data provided\"}"));
    }
}
