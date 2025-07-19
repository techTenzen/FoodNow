document.addEventListener('DOMContentLoaded', () => {
    const token = localStorage.getItem('foodnow_token');
    if (!token) window.location.href = '../index.html';
    
    const params = new URLSearchParams(window.location.search);
    const restaurantId = params.get('id');
    if (!restaurantId) window.location.href = 'dashboard.html';

    const restaurantNameEl = document.getElementById('restaurant-name');
    const restaurantAddressEl = document.getElementById('restaurant-address');
    const menuContainer = document.getElementById('menu-container');

    const fetchMenu = async () => {
        try {
            const response = await fetch(`${API_BASE_URL}/public/restaurants/${restaurantId}/menu`);
            if (!response.ok) throw new Error('Restaurant not found.');
            const restaurant = await response.json();
            renderMenu(restaurant);
        } catch (error) {
            showToast(error.message, 'error');
        }
    };

    const renderMenu = (restaurant) => {
        restaurantNameEl.textContent = restaurant.name;
        restaurantAddressEl.textContent = restaurant.address;
        menuContainer.innerHTML = '';
        if (!restaurant.menu || restaurant.menu.length === 0) {
            menuContainer.innerHTML = '<p class="text-text-muted col-span-full">This restaurant has no items on its menu yet.</p>';
            return;
        }
        restaurant.menu.forEach(item => {
            const itemEl = document.createElement('div');
            itemEl.className = 'bg-surface p-4 rounded-lg flex flex-col justify-between';
            itemEl.innerHTML = `
                <div>
                    <h4 class="text-lg font-bold">${item.name}</h4>
                    <p class="text-sm text-text-muted mt-1">${item.description}</p>
                    <p class="text-lg font-semibold mt-2 text-primary">₹${item.price.toFixed(2)}</p>
                </div>
                <div class="mt-4 flex items-center gap-2">
                    <input type="number" value="1" min="1" class="form-input w-20 text-center quantity-input">
                    <button data-item-id="${item.id}" class="add-to-cart-btn btn-primary flex-grow py-2 px-4 rounded-lg font-semibold">Add to Cart</button>
                </div>
            `;
            menuContainer.appendChild(itemEl);
        });
    };

    menuContainer.addEventListener('click', async (e) => {
        if (e.target.classList.contains('add-to-cart-btn')) {
            const foodItemId = e.target.dataset.itemId;
            const quantityInput = e.target.previousElementSibling;
            const quantity = parseInt(quantityInput.value, 10);

            if (isNaN(quantity) || quantity < 1) {
                showToast('Please enter a valid quantity.', 'error');
                return;
            }

            try {
                const response = await fetch(`${API_BASE_URL}/cart/items`, {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json',
                        'Authorization': `Bearer ${token}`
                    },
                    body: JSON.stringify({ foodItemId: parseInt(foodItemId), quantity })
                });
                if (!response.ok) throw new Error('Could not add item to cart.');
                showToast(`${quantity} x ${e.target.closest('.flex').previousElementSibling.querySelector('h4').textContent} added to cart!`, 'success');
            } catch (error) {
                showToast(error.message, 'error');
            }
        }
    });

    fetchMenu();
});
