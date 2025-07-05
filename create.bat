@echo off
setlocal

:: Define the base directory where all foodnow content will be created
set BASE_DIR=backend\src\main\java\com\foodnow

echo Creating directories and files for FoodNow backend...

:: Create the base foodnow directory (and its parents if they don't exist)
:: If foodnow already exists, this command will not cause an error.
mkdir %BASE_DIR% >nul 2>&1

:: Create the main application file directly inside the foodnow directory
echo. > %BASE_DIR%\FoodNowApplication.java

:: Create controller directory and files
mkdir %BASE_DIR%\controller >nul 2>&1
echo. > %BASE_DIR%\controller\AuthController.java
echo. > %BASE_DIR%\controller\UserController.java
echo. > %BASE_DIR%\controller\RestaurantController.java
echo. > %BASE_DIR%\controller\OrderController.java
echo. > %BASE_DIR%\controller\PaymentController.java
echo. > %BASE_DIR%\controller\DeliveryController.java

:: Create model directory and files
mkdir %BASE_DIR%\model >nul 2>&1
echo. > %BASE_DIR%\model\User.java
echo. > %BASE_DIR%\model\Role.java
echo. > %BASE_DIR%\model\ERole.java
echo. > %BASE_DIR%\model\Address.java
echo. > %BASE_DIR%\model\Restaurant.java
echo. > %BASE_DIR%\model\MenuItem.java
echo. > %BASE_DIR%\model\Order.java
echo. > %BASE_DIR%\model\OrderItem.java
echo. > %BASE_DIR%\model\Payment.java
echo. > %BASE_DIR%\model\DeliveryAgent.java
echo. > %BASE_DIR%\model\Delivery.java

:: Create repository directory and files
mkdir %BASE_DIR%\repository >nul 2>&1
echo. > %BASE_DIR%\repository\UserRepository.java
echo. > %BASE_DIR%\repository\AddressRepository.java
echo. > %BASE_DIR%\repository\RestaurantRepository.java
echo. > %BASE_DIR%\repository\MenuItemRepository.java
echo. > %BASE_DIR%\repository\OrderRepository.java
echo. > %BASE_DIR%\repository\OrderItemRepository.java
echo. > %BASE_DIR%\repository\PaymentRepository.java
echo. > %BASE_DIR%\repository\DeliveryAgentRepository.java

:: Create service directory and files
mkdir %BASE_DIR%\service >nul 2>&1
echo. > %BASE_DIR%\service\AuthService.java
echo. > %BASE_DIR%\service\UserService.java
echo. > %BASE_DIR%\service\RestaurantService.java
echo. > %BASE_DIR%\service\OrderService.java
echo. > %BASE_DIR%\service\PaymentService.java
echo. > %BASE_DIR%\service\DeliveryService.java

:: Create config directory and files
mkdir %BASE_DIR%\config >nul 2>&1
echo. > %BASE_DIR%\config\SecurityConfig.java
echo. > %BASE_DIR%\config\JwtAuthenticationFilter.java
echo. > %BASE_DIR%\config\JwtUtils.java
echo. > %BASE_DIR%\config\WebConfig.java

:: Create exception directory and files
mkdir %BASE_DIR%\exception >nul 2>&1
echo. > %BASE_DIR%\exception\GlobalExceptionHandler.java
echo. > %BASE_DIR%\exception\CustomException.java
echo. > %BASE_DIR%\exception\ResourceNotFoundException.java

echo All directories and empty Java files have been created.
endlocal
