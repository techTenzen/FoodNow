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

<<<<<<< HEAD

Module 2: Restaurant Application & Management Tests
1. Customer Submits a Restaurant Application
Action: A logged-in CUSTOMER applies to open a restaurant.

Method: POST

URL: http://localhost:8080/api/applications/restaurant/apply

Headers: Authorization: Bearer <customer_token>

Body:

JSON

{
    "restaurantName": "The Test Kitchen",
    "restaurantAddress": "123 API Lane, Hyderabad",
    "restaurantPhone": "9988776655",
    "locationPin": "17.3850,78.4867"
}
Result: 200 OK with the application details and a "status": "PENDING".

2. Admin Approves an Application
Action: An ADMIN approves a pending application, creating a restaurant and upgrading the user's role.

Method: POST

URL: http://localhost:8080/api/admin/applications/{applicationId}/approve

Headers: Authorization: Bearer <admin_token>

Result: 200 OK with the details of the newly created Restaurant entity.

3. Admin Rejects an Application
Action: An ADMIN rejects a pending application.

Method: POST

URL: http://localhost:8080/api/admin/applications/{applicationId}/reject

Headers: Authorization: Bearer <admin_token>

Body:

JSON

{
    "reason": "Incomplete documentation provided."
}
Result: 200 OK with a success message.

4. Approved Owner Adds a Menu Item
Action: A newly approved RESTAURANT_OWNER adds an item to their menu.

Method: POST

URL: http://localhost:8080/api/restaurant/menu

Headers: Authorization: Bearer <new_owner_token> (This required logging in again as the user to get a token with the updated role).

Body:

JSON

{
    "name": "Chicken Tikka Masala",
    "description": "Grilled chicken chunks in a spiced curry sauce.",
    "price": 400.00
}
Result: 200 OK with a clean JSON response of the FoodItemDto.



Test 1: Get a List of All Restaurants
This test checks if the API can publicly show all available restaurants.

Open Postman.

Set the Method: GET

Enter the URL: http://localhost:8080/api/public/restaurants

Click "Send".

✅ Expected Result: You should get a 200 OK status. The response body will be a JSON array containing a list of all the restaurants in your system, formatted cleanly by your RestaurantDto.

Test 2: Get the Menu for a Specific Restaurant
This test checks if you can view the menu for a single restaurant.

Get a Restaurant ID: Look at the response from Test 1 and find the id of one of the restaurants (e.g., 2).

Set the Method: GET

Enter the URL: http://localhost:8080/api/public/restaurants/{restaurantId}/menu (replace {restaurantId} with the actual ID, for example: http://localhost:8080/api/public/restaurants/2/menu).

Click "Send".

✅ Expected Result: You should get a 200 OK status. The response body will be a single JSON object for that specific restaurant, including a menu array that lists all of its available food items.

oodNow: End-to-End Backend Test Plan
This guide covers the complete testing workflow for Modules 1, 2, and 3. Follow these steps in order using Postman.

Part 1: User Registration & Application
Objective: Create a new user and have them apply to become a restaurant owner.

Register a New Customer

Action: Create a new user who will become a restaurant owner.

Method: POST

URL: http://localhost:8080/api/auth/register

Body:

{
    "name": "Priya Sharma",
    "email": "priya.sharma@example.com",
    "password": "password123",
    "phoneNumber": "9988776655"
}

Expected Result: 200 OK

Log In as the New Customer

Action: Log in to get an authentication token for Priya.

Method: POST

URL: http://localhost:8080/api/auth/login

Body:

{
    "email": "priya.sharma@example.com",
    "password": "password123"
}

➡️ Action: Copy the accessToken from the response. Let's call this priya_customer_token.

Submit Restaurant Application

Action: Priya submits her application.

Method: POST

URL: http://localhost:8080/api/applications/restaurant/apply

Headers: Authorization: Bearer <priya_customer_token>

Body:

{
    "restaurantName": "Priya's Kitchen",
    "restaurantAddress": "123 Jubilee Hills, Hyderabad",
    "restaurantPhone": "1122334455",
    "locationPin": "17.4375, 78.4483"
}

Expected Result: 200 OK. The application is now pending.

Part 2: Admin Approval
Objective: As an admin, approve Priya's application.

Log In as Admin

Method: POST

URL: http://localhost:8080/api/auth/login

Body:

{
    "email": "admin@foodnow.com",
    "password": "admin123"
}

➡️ Action: Copy the accessToken. Let's call this admin_token.

View Pending Applications

Action: The admin checks for new applications.

Method: GET

URL: http://localhost:8080/api/admin/applications/pending

Headers: Authorization: Bearer <admin_token>

➡️ Action: Find the application for "Priya's Kitchen" in the response and note its id.

Approve the Application

Action: The admin approves Priya's application.

Method: POST

URL: http://localhost:8080/api/admin/applications/{applicationId}/approve (replace {applicationId} with the ID you noted).

Headers: Authorization: Bearer <admin_token>

Expected Result: 200 OK. Priya's role is now RESTAURANT_OWNER.

Part 3: Restaurant Owner Menu Management
Objective: Priya, now an owner, logs in and adds items to her menu.

Log In as Restaurant Owner

Action: Priya logs in again to get a new token with her updated role.

Method: POST

URL: http://localhost:8080/api/auth/login

Body:

{
    "email": "priya.sharma@example.com",
    "password": "password123"
}

➡️ Action: Copy the new accessToken. Let's call this priya_owner_token.

Add Menu Items

Action: Priya adds two items to her menu.

Method: POST

URL: http://localhost:8080/api/restaurant/menu

Headers: Authorization: Bearer <priya_owner_token>

Body (Item 1):

{
    "name": "Samosa Chaat",
    "description": "Crispy samosas topped with yogurt, chutney, and spices.",
    "price": 120.00
}

Body (Item 2): (Send a second request with this body)

{
    "name": "Mango Lassi",
    "description": "A refreshing yogurt-based mango smoothie.",
    "price": 90.00
}

Expected Result: 200 OK for both requests.

Part 4: Customer Ordering Workflow
Objective: A different customer finds Priya's restaurant and places an order.

Register a New Customer (for ordering)

Action: Create a new customer named Arjun.

Method: POST

URL: http://localhost:8080/api/auth/register

Body:

{
    "name": "Arjun Verma",
    "email": "arjun.v@example.com",
    "password": "password456",
    "phoneNumber": "8877665544"
}

Log In as Arjun

Method: POST

URL: http://localhost:8080/api/auth/login

Body:

{
    "email": "arjun.v@example.com",
    "password": "password456"
}

➡️ Action: Copy the accessToken. Let's call this arjun_token.

Browse Restaurants (Public)

Action: Arjun views all restaurants.

Method: GET

URL: http://localhost:8080/api/public/restaurants

➡️ Action: Find "Priya's Kitchen" and note the id of her "Samosa Chaat" from the menu in the response.

Add Item to Cart

Action: Arjun adds two Samosa Chaats to his cart.

Method: POST

URL: http://localhost:8080/api/cart/items

Headers: Authorization: Bearer <arjun_token>

Body: (Use the foodItemId for Samosa Chaat)

{
    "foodItemId": 1, 
    "quantity": 2
}

Expected Result: 200 OK with the cart details.

Place Order

Action: Arjun confirms his cart and places the order.

Method: POST

URL: http://localhost:8080/api/orders

Headers: Authorization: Bearer <arjun_token>

Expected Result: 200 OK with the new order details and a status of PENDING.

View Order History

Action: Arjun checks his past orders.

Method: GET

URL: http://localhost:8080/api/orders/my-orders

Headers: Authorization: Bearer <arjun_token>

Expected Result: 200 OK. The response will be an array containing the Samosa Chaat order he just placed.