package com.tinqinacademy.hotel.api.operations.createuser;

import com.tinqinacademy.hotel.api.base.OperationInput;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateUserInput implements OperationInput {

    @NotNull(message = "First name must not be blank.")
    private String firstName;

    @NotNull(message = "Last name must not be blank.")
    private String lastName;

    @NotNull(message = "Password must not be blank.")
    private String userPass;

    @NotNull(message = "Email must not be blank.")
    @Email(message = "Invalid email format.")
    private String email;

    @PastOrPresent(message = "Birthday must be a past or present date.")
    private LocalDate birthday;

    @NotNull(message = "Phone number must not be blank.")
    private String phoneNumber;
}
