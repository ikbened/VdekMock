package com.spronq.mbt.VdekMock.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;

@Document(collection = "users")
@Data
@AllArgsConstructor
@ToString
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class User {

    @Id
    private String id;
    private String label;
    private String email;
    //private String customerNumber;
    //private String accountSetId;
    private String postalCode;

    public User() {
        this.id = java.util.UUID.randomUUID().toString();
    }


}
