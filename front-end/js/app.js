/**
 * eFinace Frontend Logic
 * Simple Vanilla JS implementation for dashboard interaction
 */

// API Configuration — use window.env.API_BASE if set (for Netlify/Prod), fallback to localhost for dev
const API_BASE = window?.env?.API_BASE || 'http://localhost:8080/api/v1';

const state = {
    token: localStorage.getItem('token'),
    user: JSON.parse(localStorage.getItem('user')),
    activeView: 'dashboard',
    isRegisterMode: false,
    categories: []
};

const app = {
    init() {
        this.bindAuth();
        this.bindNav();
        this.bindFilters();
        this.checkAuth();
        
        document.getElementById('logout-btn').onclick = () => this.logout();
        document.getElementById('add-record-btn').onclick = () => this.showRecordModal();
        document.getElementById('add-category-btn').onclick = () => this.showCategoryModal();
        document.getElementById('current-date').innerText = new Date().toLocaleDateString('en-US', { weekday: 'long', year: 'numeric', month: 'long', day: 'numeric' });
    },

    // --- Authentication ---
    checkAuth() {
        if (state.token) {
            document.getElementById('auth-view').classList.add('hidden');
            document.getElementById('main-view').classList.remove('hidden');
            document.getElementById('user-display').innerText = `${state.user.fullName} (${state.user.roles.join(', ')})`;
            
            if (state.user.roles.includes('ADMIN')) {
                document.getElementById('admin-nav').classList.remove('hidden');
            } else {
                document.getElementById('admin-nav').classList.add('hidden');
            }
            
            this.switchView('dashboard');
            this.loadCategories(); // Preload categories for filters
        } else {
            document.getElementById('main-view').classList.add('hidden');
            document.getElementById('auth-view').classList.remove('hidden');
            this.updateAuthUI();
        }
    },

    bindAuth() {
        const toggle = document.getElementById('toggle-auth');
        toggle.onclick = (e) => {
            e.preventDefault();
            state.isRegisterMode = !state.isRegisterMode;
            this.updateAuthUI();
        };

        const form = document.getElementById('auth-form');
        form.onsubmit = async (e) => {
            e.preventDefault();
            const email = document.getElementById('auth-email').value;
            const password = document.getElementById('auth-password').value;
            const fullName = document.getElementById('reg-name')?.value;

            const endpoint = state.isRegisterMode ? '/auth/register' : '/auth/login';
            const body = state.isRegisterMode ? { email, password, fullName } : { email, password };

            try {
                const res = await this.api(endpoint, 'POST', body);
                if (res.success) {
                    localStorage.setItem('token', res.data.token);
                    localStorage.setItem('user', JSON.stringify(res.data));
                    state.token = res.data.token;
                    state.user = res.data;
                    this.checkAuth();
                } else {
                    alert(res.message);
                }
            } catch (err) {
                alert('Authentication failed');
            }
        };
    },

    updateAuthUI() {
        document.getElementById('auth-title').innerText = state.isRegisterMode ? 'Create Account' : 'Login';
        document.getElementById('auth-submit').innerText = state.isRegisterMode ? 'Register' : 'Login';
        document.getElementById('toggle-text').innerText = state.isRegisterMode ? 'Already have an account?' : "Don't have an account?";
        document.getElementById('toggle-auth').innerText = state.isRegisterMode ? 'Login' : 'Register';
        document.getElementById('name-field').style.display = state.isRegisterMode ? 'block' : 'none';
    },

    logout() {
        localStorage.removeItem('token');
        localStorage.removeItem('user');
        state.token = null;
        state.user = null;
        this.checkAuth();
    },

    // --- Navigation ---
    bindNav() {
        document.querySelectorAll('.nav-link').forEach(link => {
            link.onclick = (e) => {
                e.preventDefault();
                const view = e.target.closest('a').dataset.view;
                this.switchView(view);
            };
        });
    },

    switchView(viewId) {
        document.querySelectorAll('.view').forEach(v => v.classList.add('hidden'));
        document.getElementById(`view-${viewId}`).classList.remove('hidden');
        
        document.querySelectorAll('.nav-link').forEach(l => {
            l.classList.remove('active');
            if (l.dataset.view === viewId) l.classList.add('active');
        });

        state.activeView = viewId;
        this.loadViewData(viewId);
    },

    async loadViewData(viewId) {
        if (viewId === 'dashboard') this.loadDashboard();
        if (viewId === 'records') this.loadRecords();
        if (viewId === 'categories') this.loadCategoriesTable();
        if (viewId === 'users') this.loadUsersTable();
    },

    // --- API Wrapper ---
    async api(path, method = 'GET', body = null) {
        const headers = { 'Content-Type': 'application/json' };
        if (state.token) headers['Authorization'] = `Bearer ${state.token}`;
        
        const config = { method, headers };
        if (body) config.body = JSON.stringify(body);

        const response = await fetch(`${API_BASE}${path}`, config);
        return await response.json();
    },

    // --- Data Loaders ---
    async loadDashboard() {
        const res = await this.api('/dashboard/summary');
        if (res.success) {
            const data = res.data;
            document.getElementById('stat-income').innerText = this.formatCurrency(data.totalIncome);
            document.getElementById('stat-expense').innerText = this.formatCurrency(data.totalExpenses);
            document.getElementById('stat-balance').innerText = this.formatCurrency(data.netBalance);
            
            const tbody = document.querySelector('#recent-table tbody');
            tbody.innerHTML = data.recentActivity.map(r => `
                <tr>
                    <td>${r.recordDate}</td>
                    <td>${r.categoryName}</td>
                    <td class="${r.type === 'INCOME' ? 'text-success' : 'text-danger'}">${r.type}</td>
                    <td>${this.formatCurrency(r.amount)}</td>
                </tr>
            `).join('') || '<tr><td colspan="4" style="text-align:center">No recent activity</td></tr>';

            const chartList = document.getElementById('category-chart-list');
            chartList.innerHTML = data.categoryTotals.slice(0, 5).map(c => `
                <div style="margin-bottom: 1rem;">
                    <div class="flex" style="font-size: 0.875rem; margin-bottom: 0.25rem;">
                        <span>${c.categoryName}</span>
                        <span>${this.formatCurrency(c.total)}</span>
                    </div>
                    <div style="height: 6px; background: #e2e8f0; border-radius: 3px; overflow: hidden;">
                        <div style="width: ${Math.min(100, (c.total / data.totalIncome * 100) || (c.total / data.totalExpenses * 100)) }%; height: 100%; background: ${c.type === 'INCOME' ? '#22c55e' : '#2563eb'};"></div>
                    </div>
                </div>
            `).join('') || '<p style="text-align:center; color:var(--text-muted)">No data available</p>';
        }

        // Fetch and show Monthly Trends
        const trendsRes = await this.api('/dashboard/trends/monthly');
        if (trendsRes.success) {
            const tbody = document.querySelector('#trends-table tbody');
            tbody.innerHTML = trendsRes.data.map(t => `
                <tr>
                    <td>${t.year}-${String(t.month).padStart(2, '0')}</td>
                    <td class="text-success">${this.formatCurrency(t.totalIncome)}</td>
                    <td class="text-danger">${this.formatCurrency(t.totalExpenses)}</td>
                    <td style="font-weight: 600;">${this.formatCurrency(t.netBalance)}</td>
                </tr>
            `).join('') || '<tr><td colspan="4" style="text-align:center">No trend data available</td></tr>';
        }
    },

    async loadRecords(params = {}) {
        let query = '';
        if (params.type) query += `&type=${params.type}`;
        if (params.categoryId) query += `&categoryId=${params.categoryId}`;
        
        const res = await this.api(`/records?page=0&size=50${query}`);
        if (res.success) {
            const tbody = document.querySelector('#records-table tbody');
            tbody.innerHTML = res.data.map(r => `
                <tr>
                    <td>${r.recordDate}</td>
                    <td>${r.categoryName}</td>
                    <td>${r.description || '-'}</td>
                    <td class="${r.type === 'INCOME' ? 'text-success' : 'text-danger'}">${r.type}</td>
                    <td>${this.formatCurrency(r.amount)}</td>
                    <td>
                        <button class="btn btn-outline" style="padding: 0.25rem 0.5rem;" onclick="app.showRecordModal(${JSON.stringify(r).replace(/"/g, '&quot;')})">Edit</button>
                        <button class="btn btn-danger" style="padding: 0.25rem 0.5rem;" onclick="app.deleteRecord(${r.id})">Delete</button>
                    </td>
                </tr>
            `).join('') || '<tr><td colspan="6" style="text-align:center">No records found</td></tr>';
        }
    },

    async loadCategories() {
        const res = await this.api('/categories');
        if (res.success) {
            state.categories = res.data;
            const selects = ['filter-category', 'modal-category'];
            selects.forEach(id => {
                const el = document.getElementById(id);
                if (!el) return;
                const currentVal = el.value;
                el.innerHTML = (id === 'filter-category' ? '<option value="">All Categories</option>' : '<option value="">Uncategorized</option>') + 
                    res.data.map(c => `<option value="${c.id}">${c.name}</option>`).join('');
                el.value = currentVal;
            });
        }
    },

    async loadCategoriesTable() {
        const res = await this.api('/categories');
        if (res.success) {
            const tbody = document.querySelector('#categories-table tbody');
            tbody.innerHTML = res.data.map(c => `
                <tr>
                    <td>${c.name}</td>
                    <td>${c.description || '-'}</td>
                    <td>
                        <button class="btn btn-outline" onclick="app.showCategoryModal(${JSON.stringify(c).replace(/"/g, '&quot;')})">Edit</button>
                        <button class="btn btn-danger" onclick="app.deleteCategory(${c.id})">Delete</button>
                    </td>
                </tr>
            `).join('') || '<tr><td colspan="3" style="text-align:center">No categories found</td></tr>';
        }
    },

    async loadUsersTable() {
        const res = await this.api('/users');
        if (res.success) {
            const tbody = document.querySelector('#users-table tbody');
            tbody.innerHTML = res.data.map(u => `
                <tr>
                    <td>${u.id}</td>
                    <td>${u.fullName}</td>
                    <td>${u.email}</td>
                    <td>${u.roles.join(', ')}</td>
                    <td><span class="${u.status === 'ACTIVE' ? 'text-success' : 'text-danger'}">${u.status}</span></td>
                    <td>
                        ${u.status === 'ACTIVE' ? `<button class="btn btn-danger" onclick="app.deactivateUser(${u.id})">Deactivate</button>` : '-'}
                    </td>
                </tr>
            `).join('');
        }
    },

    // --- Actions ---
    async deleteRecord(id) {
        if (!confirm('Are you sure you want to delete this record?')) return;
        const res = await this.api(`/records/${id}`, 'DELETE');
        if (res.success) this.loadRecords(); else alert(res.message);
    },

    async deleteCategory(id) {
        if (!confirm('Are you sure? This will not delete records but categories will be removed.')) return;
        const res = await this.api(`/categories/${id}`, 'DELETE');
        if (res.success) this.loadCategoriesTable(); else alert(res.message);
    },

    async deactivateUser(id) {
        if (!confirm('Deactivate this user? They will not be able to login.')) return;
        const res = await this.api(`/users/${id}`, 'DELETE');
        if (res.success) this.loadUsersTable(); else alert(res.message);
    },

    bindFilters() {
        document.getElementById('apply-filters').onclick = () => {
            const type = document.getElementById('filter-type').value;
            const categoryId = document.getElementById('filter-category').value;
            this.loadRecords({ type, categoryId });
        };
    },

    // --- Modals ---
    showRecordModal(record = null) {
        const container = document.getElementById('modal-container');
        container.innerHTML = `
            <div class="modal-overlay">
                <div class="modal-content">
                    <h3 style="margin-bottom: 1.5rem;">${record ? 'Edit' : 'Add'} Transaction</h3>
                    <form id="record-form">
                        <div class="form-group">
                            <label>Amount</label>
                            <input type="number" step="0.01" id="modal-amount" value="${record ? record.amount : ''}" required>
                        </div>
                        <div class="form-group">
                            <label>Type</label>
                            <select id="modal-type" required>
                                <option value="EXPENSE" ${record?.type === 'EXPENSE' ? 'selected' : ''}>Expense</option>
                                <option value="INCOME" ${record?.type === 'INCOME' ? 'selected' : ''}>Income</option>
                            </select>
                        </div>
                        <div class="form-group">
                            <label>Category</label>
                            <select id="modal-category" value="${record ? record.categoryId : ''}"></select>
                        </div>
                        <div class="form-group">
                            <label>Date</label>
                            <input type="date" id="modal-date" value="${record ? record.recordDate : new Date().toISOString().split('T')[0]}" required>
                        </div>
                        <div class="form-group">
                            <label>Description</label>
                            <textarea id="modal-desc">${record ? record.description || '' : ''}</textarea>
                        </div>
                        <div class="flex" style="margin-top: 1.5rem;">
                            <button type="button" class="btn btn-outline" id="close-modal">Cancel</button>
                            <button type="submit" class="btn btn-primary">${record ? 'Update' : 'Save'} Record</button>
                        </div>
                    </form>
                </div>
            </div>
        `;
        container.classList.remove('hidden');
        this.loadCategories(); // Refresh categories in modal select
        
        if (record && record.categoryId) {
            setTimeout(() => document.getElementById('modal-category').value = record.categoryId, 100);
        }

        document.getElementById('close-modal').onclick = () => container.classList.add('hidden');
        document.getElementById('record-form').onsubmit = async (e) => {
            e.preventDefault();
            const body = {
                amount: document.getElementById('modal-amount').value,
                type: document.getElementById('modal-type').value,
                categoryId: document.getElementById('modal-category').value || null,
                recordDate: document.getElementById('modal-date').value,
                description: document.getElementById('modal-desc').value
            };
            const endpoint = record ? `/records/${record.id}` : '/records';
            const method = record ? 'PUT' : 'POST';
            const res = await this.api(endpoint, method, body);
            if (res.success) {
                container.classList.add('hidden');
                this.loadViewData(state.activeView);
                if (state.activeView === 'records') this.loadDashboard(); // Update dash stats too
            } else alert(res.message);
        };
    },

    showCategoryModal(category = null) {
        const container = document.getElementById('modal-container');
        container.innerHTML = `
            <div class="modal-overlay">
                <div class="modal-content">
                    <h3 style="margin-bottom: 1.5rem;">${category ? 'Edit' : 'New'} Category</h3>
                    <form id="category-form">
                        <div class="form-group">
                            <label>Name</label>
                            <input type="text" id="modal-cat-name" value="${category ? category.name : ''}" required>
                        </div>
                        <div class="form-group">
                            <label>Description</label>
                            <textarea id="modal-cat-desc">${category ? category.description || '' : ''}</textarea>
                        </div>
                        <div class="flex" style="margin-top: 1.5rem;">
                            <button type="button" class="btn btn-outline" id="close-modal">Cancel</button>
                            <button type="submit" class="btn btn-primary">Save Category</button>
                        </div>
                    </form>
                </div>
            </div>
        `;
        container.classList.remove('hidden');
        document.getElementById('close-modal').onclick = () => container.classList.add('hidden');
        document.getElementById('category-form').onsubmit = async (e) => {
            e.preventDefault();
            const body = {
                name: document.getElementById('modal-cat-name').value,
                description: document.getElementById('modal-cat-desc').value
            };
            const endpoint = category ? `/categories/${category.id}` : '/categories';
            const method = category ? 'PUT' : 'POST';
            const res = await this.api(endpoint, method, body);
            if (res.success) {
                container.classList.add('hidden');
                this.loadViewData('categories');
                this.loadCategories(); // Update global category state
            } else alert(res.message);
        };
    },

    formatCurrency(val) {
        return new Intl.NumberFormat('en-IN', { style: 'currency', currency: 'INR' }).format(val || 0);
    }
};

window.onload = () => app.init();
