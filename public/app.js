/* ==========================================================================
   ATHENA LIBRARY MANAGEMENT SYSTEM - FRONTEND APP ENGINE (VANILLA JS)
   ========================================================================== */

// --- Global Application State ---
const state = {
  token: localStorage.getItem('athena_token') || null,
  user: JSON.parse(localStorage.getItem('athena_user')) || null,
  activeTab: 'books',
  books: [],
  selectedCategory: 'all',
  searchQuery: ''
};

// --- API Configuration ---
const API_BASE_URL = '/api';

// --- Toast Notifications Helper ---
function showToast(message, type = 'success') {
  const container = document.getElementById('toast-container');
  const toast = document.createElement('div');
  toast.className = `toast ${type}`;
  
  const iconName = type === 'success' ? 'check-circle' : 'alert-circle';
  toast.innerHTML = `
    <i data-lucide="${iconName}"></i>
    <span>${message}</span>
  `;
  
  container.appendChild(toast);
  lucide.createIcons(); // Initialize the new icon
  
  // Fade out and remove
  setTimeout(() => {
    toast.classList.add('fade-out');
    setTimeout(() => toast.remove(), 300);
  }, 4000);
}

// --- Headers Helper for API Requests ---
function getHeaders(contentType = 'application/json') {
  const headers = {};
  if (contentType) {
    headers['Content-Type'] = contentType;
  }
  if (state.token) {
    headers['Authorization'] = `Bearer ${state.token}`;
  }
  return headers;
}

// --- App Initialization ---
document.addEventListener('DOMContentLoaded', () => {
  setupEventListeners();
  checkAuth();
  lucide.createIcons();
});

// --- Authentication & Session Management ---
function checkAuth() {
  const authOverlay = document.getElementById('auth-overlay');
  
  if (!state.token || !state.user) {
    // Session is absent: display login overlay
    authOverlay.classList.remove('hidden');
    document.getElementById('app').style.opacity = '0.3';
  } else {
    // Session is present: restore layout
    authOverlay.classList.add('hidden');
    document.getElementById('app').style.opacity = '1';
    
    // Update profile card details
    document.getElementById('user-display-name').textContent = state.user.name;
    document.getElementById('user-display-role').textContent = state.user.role === 'ADMIN' ? 'Administrador' : 'Estudiante';
    document.getElementById('user-avatar-initials').textContent = state.user.name.split(' ').map(n => n[0]).join('').substring(0, 2).toUpperCase();
    
    // Adjust layout for role-based permissions
    const adminElements = document.querySelectorAll('.admin-only');
    const studentElements = document.querySelectorAll('.student-only');
    
    if (state.user.role === 'ADMIN') {
      adminElements.forEach(el => el.classList.remove('hidden'));
      studentElements.forEach(el => el.classList.add('hidden'));
      if (state.activeTab === 'my-loans') state.activeTab = 'books';
    } else {
      adminElements.forEach(el => el.classList.add('hidden'));
      studentElements.forEach(el => el.classList.remove('hidden'));
      if (state.activeTab === 'admin-loans' || state.activeTab === 'admin-users') state.activeTab = 'books';
    }
    
    // Select correct menu item and trigger data loading
    updateActiveTabUI();
    loadTabContent();
  }
}

// Save authentication data
function saveAuth(token, user) {
  state.token = token;
  state.user = user;
  localStorage.setItem('athena_token', token);
  localStorage.setItem('athena_user', JSON.stringify(user));
  checkAuth();
  showToast(`¡Bienvenido de vuelta, ${user.name}!`);
}

// Logout procedure
function handleLogout() {
  state.token = null;
  state.user = null;
  localStorage.removeItem('athena_token');
  localStorage.removeItem('athena_user');
  state.activeTab = 'books';
  showToast('Has cerrado sesión correctamente.', 'success');
  checkAuth();
}

// --- Navigation & Tab Switching ---
function setupEventListeners() {
  // Sidebar Tabs Navigation
  document.querySelectorAll('.sidebar-menu .menu-item').forEach(item => {
    const tabName = item.getAttribute('data-tab');
    if (tabName) {
      item.addEventListener('click', (e) => {
        e.preventDefault();
        document.querySelectorAll('.sidebar-menu .menu-item').forEach(m => m.classList.remove('active'));
        item.classList.add('active');
        state.activeTab = tabName;
        updateActiveTabUI();
        loadTabContent();
      });
    }
  });

  // Logout button
  document.getElementById('logout-button').addEventListener('click', handleLogout);

  // Authentication Switch Login/Register Tabs
  const loginTabBtn = document.getElementById('auth-tab-login');
  const registerTabBtn = document.getElementById('auth-tab-register');
  const loginForm = document.getElementById('login-form');
  const registerForm = document.getElementById('register-form');

  loginTabBtn.addEventListener('click', () => {
    loginTabBtn.classList.add('active');
    registerTabBtn.classList.remove('active');
    loginForm.classList.remove('hidden');
    registerForm.classList.add('hidden');
  });

  registerTabBtn.addEventListener('click', () => {
    registerTabBtn.classList.add('active');
    loginTabBtn.classList.remove('active');
    registerForm.classList.remove('hidden');
    loginForm.classList.add('hidden');
  });

  // Submit Forms for Authentication
  loginForm.addEventListener('submit', async (e) => {
    e.preventDefault();
    const email = document.getElementById('login-email').value;
    const password = document.getElementById('login-password').value;
    
    try {
      const response = await fetch(`${API_BASE_URL}/users/login`, {
        method: 'POST',
        headers: getHeaders(),
        body: JSON.stringify({ email, password })
      });
      
      const data = await response.json();
      if (!response.ok) throw new Error(data.error || 'Fallo de inicio de sesión');
      
      saveAuth(data.token, data.user);
    } catch (err) {
      showToast(err.message, 'error');
    }
  });

  registerForm.addEventListener('submit', async (e) => {
    e.preventDefault();
    const name = document.getElementById('register-name').value;
    const email = document.getElementById('register-email').value;
    const password = document.getElementById('register-password').value;
    const role = document.getElementById('register-role').value;
    
    try {
      const response = await fetch(`${API_BASE_URL}/users/register`, {
        method: 'POST',
        headers: getHeaders(),
        body: JSON.stringify({ name, email, password, role })
      });
      
      const data = await response.json();
      if (!response.ok) throw new Error(data.error || 'Fallo en el registro de usuario');
      
      showToast('Registro exitoso. Ahora puedes iniciar sesión.');
      
      // Auto switch back to login
      loginTabBtn.click();
      document.getElementById('login-email').value = email;
      document.getElementById('login-password').value = '';
    } catch (err) {
      showToast(err.message, 'error');
    }
  });

  // Real-time search filter input
  const searchInput = document.getElementById('search-input');
  let searchTimeout;
  searchInput.addEventListener('input', (e) => {
    clearTimeout(searchTimeout);
    state.searchQuery = e.target.value.trim();
    // Debounce search querying by 350ms
    searchTimeout = setTimeout(() => {
      if (state.activeTab === 'books') {
        loadBooks();
      }
    }, 350);
  });

  // Category filter chips triggers
  document.querySelectorAll('#category-filters-container .category-chip').forEach(chip => {
    chip.addEventListener('click', () => {
      document.querySelectorAll('#category-filters-container .category-chip').forEach(c => c.classList.remove('active'));
      chip.classList.add('active');
      state.selectedCategory = chip.getAttribute('data-category');
      loadBooks();
    });
  });

  // Add Book modal controls (Admin only)
  document.getElementById('btn-new-book').addEventListener('click', () => {
    openBookModal();
  });
  document.getElementById('book-modal-close-btn').addEventListener('click', closeBookModal);
  document.getElementById('book-modal-cancel-btn').addEventListener('click', closeBookModal);

  document.getElementById('book-form').addEventListener('submit', async (e) => {
    e.preventDefault();
    const id = document.getElementById('book-form-id').value;
    const title = document.getElementById('book-title').value;
    const author = document.getElementById('book-author').value;
    const category = document.getElementById('book-category').value;
    const stock = parseInt(document.getElementById('book-stock').value);
    
    const bookData = { title, author, category, stock };
    const method = id ? 'PUT' : 'POST';
    const url = id ? `${API_BASE_URL}/books/${id}` : `${API_BASE_URL}/books`;
    
    try {
      const response = await fetch(url, {
        method: method,
        headers: getHeaders(),
        body: JSON.stringify(bookData)
      });
      
      const data = await response.json();
      if (!response.ok) throw new Error(data.error || 'No se pudo guardar el libro');
      
      showToast(id ? 'Libro actualizado correctamente' : 'Libro creado exitosamente');
      closeBookModal();
      loadBooks();
    } catch (err) {
      showToast(err.message, 'error');
    }
  });

  // Rent/Loan modal controls (Student only)
  document.getElementById('loan-modal-close-btn').addEventListener('click', closeLoanModal);
  document.getElementById('loan-modal-cancel-btn').addEventListener('click', closeLoanModal);

  document.getElementById('loan-form').addEventListener('submit', async (e) => {
    e.preventDefault();
    const bookId = parseInt(document.getElementById('loan-form-book-id').value);
    const estimatedReturn = document.getElementById('loan-return-date').value;
    
    // Check future date
    if (new Date(estimatedReturn) <= new Date()) {
      showToast('La fecha de devolución estimada debe ser en el futuro.', 'error');
      return;
    }
    
    try {
      const response = await fetch(`${API_BASE_URL}/loans`, {
        method: 'POST',
        headers: getHeaders(),
        body: JSON.stringify({ bookId, estimatedReturn: new Date(estimatedReturn).toISOString() })
      });
      
      const data = await response.json();
      if (!response.ok) throw new Error(data.error || 'No se pudo registrar el préstamo');
      
      showToast('Préstamo realizado exitosamente.');
      closeLoanModal();
      loadBooks(); // reload stock count
    } catch (err) {
      showToast(err.message, 'error');
    }
  });
}

function updateActiveTabUI() {
  // Update sidebar active classes
  document.querySelectorAll('.sidebar-menu .menu-item').forEach(item => {
    const tabName = item.getAttribute('data-tab');
    if (tabName === state.activeTab) {
      item.classList.add('active');
    } else {
      item.classList.remove('active');
    }
  });

  // Switch display elements
  document.querySelectorAll('.content-body section').forEach(section => {
    if (section.id === `tab-${state.activeTab}`) {
      section.classList.add('active');
    } else {
      section.classList.remove('active');
    }
  });
}

// --- Data Loading Dispatcher ---
function loadTabContent() {
  switch (state.activeTab) {
    case 'books':
      loadBooks();
      break;
    case 'my-loans':
      loadMyLoans();
      break;
    case 'admin-loans':
      loadAdminLoans();
      break;
    case 'admin-users':
      loadAdminUsers();
      break;
  }
}

// --- Tab Controller: Books Catalogue ---
async function loadBooks() {
  const container = document.getElementById('books-grid-container');
  container.innerHTML = `
    <div class="loader-container">
      <div class="spinner"></div>
      <p>Buscando libros...</p>
    </div>
  `;
  
  try {
    // Set query filters
    let url = `${API_BASE_URL}/books`;
    const params = new URLSearchParams();
    
    if (state.searchQuery) {
      params.append('title', state.searchQuery);
    }
    if (state.selectedCategory !== 'all') {
      params.append('category', state.selectedCategory);
    }
    
    if (params.toString()) {
      url += `?${params.toString()}`;
    }
    
    const response = await fetch(url, { headers: getHeaders() });
    const books = await response.json();
    
    if (!response.ok) throw new Error(books.error || 'Fallo al recuperar libros');
    
    state.books = books;
    renderBooksList();
  } catch (err) {
    showToast(err.message, 'error');
    container.innerHTML = `<div class="empty-state"><p>Error al cargar catálogo de libros.</p></div>`;
  }
}

function renderBooksList() {
  const container = document.getElementById('books-grid-container');
  
  if (state.books.length === 0) {
    container.innerHTML = `
      <div class="empty-state">
        <i data-lucide="book-x"></i>
        <p>No se encontraron libros en esta búsqueda.</p>
      </div>
    `;
    lucide.createIcons();
    return;
  }
  
  container.innerHTML = state.books.map(book => {
    const isOutOfStock = book.stock <= 0;
    const stockClass = isOutOfStock ? 'out-of-stock' : 'in-stock';
    const stockText = isOutOfStock ? 'Agotado' : `${book.stock} disponibles`;
    
    let actionButtons = '';
    
    if (state.user && state.user.role === 'ADMIN') {
      actionButtons = `
        <div class="book-actions-admin">
          <button class="btn btn-secondary btn-sm" onclick="openBookModal(${book.id})">
            <i data-lucide="edit-2"></i> Editar
          </button>
          <button class="btn btn-danger btn-sm" onclick="deleteBook(${book.id})">
            <i data-lucide="trash-2"></i> Eliminar
          </button>
        </div>
      `;
    } else {
      actionButtons = `
        <button class="btn btn-primary btn-block" ${isOutOfStock ? 'disabled' : ''} onclick="openLoanModal(${book.id})">
          <i data-lucide="bookmark-plus"></i> Solicitar Préstamo
        </button>
      `;
    }
    
    return `
      <article class="book-card">
        <div class="book-card-header">
          <span class="book-category-tag">${book.category}</span>
          <span class="book-stock-indicator ${stockClass}">
            <span class="status-dot ${isOutOfStock ? 'offline' : 'online'}"></span>
            ${stockText}
          </span>
        </div>
        <div class="book-card-body">
          <h3 class="book-card-title">${book.title}</h3>
          <p class="book-card-author">${book.author}</p>
        </div>
        <div class="book-card-footer">
          ${actionButtons}
        </div>
      </article>
    `;
  }).join('');
  
  lucide.createIcons();
}

// --- Tab Controller: My Loans (Student View) ---
async function loadMyLoans() {
  const tbody = document.getElementById('my-loans-table-body');
  const emptyState = document.getElementById('my-loans-empty');
  tbody.innerHTML = '';
  
  try {
    const response = await fetch(`${API_BASE_URL}/loans/my-loans`, { headers: getHeaders() });
    const loans = await response.json();
    
    if (!response.ok) throw new Error(loans.error || 'Error al obtener préstamos');
    
    if (loans.length === 0) {
      emptyState.classList.remove('hidden');
    } else {
      emptyState.classList.add('hidden');
      tbody.innerHTML = loans.map(loan => {
        const isBorrowed = loan.status === 'BORROWED';
        const badgeClass = isBorrowed ? 'badge-status-borrowed' : 'badge-status-returned';
        const badgeText = isBorrowed ? 'Solicitado' : 'Devuelto';
        
        return `
          <tr>
            <td><strong>${loan.book.title}</strong></td>
            <td>${loan.book.author}</td>
            <td>${new Date(loan.loanDate).toLocaleDateString()}</td>
            <td>${new Date(loan.estimatedReturn).toLocaleDateString()}</td>
            <td>
              <span class="badge ${badgeClass}">${badgeText}</span>
            </td>
          </tr>
        `;
      }).join('');
    }
  } catch (err) {
    showToast(err.message, 'error');
  }
}

// --- Tab Controller: Admin Loans (Admin Control) ---
async function loadAdminLoans() {
  const tbody = document.getElementById('admin-loans-table-body');
  const emptyState = document.getElementById('admin-loans-empty');
  tbody.innerHTML = '';
  
  try {
    const response = await fetch(`${API_BASE_URL}/loans`, { headers: getHeaders() });
    const loans = await response.json();
    
    if (!response.ok) throw new Error(loans.error || 'Error al obtener control de préstamos');
    
    if (loans.length === 0) {
      emptyState.classList.remove('hidden');
    } else {
      emptyState.classList.add('hidden');
      tbody.innerHTML = loans.map(loan => {
        const isBorrowed = loan.status === 'BORROWED';
        const badgeClass = isBorrowed ? 'badge-status-borrowed' : 'badge-status-returned';
        const badgeText = isBorrowed ? 'Prestado' : 'Devuelto';
        
        const returnAction = isBorrowed
          ? `<button class="btn btn-primary btn-sm" onclick="returnLoan(${loan.id})">Aceptar Devolución</button>`
          : `<span class="badge badge-status-returned"><i data-lucide="check"></i> Listo</span>`;
        
        return `
          <tr>
            <td>
              <div style="font-weight: 600;">${loan.user.name}</div>
              <div style="font-size: 0.75rem; color: var(--text-muted);">${loan.user.email}</div>
            </td>
            <td><strong>${loan.book.title}</strong></td>
            <td>${new Date(loan.loanDate).toLocaleDateString()}</td>
            <td>${new Date(loan.estimatedReturn).toLocaleDateString()}</td>
            <td>
              <span class="badge ${badgeClass}">${badgeText}</span>
            </td>
            <td class="actions-col">${returnAction}</td>
          </tr>
        `;
      }).join('');
    }
    lucide.createIcons();
  } catch (err) {
    showToast(err.message, 'error');
  }
}

// --- Tab Controller: Admin Users (Admin View) ---
async function loadAdminUsers() {
  const tbody = document.getElementById('admin-users-table-body');
  tbody.innerHTML = '';
  
  try {
    const response = await fetch(`${API_BASE_URL}/users`, { headers: getHeaders() });
    const users = await response.json();
    
    if (!response.ok) throw new Error(users.error || 'Error al obtener usuarios');
    
    tbody.innerHTML = users.map(user => {
      const isAltRole = user.role === 'ADMIN';
      const roleBadge = isAltRole ? 'badge-role-admin' : 'badge-role-student';
      const roleText = isAltRole ? 'Administrador' : 'Estudiante';
      const registerDate = user.createdAt ? new Date(user.createdAt).toLocaleDateString() : 'N/D';
      
      return `
        <tr>
          <td>#${user.id}</td>
          <td><strong>${user.name}</strong></td>
          <td>${user.email}</td>
          <td>
            <span class="badge ${roleBadge}">${roleText}</span>
          </td>
          <td>${registerDate}</td>
        </tr>
      `;
    }).join('');
    lucide.createIcons();
  } catch (err) {
    showToast(err.message, 'error');
  }
}

// --- Create/Edit Book Modal Actions (Admin Only) ---
function openBookModal(bookId = null) {
  const modal = document.getElementById('book-modal');
  const titleInput = document.getElementById('book-title');
  const authorInput = document.getElementById('book-author');
  const categoryInput = document.getElementById('book-category');
  const stockInput = document.getElementById('book-stock');
  const idInput = document.getElementById('book-form-id');
  const modalTitle = document.getElementById('book-modal-title');
  
  if (bookId) {
    // Edit mode
    modalTitle.textContent = 'Editar Libro';
    const book = state.books.find(b => b.id === bookId);
    if (book) {
      idInput.value = book.id;
      titleInput.value = book.title;
      authorInput.value = book.author;
      categoryInput.value = book.category;
      stockInput.value = book.stock;
    }
  } else {
    // Add mode
    modalTitle.textContent = 'Agregar Nuevo Libro';
    idInput.value = '';
    titleInput.value = '';
    authorInput.value = '';
    categoryInput.value = '';
    stockInput.value = '1';
  }
  
  modal.classList.remove('hidden');
}

function closeBookModal() {
  document.getElementById('book-modal').classList.add('hidden');
}

// Delete Book
async function deleteBook(id) {
  if (!confirm('¿Estás seguro de que deseas eliminar este libro? Esta acción es irreversible.')) {
    return;
  }
  
  try {
    const response = await fetch(`${API_BASE_URL}/books/${id}`, {
      method: 'DELETE',
      headers: getHeaders()
    });
    
    if (!response.ok) {
      const data = await response.json();
      throw new Error(data.error || 'No se pudo eliminar el libro');
    }
    
    showToast('Libro eliminado correctamente.');
    loadBooks();
  } catch (err) {
    showToast(err.message, 'error');
  }
}

// --- Rent Book Actions (Student Only) ---
function openLoanModal(bookId) {
  const book = state.books.find(b => b.id === bookId);
  if (!book) return;
  
  if (book.stock <= 0) {
    showToast('Este libro no tiene existencias disponibles en este momento.', 'error');
    return;
  }
  
  document.getElementById('loan-form-book-id').value = book.id;
  document.getElementById('loan-summary-title').textContent = book.title;
  document.getElementById('loan-summary-author').textContent = book.author;
  
  // Set default return date to 14 days in the future
  const defaultReturnDate = new Date();
  defaultReturnDate.setDate(defaultReturnDate.getDate() + 14);
  
  const yyyy = defaultReturnDate.getFullYear();
  const mm = String(defaultReturnDate.getMonth() + 1).padStart(2, '0');
  const dd = String(defaultReturnDate.getDate()).padStart(2, '0');
  
  const datePicker = document.getElementById('loan-return-date');
  datePicker.value = `${yyyy}-${mm}-${dd}`;
  datePicker.min = new Date().toISOString().split('T')[0]; // Minimum date today
  
  document.getElementById('loan-modal').classList.remove('hidden');
}

function closeLoanModal() {
  document.getElementById('loan-modal').classList.add('hidden');
}

// --- Return Book Loan Action (Admin Only) ---
async function returnLoan(loanId) {
  try {
    const response = await fetch(`${API_BASE_URL}/loans/${loanId}/return`, {
      method: 'POST',
      headers: getHeaders()
    });
    
    const data = await response.json();
    if (!response.ok) throw new Error(data.error || 'No se pudo procesar la devolución');
    
    showToast('Devolución registrada correctamente. Inventario actualizado.');
    loadAdminLoans(); // reload list
  } catch (err) {
    showToast(err.message, 'error');
  }
}

// Map window actions to let inline HTML attributes (like onclick) function correctly
window.openBookModal = openBookModal;
window.deleteBook = deleteBook;
window.openLoanModal = openLoanModal;
window.returnLoan = returnLoan;
