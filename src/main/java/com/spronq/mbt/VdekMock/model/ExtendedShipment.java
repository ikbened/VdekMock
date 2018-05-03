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
@NoArgsConstructor
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

    public ExtendedShipment() {
        this.shipmentId = new java.util.UUID().randomUUID();
    }

    public String getErrorMessage() {
        return ErrorMessage;
    }

    public Integer getProcessedByTask() {
        return ProcessedByTask;
    }

    public void setErrorMessage(String errorMessage) {
        ErrorMessage = errorMessage;
    }

    public void setProcessedByTask(Integer processedByTask) {
        ProcessedByTask = processedByTask;
    }

    public String getShipmentId() {
        return shipmentId;
    }

    public String getCustomerNumber() {
        return customerNumber;
    }

    public String getEan() {
        return ean;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public String getEmailUser() {
        return emailUser;
    }

    public String getOrderId() {
        return orderId;
    }

    public String getSchoolId() {
        return schoolId;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public String getLabel() {
        return label;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getAdministration() {
        return administration;
    }

}
