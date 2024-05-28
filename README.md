# Train Ticket Booking System API

## API Overview

This service includes a set of APIs to:

1. **Purchase Ticket (POST)**
2. **Get Receipt/Ticket (GET)**
3. **Get Tickets Booked by Section (GET)**
4. **Remove Ticket (DELETE)**
5. **Modify Ticket (PUT)**

## Validations

Validations added in API include:

- **On Mandatory Fields**: Validates the presence of mandatory fields:
  - User: `{firstName, email}`
  - Ticket: `{from, to, date}`
- **Valid Email ID**: Ensures that the email provided is valid. (Note: Standard validation annotations are not used.)
- **On Ticket Booking**: Checks if a ticket already exists for the same user on the specified date for the given route.
- **Section and Seat Validations**: Ensures the section is either `{A, B}` and validates the seat numbers.

## JUnit Tests

### TicketServiceImplTest.java

- **Case 1: `purchaseTicket_SuccessfulPurchase_ReturnsAcceptedResponse`**
  - Tests that a ticket purchase is successful and returns an accepted response.
- **Case 2: `purchaseTicket_ValidationFails_ReturnsAccepted`**
  - Tests that the purchase fails validations and still returns an accepted response for failed validation scenarios.
- **Case 3: `findTicketByUserEmail_TicketExists_ReturnsTicket`**
  - Tests retrieving a ticket by user email when it exists.
- **Case 4: `modifyUserSeat_SuccessfulModification_ReturnsAcceptedResponse`**
  - Tests that modifying a seat on an existing ticket is successful and returns an accepted response.

### TicketValidatorTest.java

- **Case 1: `testCheckRequiredFields_MissingFields_AddsAppropriateErrors`**
  - Tests that missing required fields are detected and appropriate errors are added.
- **Case 2: `testValidateTicket_TicketExists_ReturnsFalse`**
  - Tests that the validation for an existing ticket returns false.
- **Case 3: `testValidateTicket_TicketDoesNotExist_ReturnsTrue`**
  - Tests that the validation for a non-existing ticket returns true.
- **Case 4: `testValidateSeat_ValidSectionAndSeatNo_NoErrorsAdded`**
  - Tests that a valid section and seat number pass without any errors.
- **Case 5: `testValidateSeat_InvalidSection_ErrorsAdded`**
  - Tests that an invalid section adds appropriate errors.
- **Case 6: `testValidateSeat_InvalidSeatNo_ErrorsAdded`**
  - Tests that an invalid seat number adds appropriate errors.
