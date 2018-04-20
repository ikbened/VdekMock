package model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "Shipments")
public class ExtendedShipment {

    @Id
    public final String ShipmentId;
    public final String CustomerNumber;

    public ExtendedShipment() {
    }

    public ExtendedShipment(String shipmentId, String customerNumber) {
        ShipmentId = shipmentId;
        CustomerNumber = customerNumber;
    }

    /*
    {
  "CustomerNumber": "string",
  "EAN": "string",
  "EmailAddress": "string",
  "EmailUser": "string",
  "DisplayUserName": "string",
  "OrderId": "string",
  "OrderLine": "string",
  "SchoolId": "string",
  "SessionId": "string",
  "PostalCode": "string",
  "Label": "string",
  "FirstName": "string",
  "MiddleName": "string",
  "LastName": "string",
  "GroupName": "string",
  "ShipmentId": "string",
  "Type": "Order",
  "VendorId": "string",
  "Amount": 0,
  "StartDate": "2018-04-20T12:22:40.824Z",
  "SchoolType": "Unknown",
  "Address": "string",
  "AddressNumber": "string",
  "AddressAdjunct": "string",
  "City": "string",
  "Country": "string",
  "Administration": "string",
  "BirthDate": "2018-04-20T12:22:40.824Z",
  "Gender": "Male",
  "LedgerTag": "string"
}
     */
}
