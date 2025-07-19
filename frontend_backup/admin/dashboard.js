document.addEventListener('DOMContentLoaded', () => {
    const token = localStorage.getItem('foodnow_token');
    if (!token) {
        window.location.href = '../index.html';
        return;
    }

    // --- Element References ---
    const mainContent = document.getElementById('main-content');
    const mainTitle = document.getElementById('main-title');
    const navLinks = document.querySelectorAll('.nav-link');

    // --- Initial State ---
    let currentSection = 'applications';

    // --- API Fetching Functions ---

    const fetchAndRender = async (section) => {
        mainContent.innerHTML = `<p class="text-text-muted">Loading...</p>`;
        anime({ targets: '#main-content', opacity: [0, 1], duration: 500 });

        try {
            switch (section) {
                case 'applications':
                    mainTitle.textContent = 'Pending Restaurant Applications';
                    await renderPendingApplications();
                    break;
                case 'restaurants':
                    mainTitle.textContent = 'All Restaurants';
                    await renderAllRestaurants();
                    break;
                case 'users':
                    mainTitle.textContent = 'All Users';
                    await renderAllUsers();
                    break;
                case 'orders':
                     mainTitle.textContent = 'All Orders';
                    await renderAllOrders();
                    break;
                case 'delivery':
                    mainTitle.textContent = 'Delivery Agents';
                    await renderDeliveryAgents();
                    break;
                case 'analytics':
                    mainTitle.textContent = 'Analytics';
                    await renderAnalytics();
                    break;
            }
        } catch (error) {
            mainContent.innerHTML = `<p class="text-red-400">Error loading data: ${error.message}</p>`;
            showToast(error.message, 'error');
        }
    };

    const apiFetch = async (endpoint) => {
        const response = await fetch(`${API_BASE_URL}${endpoint}`, {
            headers: { 'Authorization': `Bearer ${token}` }
        });
        if (!response.ok) {
            if (response.status === 403) {
                showToast('Access Denied. Redirecting to login.', 'error');
                setTimeout(() => window.location.href = '../index.html', 2000);
            }
            throw new Error(`Failed to fetch data from ${endpoint}`);
        }
        return response.json();
    };

    // --- Rendering Functions ---

    const renderTable = (data, headers, rowRenderer) => {
        if (data.length === 0) {
            mainContent.innerHTML = `<p class="text-text-muted">No data found.</p>`;
            return;
        }
        const table = createTable(headers);
        data.forEach(item => table.tBody.appendChild(rowRenderer(item)));
        mainContent.innerHTML = '';
        mainContent.appendChild(table.container);
    };

    const renderPendingApplications = async () => {
        const applications = await apiFetch('/admin/applications/pending');
        renderTable(applications, ['Restaurant Name', 'Applicant', 'Contact', 'Actions'], app => {
            const row = document.createElement('tr');
            row.className = 'border-b border-border';
            row.innerHTML = `
                <td class="px-6 py-4 font-medium">${app.restaurantName}</td>
                <td class="px-6 py-4 text-text-muted">${app.applicant.name} (${app.applicant.email})</td>
                <td class="px-6 py-4 text-text-muted">${app.phoneNumber}</td>
                <td class="px-6 py-4 text-right">
                    <button class="btn-success text-sm font-semibold py-1 px-3 rounded-md mr-2" data-id="${app.id}" data-action="approve">Approve</button>
                    <button class="btn-danger text-sm font-semibold py-1 px-3 rounded-md" data-id="${app.id}" data-action="reject">Reject</button>
                </td>
            `;
            return row;
        });
    };

    const renderAllRestaurants = async () => {
        const restaurants = await apiFetch('/admin/restaurants');
        renderTable(restaurants, ['ID', 'Name', 'Address', 'Owner'], restaurant => {
            const row = document.createElement('tr');
            row.className = 'border-b border-border';
            row.innerHTML = `
                <td class="px-6 py-4 font-medium">${restaurant.id}</td>
                <td class="px-6 py-4">${restaurant.name}</td>
                <td class="px-6 py-4 text-text-muted">${restaurant.address}</td>
                <td class="px-6 py-4 text-text-muted">${restaurant.ownerName}</td>
            `;
            return row;
        });
    };

    const renderAllUsers = async () => {
        const users = await apiFetch('/admin/users');
        renderTable(users, ['ID', 'Name', 'Email', 'Phone', 'Role'], user => {
            const row = document.createElement('tr');
            row.className = 'border-b border-border';
            row.innerHTML = `
                <td class="px-6 py-4 font-medium">${user.id}</td>
                <td class="px-6 py-4">${user.name}</td>
                <td class="px-6 py-4 text-text-muted">${user.email}</td>
                <td class="px-6 py-4 text-text-muted">${user.phoneNumber}</td>
                <td class="px-6 py-4 text-text-muted">${user.role}</td>
            `;
            return row;
        });
    };

    const renderAllOrders = async () => {
        const orders = await apiFetch('/admin/orders');
        renderTable(orders, ['Order ID', 'Customer', 'Restaurant', 'Total', 'Status', 'Time'], order => {
            const row = document.createElement('tr');
            row.className = 'border-b border-border';
            row.innerHTML = `
                <td class="px-6 py-4 font-medium">#${order.id}</td>
                <td class="px-6 py-4">${order.customerName}</td>
                <td class="px-6 py-4 text-text-muted">${order.restaurantName}</td>
                <td class="px-6 py-4 text-text-muted">₹${order.totalPrice.toFixed(2)}</td>
                <td class="px-6 py-4 text-text-muted">${order.status}</td>
                <td class="px-6 py-4 text-text-muted">${new Date(order.orderTime).toLocaleString()}</td>
            `;
            return row;
        });
    };

    const renderDeliveryAgents = async () => {
        const agents = await apiFetch('/admin/delivery-agents');
        renderTable(agents, ['ID', 'Name', 'Email', 'Phone'], agent => {
            const row = document.createElement('tr');
            row.className = 'border-b border-border';
            row.innerHTML = `
                <td class="px-6 py-4 font-medium">${agent.id}</td>
                <td class="px-6 py-4">${agent.name}</td>
                <td class="px-6 py-4 text-text-muted">${agent.email}</td>
                <td class="px-6 py-4 text-text-muted">${agent.phoneNumber}</td>
            `;
            return row;
        });
    };
    
    const renderAnalytics = async () => {
        const data = await apiFetch('/admin/analytics');
        mainContent.innerHTML = `
            <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
                <div class="bg-surface p-6 rounded-lg"><p class="text-sm text-text-muted">Total Users</p><p class="text-3xl font-bold">${data.totalUsers}</p></div>
                <div class="bg-surface p-6 rounded-lg"><p class="text-sm text-text-muted">Total Restaurants</p><p class="text-3xl font-bold">${data.totalRestaurants}</p></div>
                <div class="bg-surface p-6 rounded-lg"><p class="text-sm text-text-muted">Total Orders</p><p class="text-3xl font-bold">${data.totalOrders}</p></div>
                <div class="bg-surface p-6 rounded-lg"><p class="text-sm text-text-muted">Pending Orders</p><p class="text-3xl font-bold">${data.pendingOrders}</p></div>
                <div class="bg-surface p-6 rounded-lg"><p class="text-sm text-text-muted">Delivered Orders</p><p class="text-3xl font-bold">${data.deliveredOrders}</p></div>
            </div>
            <h3 class="text-2xl font-bold mt-12 mb-4">Orders per Restaurant</h3>
            <div class="bg-surface rounded-lg p-6">
                <ul>
                    ${data.ordersPerRestaurant.map(item => `<li class="flex justify-between py-2 border-b border-border"><span class="font-semibold">${item.restaurantName}</span><span>${item.orderCount} orders</span></li>`).join('')}
                </ul>
            </div>
        `;
    };

    function createTable(headers) {
        const container = document.createElement('div');
        container.className = 'bg-surface rounded-2xl shadow-xl border border-border overflow-hidden';
        const table = document.createElement('table');
        table.className = 'min-w-full text-left';
        const thead = table.createTHead();
        thead.className = 'border-b border-border';
        const tBody = table.createTBody();
        const headerRow = thead.insertRow();
        headers.forEach(headerText => {
            const th = document.createElement('th');
            th.className = 'px-6 py-4 text-sm font-semibold text-text-muted';
            if (headerText === 'Actions') th.classList.add('text-right');
            headerRow.appendChild(th);
            th.textContent = headerText;
        });
        container.appendChild(table);
        return { container, table, tBody };
    }

    document.getElementById('logout-btn').addEventListener('click', () => {
        localStorage.removeItem('foodnow_token');
        window.location.href = '../index.html';
    });

    navLinks.forEach(link => {
        link.addEventListener('click', (e) => {
            e.preventDefault();
            const section = e.target.dataset.section;
            if (section === currentSection) return;

            navLinks.forEach(l => l.classList.remove('active'));
            e.target.classList.add('active');
            
            currentSection = section;
            fetchAndRender(section);
        });
    });

    mainContent.addEventListener('click', async (event) => {
        const target = event.target;
        const action = target.dataset.action;
        const id = target.dataset.id;

        if (currentSection === 'applications' && (action === 'approve' || action === 'reject')) {
            let url = `${API_BASE_URL}/admin/applications/${id}/${action}`;
            let options = {
                method: 'POST',
                headers: { 'Authorization': `Bearer ${token}`, 'Content-Type': 'application/json' }
            };

            if (action === 'reject') {
                const reason = prompt("Please provide a reason for rejection:");
                if (!reason) return showToast('Rejection cancelled.', 'loading');
                options.body = JSON.stringify({ reason });
            }

            showToast(`${action.charAt(0).toUpperCase() + action.slice(1)}ing...`, 'loading');
            try {
                const response = await fetch(url, options);
                if (response.ok) {
                    showToast(`Application ${action}ed successfully!`, 'success');
                    fetchAndRender('applications');
                } else {
                    const error = await response.json();
                    throw new Error(error.message || `${action} failed.`);
                }
            } catch (error) {
                showToast(error.message, 'error');
            }
        }
    });

    // --- Initial Load ---
    fetchAndRender(currentSection);
});
