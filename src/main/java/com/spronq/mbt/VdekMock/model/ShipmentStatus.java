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
public class ShipmentStatus {

    @Id
    private String statusId;
    private String shipmentId;
    private String processedByTask;
    private String errorMessage;

    public ShipmentStatus() {
        this.shipmentId = new java.util.UUID().randomUUID();
    }


}
