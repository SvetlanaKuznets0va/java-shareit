package ru.practicum.shareit.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserRequestDto {
    private int id;
    @NotEmpty
    @Email (message = "Email not valid")
    private String email;
    @NotEmpty
    private String name;
}
