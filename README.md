# FoodNow

Module 1 Complete

🧪 Test Scenarios
Success Scenariohttps://github.com/techTenzen/FoodNow/pull/4s
1. Successful User Registration

Action: Create a new user with unique details.

Request:

Method: POST

URL: http://localhost:8080/api/auth/register

Body (raw, JSON):

JSON

{
    "name": "Alice",
    "email": "alice@example.com",
    "password": "password123",
    "phoneNumber": "1112223330"
}
Expected Response:

Status: 200 OK

Body: User registered successfully!

2. Successful Login

Action: Log in with the user you just created.

Request:

Method: POST

URL: http://localhost:8080/api/auth/login

Body (raw, JSON):

JSON

{
    "email": "alice@example.com",
    "password": "password123"
}
Expected Response:

Status: 200 OK

Body: A JSON object containing the JWT token.

JSON

{
    "accessToken": "ey...",
    "tokenType": "Bearer"
}
Failure Scenarios (Testing your Exception Handler)
3. Registration with a Duplicate Email

Action: Try to register a new user with the same email as before.

Request:

Method: POST

URL: http://localhost:8080/api/auth/register

Body:

JSON

{
    "name": "Bob",
    "email": "alice@example.com", // DUPLICATE
    "password": "password456",
    "phoneNumber": "4445556667"
}
Expected Response (from handleDataIntegrityViolation):

Status: 409 Conflict

Body:

JSON

{
    "timestamp": "...",
    "status": 409,
    "error": "Conflict",
    "message": "Email address already exists"
}
4. Login with Incorrect Password

Action: Use the correct email but the wrong password.

Request:

Method: POST

URL: http://localhost:8080/api/auth/login

Body:

JSON

{
    "email": "alice@example.com",
    "password": "wrongpassword" // INCORRECT
}
Expected Response (from handleBadCredentials):

Status: 401 Unauthorized

Body:

JSON

{
    "timestamp": "...",
    "status": 401,
    "error": "Unauthorized",
    "message": "Invalid email or password"
}
5. Login with a Non-Existent User

Action: Try to log in with an email that is not registered.

Request:

Method: POST

URL: http://localhost:8080/api/auth/login

Body:

JSON

{
    "email": "notauser@example.com", // DOES NOT EXIST
    "password": "password123"
}
Expected Response (from handleUserNotFound):

Status: 401 Unauthorized

Body:

JSON

{
    "timestamp": "...",
    "status": 401,
    "error": "Unauthorized",
    "message": "Invalid credentials"
}


✅ How to Test Role-Based Access
Here’s how you can test that the new security rule is working correctly.

Test 1: Access Denied for a Regular User
Register a new user (or use "alice@example.com" if she's still in your database). This user will have the CUSTOMER role.

Log in as the CUSTOMER user (alice@example.com) to get a JWT token.

Try to access the admin endpoint with that token.

Request:

Method: GET

URL: http://localhost:8080/api/admin/dashboard

Headers:

Authorization: Bearer <token_from_customer_login>

Expected Response:

Status: 403 Forbidden

This proves that users without the ADMIN role cannot access the endpoint.

Test 2: Access Granted for the Admin User
Log in as the default admin user.

Method: POST

URL: http://localhost:8080/api/auth/login

Body:

JSON

{
    "email": "admin@foodnow.com",
    "password": "admin123"
}
You will receive a new JWT token for the admin.

Access the admin endpoint using the admin's token.

Request:

Method: GET

URL: http://localhost:8080/api/admin/dashboard

Headers:

Authorization: Bearer <token_from_admin_login>

Expected Response:

Status: 200 OK

Body:

JSON

{
    "message": "Welcome to the Admin Dashboard!"
}

