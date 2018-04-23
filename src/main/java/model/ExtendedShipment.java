package model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document(collection = "Shipments")
public class ExtendedShipment {

    @Id
    public final String shipmentId;
    public final String customerNumber;
    public final String ean;
    public final String emailAddress;
    public final String emailUser;
    public final String displayUserName;
    public final String orderId;
    public final String orderLine;
    public final String schoolId;
    public final String sessionId;
    public final String postalCode;
    public final String label;
    public final String firstName;
    public final String middleName;
    public final String lastName;
    public final String groupName;
    public final String type;
    public final String vendorId;
    public final Integer amount;
    public final Date startDate;
    public final String schoolType;
    public final String address;
    public final String addressNumber;
    public final String addressAdjunct;
    public final String city;
    public final String country;
    public final String administration;
    public final Date birthDate;
    public final String gender;
    public final String ledgerTag;

    public ExtendedShipment() {
    }

    public ExtendedShipment(String shipmentId, String customerNumber, String ean, String emailAddress, String emailUser,
            String displayUserName, String orderId, String orderLine, String schoolId, String sessionId, String postalCode,
            String label, String firstName, String middleName, String lastName, String groupName, String type, String vendorId,
            Integer amount, Date startDate, String schoolType, String address, String addressNumber, String addressAdjunct,
            String city, String country, String administration, Date birthDate, String gender, String ledgerTag) {
        this.shipmentId = shipmentId;
        this.customerNumber = customerNumber;
        this.ean = ean;
        this.emailAddress = emailAddress;
        this.emailUser = emailUser;
        this.displayUserName = displayUserName;
        this.orderId = orderId;
        this.orderLine = orderLine;
        this.schoolId = schoolId;
        this.sessionId = sessionId;
        this.postalCode = postalCode;
        this.label = label;
        this.firstName = firstName;
        this.middleName = middleName;
        this.lastName = lastName;
        this.groupName = groupName;
        this.type = type;
        this.vendorId = vendorId;
        this.amount = amount;
        this.startDate = startDate;
        this.schoolType = schoolType;
        this.address = address;
        this.addressNumber = addressNumber;
        this.addressAdjunct = addressAdjunct;
        this.city = city;
        this.country = country;
        this.administration = administration;
        this.birthDate = birthDate;
        this.gender = gender;
        this.ledgerTag = ledgerTag;
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
