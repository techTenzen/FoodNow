document.addEventListener('DOMContentLoaded', () => {
    const token = localStorage.getItem('foodnow_token');
    
    // FIX: Redirect to the full path of index.html if not logged in
    if (!token) {
        const path = window.location.pathname.replace('customer-dashboard.html', 'index.html');
        window.location.href = window.location.origin + path;
        return;
    }

    // Elements
    const logoutBtn = document.getElementById('logout-btn');
    const openModalBtn = document.getElementById('open-modal-btn');
    const closeModalBtn = document.getElementById('close-modal-btn');
    const modalBackdrop = document.getElementById('modal-backdrop');
    const modalContent = document.getElementById('modal-content');
    const applicationForm = document.getElementById('application-form');
    const toastContainer = document.getElementById('toast-container');
    
    const API_BASE_URL = 'http://localhost:8080/api';

    // Animations
    anime({
        targets: '#dashboard-content',
        translateY: [20, 0],
        opacity: [0, 1],
        duration: 800,
        easing: 'easeOutQuint',
        delay: 200
    });

    const openModal = () => {
        modalBackdrop.classList.remove('pointer-events-none');
        anime({ targets: modalBackdrop, opacity: 1, duration: 300, easing: 'easeOutQuad' });
        anime({ targets: modalContent, opacity: 1, scale: 1, duration: 300, easing: 'easeOutQuad' });
    };

    const closeModal = () => {
        modalBackdrop.classList.add('pointer-events-none');
        anime({ targets: modalBackdrop, opacity: 0, duration: 300, easing: 'easeInQuad' });
        anime({ targets: modalContent, opacity: 0, scale: 0.95, duration: 300, easing: 'easeInQuad' });
    };

    openModalBtn.addEventListener('click', openModal);
    closeModalBtn.addEventListener('click', closeModal);
    modalBackdrop.addEventListener('click', (e) => {
        if (e.target === modalBackdrop) closeModal();
    });

    // Event Listeners & API Calls
    logoutBtn.addEventListener('click', () => {
        localStorage.removeItem('foodnow_token');
        showToast('Logged out successfully.', 'success');
        setTimeout(() => {
            // FIX: Redirect to the full path of index.html
            const path = window.location.pathname.replace('customer-dashboard.html', 'index.html');
            window.location.href = window.location.origin + path;
        }, 1500);
    });

    applicationForm.addEventListener('submit', async (event) => {
        event.preventDefault();
        const formData = new FormData(applicationForm);
        const data = Object.fromEntries(formData.entries());
        showToast('Submitting application...', 'loading');

        try {
            const response = await fetch(`${API_BASE_URL}/applications/restaurant/apply`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${token}`
                },
                body: JSON.stringify(data)
            });

            if (response.ok) {
                showToast('Application submitted successfully!', 'success');
                applicationForm.reset();
                setTimeout(closeModal, 1500);
            } else {
                const error = await response.json();
                showToast(error.message || 'Submission failed.', 'error');
            }
        } catch (error) {
            showToast('An error occurred. Please try again.', 'error');
        }
    });

    // Reusable Toast Function
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
});
