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

@Document(collection = "shipments")
@Data
@AllArgsConstructor
@ToString
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ExtendedShipment {

    @Id
    private String shipmentId;
    private String customerNumber;
    private String ean;
    private String emailAddress;
    private String emailUser;
    private String displayUserName;
    private String orderId;
    private String orderLine;
    private String schoolId;
    private String sessionId;
    private String postalCode;
    private String label;
    private String firstName;
    private String middleName;
    private String lastName;
    private String groupName;
    private String type;
    private String vendorId;
    private Integer amount;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate startDate;
    private String schoolType;
    private String address;
    private String addressNumber;
    private String addressAdjunct;
    private String city;
    private String country;
    private String administration;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate birthDate;
    private String gender;
    private String ledgerTag;
    private String errorMessage;
    private Boolean processedByTask;

    public ExtendedShipment() {
        this.shipmentId = java.util.UUID.randomUUID().toString();
    }



}
