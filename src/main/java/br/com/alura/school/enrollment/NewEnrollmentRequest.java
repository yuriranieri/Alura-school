package br.com.alura.school.enrollment;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

class NewEnrollmentRequest {
    
    @Size(max=20)
    @NotBlank(message = "campo username é obrigatório")
    @JsonProperty("username")
    private final String username;

    @JsonCreator
    public NewEnrollmentRequest(@JsonProperty("username") String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }
}
