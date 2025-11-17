package com.mud.mud.entity;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class UpdatePasswordAndEmailRequest {
    private String rawPassword;
    private String newPassword;
    private String newEmail;

}
