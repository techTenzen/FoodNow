document.addEventListener('DOMContentLoaded', () => {
    const token = localStorage.getItem('foodnow_token');

    // Security Check: Redirect if not logged in or token is invalid
    if (!token) {
        redirectToLogin();
        return;
    }

    // Elements
    const logoutBtn = document.getElementById('logout-btn');
    const tableBody = document.getElementById('applications-table-body');
    const loadingRow = document.getElementById('loading-row');
    const toastContainer = document.getElementById('toast-container');
    
    const API_BASE_URL = 'http://localhost:8080/api';

    // ===============================================
    // Initial Animations & Data Fetching
    // ===============================================
    anime({
        targets: '#admin-content',
        translateY: [20, 0],
        opacity: [0, 1],
        duration: 800,
        easing: 'easeOutQuint',
    });

    const fetchApplications = async () => {
        try {
            const response = await fetch(`${API_BASE_URL}/admin/applications/pending`, {
                headers: { 'Authorization': `Bearer ${token}` }
            });

            if (!response.ok) {
                if(response.status === 403) {
                    showToast('Access Denied. Only admins can view this page.', 'error');
                    setTimeout(redirectToLogin, 2000);
                }
                throw new Error('Failed to fetch applications.');
            }
            
            const applications = await response.json();
            renderTable(applications);

        } catch (error) {
            console.error(error);
            loadingRow.innerHTML = `<td colspan="4" class="text-center px-6 py-12 text-red-400">Error loading data.</td>`;
        }
    };

    const renderTable = (applications) => {
        loadingRow.remove(); // Remove the loading indicator
        if (applications.length === 0) {
            tableBody.innerHTML = `<tr><td colspan="4" class="text-center px-6 py-12 text-text-muted">No pending applications found.</td></tr>`;
            return;
        }

        applications.forEach(app => {
            const row = document.createElement('tr');
            row.className = 'border-b border-border';
            row.innerHTML = `
                <td class="px-6 py-4 font-medium">${app.restaurantName}</td>
                <td class="px-6 py-4 text-text-muted">${app.applicant.name} (${app.applicant.email})</td>
                <td class="px-6 py-4 text-text-muted">${app.restaurantPhone}</td>
                <td class="px-6 py-4 text-right">
                    <button class="btn-success text-sm font-semibold py-1 px-3 rounded-md mr-2" onclick="handleApprove(${app.id})">Approve</button>
                    <button class="btn-danger text-sm font-semibold py-1 px-3 rounded-md" onclick="handleReject(${app.id})">Reject</button>
                </td>
            `;
            tableBody.appendChild(row);
        });
    };

    // ===============================================
    // Event Handlers for Approve/Reject
    // ===============================================
    window.handleApprove = async (applicationId) => {
        showToast('Approving...', 'loading');
        try {
            const response = await fetch(`${API_BASE_URL}/admin/applications/${applicationId}/approve`, {
                method: 'POST',
                headers: { 'Authorization': `Bearer ${token}` }
            });
            if (response.ok) {
                showToast('Application approved successfully!', 'success');
                tableBody.innerHTML = ''; // Clear table
                fetchApplications(); // Refresh the list
            } else {
                throw new Error('Approval failed.');
            }
        } catch (error) {
            showToast('An error occurred during approval.', 'error');
        }
    };

    window.handleReject = async (applicationId) => {
        const reason = prompt("Please provide a reason for rejection:");
        if (!reason) {
            showToast('Rejection cancelled.', 'loading');
            return;
        }

        showToast('Rejecting...', 'loading');
        try {
            const response = await fetch(`${API_BASE_URL}/admin/applications/${applicationId}/reject`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${token}`
                },
                body: JSON.stringify({ reason })
            });

            if (response.ok) {
                showToast('Application rejected.', 'success');
                tableBody.innerHTML = ''; // Clear table
                fetchApplications(); // Refresh the list
            } else {
                throw new Error('Rejection failed.');
            }
        } catch (error) {
            showToast('An error occurred during rejection.', 'error');
        }
    };
    
    // ===============================================
    // Utility Functions
    // ===============================================
    function redirectToLogin() {
        localStorage.removeItem('foodnow_token');
        const path = window.location.pathname.replace('admin-dashboard.html', 'index.html');
        window.location.href = window.location.origin + path;
    }
    
    logoutBtn.addEventListener('click', redirectToLogin);

    function showToast(message, type = 'success') {
        const toast = document.createElement('div');
        toast.textContent = message;
        toast.className = `toast toast-${type}`;
        toastContainer.appendChild(toast);
        setTimeout(() => toast.classList.add('show'), 100);
        setTimeout(() => {
            toast.classList.remove('show');
            toast.addEventListener('transitionend', () => toast.remove());
        }, 3000);
    }

    // Initial data load
    fetchApplications();
});
