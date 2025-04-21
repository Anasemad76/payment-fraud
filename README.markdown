# Transaction Service Configuration

## Overview
This project consists of multiple services, each configurable via its own `application.yml` file. You can set the `server.port` property in the `application.yml` file for each service to specify the port on which the service will run on your machine.

## Configuring Server Port
To configure the port for a service:
1. Locate the `application.yml` file in the service's resource directory (e.g., `src/main/resources/application.yml`).
2. Set the `server.port` property to the desired port number. For example:
   ```yaml
   server:
     port: 8081
   ```
3. Save the file and restart the service to apply the changes.

## Reader Service POST Request Example
The `reader-service` accepts POST requests to process transactions. Below is an example of how to send a transaction to the `reader-service`.

### Endpoint
```
POST http://localhost:8081/transactions
```

### Request Body
```json
{
  "transaction_id": "TXN202504170001",
  "account_number": "ACC12345678",
  "customer_id": "CUST98765",
  "amount": 250.75,
  "currency": "USD",
  "payment_method": "Credit Card",
  "payment_type": "Online Purchase",
  "transaction_date": "2025-04-17T14:35:00Z",
  "country": "Jordan",
  "city": "Amman",
  "merchant_name": "TechZone Electronics",
  "status": "Completed"
}
```

### Notes
- Ensure the `reader-service` is running on the specified port (e.g., `8081` as shown in the example).
- The JSON body must include all required fields as shown above.
- Verify the service's `application.yml` configuration to confirm the correct port if you encounter connection issues.