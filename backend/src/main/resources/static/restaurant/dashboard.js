document.addEventListener('DOMContentLoaded', () => {
    const token = localStorage.getItem('foodnow_token');
    if (!token) {
        window.location.href = '../index.html';
        return;
    }

    // --- State Management ---
    let restaurantData = {};
    let currentSection = 'overview';
    let orderCheckInterval;

    // --- Element References ---
    const mainContent = document.getElementById('main-content');
    const navContainer = document.getElementById('dashboard-nav');
    const logoutBtn = document.getElementById('logout-btn');
    const restaurantNameHeader = document.getElementById('restaurant-name-header');
    
    // Modal Elements
    const itemModal = document.getElementById('item-modal');
    const modalTitle = document.getElementById('modal-title');
    const itemForm = document.getElementById('item-form');
    const closeBtn = document.getElementById('close-modal-btn');

    // --- API Functions ---
    const apiFetch = async (endpoint, options = {}) => {
        options.headers = { ...options.headers, 'Authorization': `Bearer ${token}` };
        const response = await fetch(`${API_BASE_URL}${endpoint}`, options);
        if (!response.ok) {
            if(response.status === 403) setTimeout(() => window.location.href = '../index.html', 2000);
            throw new Error('API request failed.');
        }
        return response.status === 204 ? null : response.json();
    };

    const fetchDashboardData = async () => {
        try {
            restaurantData = await apiFetch('/restaurant/dashboard');
            restaurantNameHeader.textContent = restaurantData.restaurantProfile.name;
            renderContent(currentSection);
            startOrderCheck();
        } catch (error) {
            showToast('Could not load dashboard data.', 'error');
        }
    };

    // --- Rendering Functions ---
    const renderContent = (section) => {
        mainContent.innerHTML = '';
        anime({ targets: '#main-content', opacity: [0, 1], translateY: [10, 0], duration: 400, easing: 'easeOutQuad' });
        switch (section) {
            case 'overview': renderOverview(); break;
            case 'orders': renderOrderManagement(); break;
            case 'menu': renderMenuManagement(); break;
            case 'reviews': renderReviews(); break;
        }
    };

    const renderOverview = () => {
        const pendingOrders = restaurantData.orders.filter(o => o.status === 'PENDING').length;
        const totalRevenue = restaurantData.orders
            .filter(o => o.status === 'DELIVERED')
            .reduce((sum, o) => sum + o.totalPrice, 0);
        
        mainContent.innerHTML = `
            <div class="grid grid-cols-1 md:grid-cols-3 gap-6">
                <div class="bg-surface p-6 rounded-lg"><p class="text-sm text-text-muted">Pending Orders</p><p class="text-4xl font-bold">${pendingOrders}</p></div>
                <div class="bg-surface p-6 rounded-lg"><p class="text-sm text-text-muted">Total Menu Items</p><p class="text-4xl font-bold">${restaurantData.menu.length}</p></div>
                <div class="bg-surface p-6 rounded-lg"><p class="text-sm text-text-muted">Total Revenue (Delivered)</p><p class="text-4xl font-bold">₹${totalRevenue.toFixed(2)}</p></div>
            </div>
        `;
    };

    const renderOrderManagement = () => {
        const statuses = ['PENDING', 'PREPARING', 'OUT_FOR_DELIVERY', 'DELIVERED'];
        mainContent.innerHTML = `
            <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
                ${statuses.map(status => `
                    <div id="orders-${status}">
                        <h3 class="text-lg font-semibold mb-4 capitalize">${status.replace('_', ' ').toLowerCase()}</h3>
                        <div class="space-y-4"></div>
                    </div>
                `).join('')}
            </div>
        `;
        restaurantData.orders.forEach(order => {
            const container = document.querySelector(`#orders-${order.status} > div`);
            if (container) {
                const orderCard = document.createElement('div');
                orderCard.className = 'bg-surface p-4 rounded-lg';
                // THIS IS THE FIX: Access 'itemName' directly from the item DTO.
                const itemsHtml = order.items.map(item => `<li>${item.quantity} x ${item.itemName}</li>`).join('');
                orderCard.innerHTML = `
                    <p class="font-bold">Order #${order.id} (Customer: ${order.customerName})</p>
                    <ul class="text-sm text-text-muted list-disc list-inside my-2">${itemsHtml}</ul>
                    <p class="font-semibold">Total: ₹${order.totalPrice.toFixed(2)}</p>
                    <div class="mt-4 flex gap-2">
                        ${order.status === 'PENDING' ? `
                            <button class="btn-success text-sm font-semibold py-1 px-2 rounded-md flex-1" data-id="${order.id}" data-action="CONFIRM">Accept</button>
                            <button class="btn-danger text-sm font-semibold py-1 px-2 rounded-md flex-1" data-id="${order.id}" data-action="CANCEL">Reject</button>
                        ` : ''}
                        ${order.status === 'PREPARING' ? `<button class="btn-primary text-sm font-semibold py-1 px-2 rounded-md w-full" data-id="${order.id}" data-action="READY">Ready for Pickup</button>` : ''}
                    </div>
                `;
                container.appendChild(orderCard);
            }
        });
    };

    const renderMenuManagement = () => {
        mainContent.innerHTML = `
            <div class="flex justify-between items-center mb-6">
                <h3 class="text-2xl font-bold">Your Menu</h3>
                <button id="add-item-btn" class="btn-primary px-4 py-2 rounded-lg font-semibold">Add New Item</button>
            </div>
            <div class="bg-surface rounded-lg border border-border">
                <table class="min-w-full">
                    <thead class="border-b border-border">
                        <tr>
                            <th class="px-6 py-3 text-left text-xs font-medium text-text-muted uppercase tracking-wider">Name</th>
                            <th class="px-6 py-3 text-left text-xs font-medium text-text-muted uppercase tracking-wider">Price</th>
                            <th class="px-6 py-3 text-left text-xs font-medium text-text-muted uppercase tracking-wider">Available</th>
                            <th class="px-6 py-3 text-right text-xs font-medium text-text-muted uppercase tracking-wider">Actions</th>
                        </tr>
                    </thead>
                    <tbody id="menu-table-body"></tbody>
                </table>
            </div>
        `;
        const menuTableBody = document.getElementById('menu-table-body');
        restaurantData.menu.forEach(item => {
            const row = document.createElement('tr');
            row.className = 'border-b border-border';
            row.innerHTML = `
                <td class="px-6 py-4 whitespace-nowrap"><div class="font-medium">${item.name}</div><div class="text-sm text-text-muted">${item.description}</div></td>
                <td class="px-6 py-4 whitespace-nowrap">₹${item.price.toFixed(2)}</td>
                <td class="px-6 py-4 whitespace-nowrap">
                    <label class="switch">
                        <input type="checkbox" data-id="${item.id}" class="availability-toggle" ${item.available ? 'checked' : ''}>
                        <span class="slider"></span>
                    </label>
                </td>
                <td class="px-6 py-4 whitespace-nowrap text-right text-sm font-medium">
                    <button class="text-indigo-400 hover:text-indigo-300 mr-4" data-id="${item.id}" data-action="edit">Edit</button>
                    <button class="text-red-500 hover:text-red-400" data-id="${item.id}" data-action="delete">Delete</button>
                </td>
            `;
            menuTableBody.appendChild(row);
        });
    };
    
    const renderReviews = () => {
        mainContent.innerHTML = `<p class="text-text-muted">Customer reviews feature is coming soon.</p>`;
    };

    const openItemModal = (item = null) => {
        itemForm.reset();
        if (item) {
            modalTitle.textContent = 'Edit Item';
            document.getElementById('item-id').value = item.id;
            document.getElementById('item-name').value = item.name;
            document.getElementById('item-description').value = item.description;
            document.getElementById('item-price').value = item.price;
        } else {
            modalTitle.textContent = 'Add New Item';
            document.getElementById('item-id').value = '';
        }
        itemModal.classList.remove('hidden');
        itemModal.classList.add('flex');
    };

    const closeItemModal = () => itemModal.classList.add('hidden');
    
    itemForm.addEventListener('submit', async (e) => {
        e.preventDefault();
        const formData = new FormData(itemForm);
        const itemData = Object.fromEntries(formData.entries());
        const itemId = itemData.id;
        
        try {
            if (itemId) {
                await apiFetch(`/restaurant/menu/${itemId}`, { method: 'PUT', headers: {'Content-Type': 'application/json'}, body: JSON.stringify(itemData) });
                showToast('Item updated successfully!', 'success');
            } else {
                await apiFetch('/restaurant/menu', { method: 'POST', headers: {'Content-Type': 'application/json'}, body: JSON.stringify(itemData) });
                showToast('Item added successfully!', 'success');
            }
            closeItemModal();
            fetchDashboardData();
        } catch (error) {
            showToast('Failed to save item.', 'error');
        }
    });

    const startOrderCheck = () => {
        if (orderCheckInterval) clearInterval(orderCheckInterval);
        orderCheckInterval = setInterval(async () => {
            try {
                const newData = await apiFetch('/restaurant/dashboard');
                const oldPendingCount = restaurantData.orders.filter(o => o.status === 'PENDING').length;
                const newPendingCount = newData.orders.filter(o => o.status === 'PENDING').length;
                
                if (newPendingCount > oldPendingCount) {
                    document.getElementById('notification-sound').play();
                    showToast(`You have ${newPendingCount - oldPendingCount} new order(s)!`, 'success');
                }
                restaurantData = newData;
                if (currentSection === 'orders' || currentSection === 'overview') {
                    renderContent(currentSection);
                }
            } catch (error) {
                console.error("Error checking for new orders:", error);
            }
        }, 15000);
    };

    navContainer.addEventListener('click', (e) => {
        if (e.target.tagName === 'A') {
            e.preventDefault();
            currentSection = e.target.dataset.section;
            navContainer.querySelector('.active').classList.remove('active');
            e.target.classList.add('active');
            renderContent(currentSection);
        }
    });
    
    mainContent.addEventListener('click', async (e) => {
        const action = e.target.dataset.action;
        const id = e.target.dataset.id;

        if (e.target.id === 'add-item-btn') {
            openItemModal();
            return;
        }

        if (!action || !id) return;

        if (action === 'edit') {
            const item = restaurantData.menu.find(i => i.id == id);
            openItemModal(item);
        } else if (action === 'delete') {
            if (confirm('Are you sure you want to delete this item?')) {
                await apiFetch(`/restaurant/menu/${id}`, { method: 'DELETE' });
                showToast('Item deleted.', 'success');
                fetchDashboardData();
            }
        } else if (e.target.classList.contains('availability-toggle')) {
            await apiFetch(`/restaurant/menu/${id}/availability`, { method: 'PATCH' });
            showToast('Availability updated.', 'success');
        } else if (['CONFIRM', 'CANCEL', 'READY'].includes(action)) {
            const newStatus = action === 'READY' ? 'OUT_FOR_DELIVERY' : action === 'CONFIRM' ? 'PREPARING' : 'CANCELLED';
            await apiFetch(`/manage/orders/${id}/status`, { method: 'PATCH', headers: {'Content-Type': 'application/json'}, body: JSON.stringify({status: newStatus}) });
            showToast('Order status updated.', 'success');
            fetchDashboardData();
        }
    });
    
    logoutBtn.addEventListener('click', () => {
        clearInterval(orderCheckInterval);
        localStorage.removeItem('foodnow_token');
        window.location.href = '../index.html';
    });
    
    closeBtn.addEventListener('click', closeItemModal);
    
    fetchDashboardData();
});
