package com.spronq.mbt.VdekMock.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "userclaims")
@Data
@AllArgsConstructor
@ToString
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class UserClaim {

    @Id
    private String id;
    private String userId;
    private String claimType;
    private String claimValue;

    public UserClaim() {
        this.id = java.util.UUID.randomUUID().toString();
    }


}
