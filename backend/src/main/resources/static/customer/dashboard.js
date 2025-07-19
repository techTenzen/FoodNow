document.addEventListener('DOMContentLoaded', () => {
    const token = localStorage.getItem('foodnow_token');
    if (!token) {
        window.location.href = '../index.html';
        return;
    }

    let allRestaurants = []; // To store the original list for filtering

    const restaurantsContainer = document.getElementById('restaurants-container');
    const searchBar = document.getElementById('search-bar');
    const sortFilter = document.getElementById('sort-filter');
    const logoutBtn = document.getElementById('logout-btn');
    const openModalBtn = document.getElementById('apply-restaurant-link');
    const closeModalBtn = document.getElementById('close-modal-btn');
    const modalBackdrop = document.getElementById('modal-backdrop');
    const applicationForm = document.getElementById('application-form');

    const fetchRestaurants = async () => {
        try {
            const response = await fetch(`${API_BASE_URL}/public/restaurants`);
            if (!response.ok) throw new Error('Could not fetch restaurants.');
            allRestaurants = await response.json();
            renderRestaurants(allRestaurants);
        } catch (error) {
            showToast(error.message, 'error');
        }
    };

    const renderRestaurants = (restaurants) => {
        restaurantsContainer.innerHTML = '';
        if (restaurants.length === 0) {
            restaurantsContainer.innerHTML = '<p class="text-gray-400 col-span-full text-center">No restaurants match your search.</p>';
            return;
        }
        restaurants.forEach(restaurant => {
            const card = document.createElement('a');
            card.href = `restaurant.html?id=${restaurant.id}`;
            card.className = 'bg-surface p-6 rounded-lg shadow-md hover:shadow-primary/20 hover:-translate-y-1 transition-all duration-300';
            card.innerHTML = `
                <div class="h-32 bg-gray-700 rounded-md mb-4"></div> <!-- Image Placeholder -->
                <h3 class="text-xl font-bold text-primary">${restaurant.name}</h3>
                <p class="text-text-muted mt-1">${restaurant.address}</p>
            `;
            restaurantsContainer.appendChild(card);
        });
    };

    const filterAndSortRestaurants = () => {
        let filtered = [...allRestaurants];
        const searchTerm = searchBar.value.toLowerCase();
        
        if (searchTerm) {
            filtered = filtered.filter(r => 
                r.name.toLowerCase().includes(searchTerm) || 
                r.address.toLowerCase().includes(searchTerm)
            );
        }

        const sortValue = sortFilter.value;
        if (sortValue === 'name_asc') {
            filtered.sort((a, b) => a.name.localeCompare(b.name));
        } else if (sortValue === 'name_desc') {
            filtered.sort((a, b) => b.name.localeCompare(a.name));
        }

        renderRestaurants(filtered);
    };

    const openModal = () => {
        modalBackdrop.classList.remove('hidden');
        modalBackdrop.classList.add('flex');
    };
    const closeModal = () => {
        modalBackdrop.classList.add('hidden');
        modalBackdrop.classList.remove('flex');
    };

    // --- Event Listeners ---
    logoutBtn.addEventListener('click', () => {
        localStorage.removeItem('foodnow_token');
        window.location.href = '../index.html';
    });
    
    searchBar.addEventListener('input', filterAndSortRestaurants);
    sortFilter.addEventListener('change', filterAndSortRestaurants);
    openModalBtn.addEventListener('click', openModal);
    closeModalBtn.addEventListener('click', closeModal);
    modalBackdrop.addEventListener('click', (e) => {
        if (e.target === modalBackdrop) closeModal();
    });

    applicationForm.addEventListener('submit', async (event) => {
        event.preventDefault();
        const formData = new FormData(applicationForm);
        const data = Object.fromEntries(formData.entries());
        showToast('Submitting application...', 'loading');

        try {
            const response = await fetch(`${API_BASE_URL}/restaurant/apply`, {
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

    // Initial Load
    fetchRestaurants();
});