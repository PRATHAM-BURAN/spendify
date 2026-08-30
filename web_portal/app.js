// Spendify Web Application & Firebase Sync Manager
const FIREBASE_CONFIG = {
    projectId: "spendify1",
    apiKey: "AIzaSyD6mS6GgtG1xk9Ha7uJhLaD7LjOR7DE50g",
    storageBucket: "spendify1.firebasestorage.app",
    projectNumber: "1085970153981",
    appId: "1:1085970153981:android:d82ac8d319f0d5da381994"
};

const CATEGORIES = [
    { name: 'Food & Dining', icon: 'utensils', color: '#8b5cf6' },
    { name: 'Groceries', icon: 'shopping-cart', color: '#3b82f6' },
    { name: 'Bills & Utilities', icon: 'receipt', color: '#10b981' },
    { name: 'Transport', icon: 'car', color: '#06b6d4' },
    { name: 'Shopping', icon: 'shopping-bag', color: '#ec4899' },
    { name: 'Subscriptions', icon: 'play-circle', color: '#f59e0b' },
    { name: 'Salary', icon: 'wallet', color: '#10b981', income: true },
    { name: 'Freelance', icon: 'briefcase', color: '#6366f1', income: true },
    { name: 'Investments', icon: 'trending-up', color: '#14b8a6', income: true }
];

// App State (Default: Empty / Null)
let transactions = JSON.parse(localStorage.getItem('spendify_tx')) || [];
let currentUser = JSON.parse(localStorage.getItem('spendify_user')) || null;
let monthlyBudgetLimit = parseFloat(localStorage.getItem('spendify_budget')) || 0;
let currencySymbol = localStorage.getItem('spendify_curr') || '$';
let currencyCode = localStorage.getItem('spendify_curr_code') || 'USD';

let selectedCategory = 'Food & Dining';
let selectedPaymentMethod = 'Card';
let currentTxType = 'EXPENSE';
let authMode = 'SIGN_IN'; // 'SIGN_IN' or 'SIGN_UP'
let historyFilter = 'ALL'; // 'ALL', 'EXPENSE', 'INCOME'

// Navigation Stack for Back Button
let navigationStack = ['dashboard'];

// Calendar State
let currentCalendarYear = new Date().getFullYear();
let currentCalendarMonth = new Date().getMonth();
let selectedCalendarDate = new Date();

// --- Storage & Sync Engine ---
function saveState() {
    localStorage.setItem('spendify_tx', JSON.stringify(transactions));
    localStorage.setItem('spendify_budget', monthlyBudgetLimit.toString());
    localStorage.setItem('spendify_curr', currencySymbol);
    localStorage.setItem('spendify_curr_code', currencyCode);

    updateSyncStatusBar();
    updateHeaderProfile();
    updateDashboard();
    renderHistoryFeed();
    renderCalendar();
    renderCharts();
    renderBudgets();
}

function updateSyncStatusBar() {
    const isOnline = navigator.onLine;
    const bar = document.getElementById('syncStatusBar');
    const text = document.getElementById('syncStatusText');
    const badge = document.getElementById('syncPendingBadge');

    const pendingCount = transactions.filter(t => t.syncStatus === 'PENDING_SYNC').length;

    if (!isOnline) {
        bar.className = 'sync-status-bar offline';
        text.innerText = '⚡ Offline Mode • Changes saved locally in Room/Storage';
        badge.innerText = `${pendingCount} pending sync`;
    } else {
        bar.className = 'sync-status-bar';
        if (currentUser) {
            text.innerText = `Online • Synced with Cloud Firestore (${currentUser.email})`;
        } else {
            text.innerText = 'Online • Local Room active (Sign in for Cloud Backup)';
        }
        badge.innerText = pendingCount > 0 ? `${pendingCount} synced now` : 'All synced';

        if (pendingCount > 0) {
            transactions.forEach(t => { t.syncStatus = 'SYNCED'; });
            localStorage.setItem('spendify_tx', JSON.stringify(transactions));
        }
    }
}

window.addEventListener('online', updateSyncStatusBar);
window.addEventListener('offline', updateSyncStatusBar);

function updateHeaderProfile() {
    const avatar = document.getElementById('userAvatar');
    const subtitle = document.getElementById('userHeaderSubtitle');
    const setProfileName = document.getElementById('settingsProfileName');
    const setProfileEmail = document.getElementById('settingsProfileEmail');
    const currDisplay = document.getElementById('activeCurrencyDisplay');

    if (currDisplay) currDisplay.innerText = `${currencyCode} (${currencySymbol})`;
    const modalSymbol = document.getElementById('modalCurrencySymbol');
    if (modalSymbol) modalSymbol.innerText = currencySymbol;

    if (currentUser) {
        const initial = (currentUser.displayName || currentUser.email || 'U')[0].toUpperCase();
        avatar.innerText = initial;
        subtitle.innerText = currentUser.displayName || currentUser.email;
        if (setProfileName) setProfileName.innerText = currentUser.displayName || 'User';
        if (setProfileEmail) setProfileEmail.innerText = currentUser.email;
    } else {
        avatar.innerText = '?';
        subtitle.innerText = 'Tap to Sign In / Sign Up';
        if (setProfileName) setProfileName.innerText = 'Not Signed In';
        if (setProfileEmail) setProfileEmail.innerText = 'Tap to Sign In or Sign Up';
    }
}

// --- Navigation with Back Stack ---
function navigateTo(screenId) {
    if (navigationStack[navigationStack.length - 1] !== screenId) {
        navigationStack.push(screenId);
    }
    renderActiveScreen(screenId);
}

function navigateBack() {
    if (navigationStack.length > 1) {
        navigationStack.pop();
        const prevScreen = navigationStack[navigationStack.length - 1];
        renderActiveScreen(prevScreen);
    }
}

function renderActiveScreen(screenId) {
    document.querySelectorAll('.screen-view').forEach(s => s.classList.remove('active'));
    document.querySelectorAll('.nav-tab-btn').forEach(t => t.classList.remove('active'));

    const backBtn = document.getElementById('headerBackBtn');
    if (backBtn) {
        backBtn.style.display = navigationStack.length > 1 ? 'flex' : 'none';
    }

    const titleMap = {
        'dashboard': 'Spendify',
        'calendar': 'Calendar Tracker',
        'history': 'History',
        'reports': 'Reports',
        'budgets': 'Budgets',
        'settings': 'Settings'
    };

    document.getElementById('screenTitle').innerText = titleMap[screenId] || 'Spendify';

    if (screenId === 'dashboard') {
        document.getElementById('screenDashboard').classList.add('active');
        const tab = document.getElementById('tabDashboard');
        if (tab) tab.classList.add('active');
        updateDashboard();
    } else if (screenId === 'calendar') {
        document.getElementById('screenCalendar').classList.add('active');
        const tab = document.getElementById('tabCalendar');
        if (tab) tab.classList.add('active');
        renderCalendar();
    } else if (screenId === 'history') {
        document.getElementById('screenHistory').classList.add('active');
        const tab = document.getElementById('tabHistory');
        if (tab) tab.classList.add('active');
        renderHistoryFeed();
    } else if (screenId === 'reports') {
        document.getElementById('screenReports').classList.add('active');
        renderCharts();
    } else if (screenId === 'budgets') {
        document.getElementById('screenBudgets').classList.add('active');
        const tab = document.getElementById('tabBudgets');
        if (tab) tab.classList.add('active');
        renderBudgets();
    } else if (screenId === 'settings') {
        document.getElementById('screenSettings').classList.add('active');
    }

    if (window.lucide) {
        window.lucide.createIcons();
    }
}

// --- Dashboard ---
function updateDashboard() {
    let totalIncome = 0;
    let totalExpense = 0;

    transactions.forEach(t => {
        if (t.isIncome) totalIncome += t.amount;
        else totalExpense += t.amount;
    });

    const totalBalance = totalIncome - totalExpense;
    document.getElementById('totalBalanceDisplay').innerText = `${currencySymbol}${totalBalance.toLocaleString('en-US', { minimumFractionDigits: 2, maximumFractionDigits: 2 })}`;
    document.getElementById('totalIncomeDisplay').innerText = `+${currencySymbol}${totalIncome.toLocaleString('en-US', { minimumFractionDigits: 2, maximumFractionDigits: 2 })}`;
    document.getElementById('totalExpenseDisplay').innerText = `-${currencySymbol}${totalExpense.toLocaleString('en-US', { minimumFractionDigits: 2, maximumFractionDigits: 2 })}`;

    // Budget bar
    if (monthlyBudgetLimit > 0) {
        const budgetPercent = Math.min(100, Math.round((totalExpense / monthlyBudgetLimit) * 100));
        const budgetRemaining = Math.max(0, monthlyBudgetLimit - totalExpense);
        document.getElementById('budgetPercentDisplay').innerText = `${budgetPercent}% Used`;
        document.getElementById('budgetProgressBar').style.width = `${budgetPercent}%`;
        document.getElementById('budgetRemainingDisplay').innerText = `${currencySymbol}${budgetRemaining.toFixed(2)} remaining of ${currencySymbol}${monthlyBudgetLimit.toFixed(2)}`;
    } else {
        document.getElementById('budgetPercentDisplay').innerText = `No Limit Set`;
        document.getElementById('budgetProgressBar').style.width = `0%`;
        document.getElementById('budgetRemainingDisplay').innerText = `Tap to set a monthly budget`;
    }

    // Recent Feed
    const feed = document.getElementById('dashboardRecentFeed');
    feed.innerHTML = '';

    if (transactions.length === 0) {
        feed.innerHTML = `
            <div class="empty-state-card" onclick="openAddModal()">
                <div class="empty-title">No transactions yet</div>
                <div class="empty-sub">Tap the + button below to record your first expense or income.</div>
            </div>
        `;
    } else {
        transactions.slice(0, 5).forEach(t => {
            feed.appendChild(createTxElement(t));
        });
    }

    if (window.lucide) {
        window.lucide.createIcons();
    }
}

function createTxElement(t) {
    const card = document.createElement('div');
    card.className = 'tx-item-card';
    card.innerHTML = `
        <div class="tx-left">
            <div class="tx-cat-icon" style="background: rgba(139, 92, 246, 0.15); color: #a78bfa;">
                <i data-lucide="${t.icon || 'receipt'}"></i>
            </div>
            <div>
                <div class="tx-title">${escapeHtml(t.title || t.category)}</div>
                <div class="tx-meta">${escapeHtml(t.category)} • ${escapeHtml(t.payment || 'Card')}</div>
            </div>
        </div>
        <div class="tx-right">
            <div class="tx-amount ${t.isIncome ? 'income' : 'expense'}">${t.isIncome ? '+' : '-'}${currencySymbol}${t.amount.toFixed(2)}</div>
            <div class="tx-date-label">${escapeHtml(t.date || 'Today')}</div>
        </div>
    `;
    return card;
}

// --- Calendar View & Daily Tracking ---
function renderCalendar() {
    const monthNames = ["January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"];
    const titleEl = document.getElementById('calendarMonthYearTitle');
    if (titleEl) titleEl.innerText = `${monthNames[currentCalendarMonth]} ${currentCalendarYear}`;

    const grid = document.getElementById('calendarDaysGrid');
    if (!grid) return;
    grid.innerHTML = '';

    const firstDayIndex = new Date(currentCalendarYear, currentCalendarMonth, 1).getDay();
    const totalDaysInMonth = new Date(currentCalendarYear, currentCalendarMonth + 1, 0).getDate();
    const prevMonthLastDay = new Date(currentCalendarYear, currentCalendarMonth, 0).getDate();

    const today = new Date();

    // Trailing days from previous month
    for (let i = firstDayIndex - 1; i >= 0; i--) {
        const cell = document.createElement('div');
        cell.className = 'calendar-day-cell other-month';
        cell.innerText = prevMonthLastDay - i;
        grid.appendChild(cell);
    }

    // Days in current month
    for (let day = 1; day <= totalDaysInMonth; day++) {
        const cell = document.createElement('div');
        cell.className = 'calendar-day-cell';
        cell.innerText = day;

        const isToday = today.getFullYear() === currentCalendarYear && today.getMonth() === currentCalendarMonth && today.getDate() === day;
        if (isToday) cell.classList.add('today');

        const isSelected = selectedCalendarDate.getFullYear() === currentCalendarYear && selectedCalendarDate.getMonth() === currentCalendarMonth && selectedCalendarDate.getDate() === day;
        if (isSelected) cell.classList.add('selected');

        // Check transactions on this day
        const dayTxs = transactions.filter(t => {
            const d = new Date(t.timestamp || Date.now());
            return d.getFullYear() === currentCalendarYear && d.getMonth() === currentCalendarMonth && d.getDate() === day;
        });

        if (dayTxs.length > 0) {
            const dot = document.createElement('div');
            const hasExpense = dayTxs.some(t => !t.isIncome);
            dot.className = `day-dot ${hasExpense ? 'expense' : 'income'}`;
            cell.appendChild(dot);
        }

        cell.onclick = () => {
            selectedCalendarDate = new Date(currentCalendarYear, currentCalendarMonth, day);
            renderCalendar();
            renderSelectedDayTransactions();
        };

        grid.appendChild(cell);
    }

    renderSelectedDayTransactions();
}

function prevCalendarMonth() {
    currentCalendarMonth--;
    if (currentCalendarMonth < 0) {
        currentCalendarMonth = 11;
        currentCalendarYear--;
    }
    renderCalendar();
}

function nextCalendarMonth() {
    currentCalendarMonth++;
    if (currentCalendarMonth > 11) {
        currentCalendarMonth = 0;
        currentCalendarYear++;
    }
    renderCalendar();
}

function renderSelectedDayTransactions() {
    const dateStr = selectedCalendarDate.toLocaleDateString('en-US', { month: 'short', day: 'numeric', year: 'numeric' });
    const labelEl = document.getElementById('calendarSelectedDateLabel');
    if (labelEl) labelEl.innerText = dateStr;

    const dayTxs = transactions.filter(t => {
        const d = new Date(t.timestamp || Date.now());
        return d.getFullYear() === selectedCalendarDate.getFullYear() && d.getMonth() === selectedCalendarDate.getMonth() && d.getDate() === selectedCalendarDate.getDate();
    });

    const dayExpense = dayTxs.filter(t => !t.isIncome).reduce((sum, t) => sum + t.amount, 0);
    const dayIncome = dayTxs.filter(t => t.isIncome).reduce((sum, t) => sum + t.amount, 0);

    const totalsEl = document.getElementById('calendarSelectedDateTotals');
    if (totalsEl) totalsEl.innerText = `Expense: -${currencySymbol}${dayExpense.toFixed(2)} | Income: +${currencySymbol}${dayIncome.toFixed(2)}`;

    const feed = document.getElementById('calendarDayFeed');
    if (!feed) return;
    feed.innerHTML = '';

    if (dayTxs.length === 0) {
        feed.innerHTML = `
            <div class="empty-state-card">
                <div class="empty-title">No spending on ${dateStr}</div>
                <div class="empty-sub">Tap "+ Add" above to log an expense or income for this day.</div>
            </div>
        `;
    } else {
        dayTxs.forEach(t => {
            feed.appendChild(createTxElement(t));
        });
    }

    if (window.lucide) window.lucide.createIcons();
}

// --- History Feed ---
function setHistoryFilter(filter, btn) {
    historyFilter = filter;
    document.querySelectorAll('.filter-pill').forEach(p => p.classList.remove('active'));
    if (btn) btn.classList.add('active');
    renderHistoryFeed();
}

function filterHistory() {
    renderHistoryFeed();
}

function renderHistoryFeed() {
    const list = document.getElementById('historyFeedList');
    if (!list) return;
    const query = (document.getElementById('txSearchInput')?.value || '').toLowerCase();
    list.innerHTML = '';

    let filtered = transactions.filter(t => {
        const matchesQuery = (t.title || '').toLowerCase().includes(query) ||
            (t.category || '').toLowerCase().includes(query) ||
            (t.payment || '').toLowerCase().includes(query);
        
        if (!matchesQuery) return false;

        if (historyFilter === 'EXPENSE') return !t.isIncome;
        if (historyFilter === 'INCOME') return t.isIncome;
        return true;
    });

    if (filtered.length === 0) {
        list.innerHTML = `
            <div class="empty-state-card">
                <div class="empty-title">${query ? 'No matching transactions' : 'No transactions recorded'}</div>
                <div class="empty-sub">${query ? 'Try searching for something else.' : 'Your full transaction history will show here.'}</div>
            </div>
        `;
        if (window.lucide) window.lucide.createIcons();
        return;
    }

    filtered.forEach(t => {
        list.appendChild(createTxElement(t));
    });

    if (window.lucide) window.lucide.createIcons();
}

// --- Profile & Logout Modal ---
function openProfileModal() {
    const modal = document.getElementById('profileModal');
    if (!modal) return;
    modal.classList.add('active');

    const nameEl = document.getElementById('modalProfileName');
    const emailEl = document.getElementById('modalProfileEmail');
    const avatarEl = document.getElementById('modalProfileAvatar');
    const badgeEl = document.getElementById('modalProfileBadge');
    const authTextEl = document.getElementById('modalAuthActionText');

    if (currentUser) {
        nameEl.innerText = currentUser.displayName || 'Spendify User';
        emailEl.innerText = currentUser.email || '';
        avatarEl.innerText = (currentUser.displayName || currentUser.email || 'U')[0].toUpperCase();
        badgeEl.innerText = 'Cloud Account Active';
        badgeEl.style.background = 'rgba(16, 185, 129, 0.18)';
        badgeEl.style.color = 'var(--income-green)';
        if (authTextEl) authTextEl.innerText = 'Switch Account';
    } else {
        nameEl.innerText = 'Guest User';
        emailEl.innerText = 'guest@spendify.app';
        avatarEl.innerText = 'G';
        badgeEl.innerText = 'Guest Session';
        badgeEl.style.background = 'rgba(139, 92, 246, 0.18)';
        badgeEl.style.color = 'var(--primary-light)';
        if (authTextEl) authTextEl.innerText = 'Sign In / Sign Up';
    }

    let totalIncome = 0;
    let totalExpense = 0;
    transactions.forEach(t => {
        if (t.isIncome) totalIncome += t.amount;
        else totalExpense += t.amount;
    });

    document.getElementById('profileStatTxs').innerText = transactions.length.toString();
    document.getElementById('profileStatSpent').innerText = `-${currencySymbol}${totalExpense.toFixed(2)}`;
    document.getElementById('profileStatIncome').innerText = `+${currencySymbol}${totalIncome.toFixed(2)}`;

    if (window.lucide) window.lucide.createIcons();
}

function closeProfileModal() {
    const modal = document.getElementById('profileModal');
    if (modal) modal.classList.remove('active');
}

function signOutUser() {
    if (confirm('Are you sure you want to log out from Spendify?')) {
        currentUser = null;
        localStorage.removeItem('spendify_user');
        closeProfileModal();
        saveState();
        navigateTo('dashboard');
        alert('You have logged out successfully.');
    }
}

// --- Transaction Modal ---
function openAddModal() {
    const modal = document.getElementById('addTxModal');
    if (!modal) return;
    modal.classList.add('active');
    populateCategoryGrid();
    if (window.lucide) window.lucide.createIcons();
}

function closeAddModal() {
    const modal = document.getElementById('addTxModal');
    if (modal) modal.classList.remove('active');
}

function setTxType(type) {
    currentTxType = type;
    const expenseBtn = document.getElementById('typeExpenseBtn');
    const incomeBtn = document.getElementById('typeIncomeBtn');
    const title = document.getElementById('addTxTitle');

    if (type === 'EXPENSE') {
        expenseBtn.className = 'type-btn active expense';
        incomeBtn.className = 'type-btn';
        title.innerText = 'Add Expense';
    } else {
        expenseBtn.className = 'type-btn';
        incomeBtn.className = 'type-btn active income';
        title.innerText = 'Add Income';
    }
    populateCategoryGrid();
}

function populateCategoryGrid() {
    const grid = document.getElementById('modalCategoryGrid');
    if (!grid) return;
    grid.innerHTML = '';

    const cats = CATEGORIES.filter(c => currentTxType === 'INCOME' ? c.income : !c.income);
    if (!cats.some(c => c.name === selectedCategory)) {
        selectedCategory = cats[0]?.name || 'General';
    }

    cats.forEach(c => {
        const item = document.createElement('div');
        item.className = `cat-grid-item ${c.name === selectedCategory ? 'selected' : ''}`;
        item.innerHTML = `<i data-lucide="${c.icon}"></i> <span>${c.name}</span>`;
        item.onclick = () => {
            selectedCategory = c.name;
            populateCategoryGrid();
        };
        grid.appendChild(item);
    });

    if (window.lucide) window.lucide.createIcons();
}

function selectPayment(el, method) {
    document.querySelectorAll('.pay-pill').forEach(p => p.classList.remove('selected'));
    el.classList.add('selected');
    selectedPaymentMethod = method;
}

function saveTransaction() {
    const amountInput = document.getElementById('inputAmount');
    const amount = parseFloat(amountInput.value);
    const note = document.getElementById('inputNote').value.trim();

    if (!amount || amount <= 0) {
        alert('Please enter a valid amount greater than 0.');
        return;
    }

    const catObj = CATEGORIES.find(c => c.name === selectedCategory);

    // Save with selected calendar date or today
    const dateObj = selectedCalendarDate || new Date();
    const dateStr = dateObj.toLocaleDateString('en-US', { month: 'short', day: 'numeric', year: 'numeric' });

    const newTx = {
        id: 'tx_' + Date.now(),
        title: note || selectedCategory,
        amount: amount,
        isIncome: currentTxType === 'INCOME',
        category: selectedCategory,
        payment: selectedPaymentMethod,
        icon: catObj ? catObj.icon : 'receipt',
        date: dateStr,
        timestamp: dateObj.getTime(),
        syncStatus: 'SYNCED'
    };

    transactions.unshift(newTx);
    amountInput.value = '';
    document.getElementById('inputNote').value = '';

    closeAddModal();
    saveState();
}

// --- Charts & Reports ---
function renderCharts() {
    // Donut Chart
    const svg = document.getElementById('donutChartSvg');
    const legend = document.getElementById('chartLegendList');
    if (!svg || !legend) return;

    svg.innerHTML = '';
    legend.innerHTML = '';

    const expenseTxs = transactions.filter(t => !t.isIncome);
    const totalExpense = expenseTxs.reduce((sum, t) => sum + t.amount, 0);

    if (totalExpense === 0) {
        svg.innerHTML = `<circle cx="110" cy="110" r="70" fill="none" stroke="rgba(255,255,255,0.08)" stroke-width="28" />
        <text x="110" y="115" text-anchor="middle" fill="#64748b" font-size="13">No Expenses</text>`;
        legend.innerHTML = '<div style="color: var(--text-muted); font-size: 13px; text-align: center;">Log expenses to see breakdown</div>';
        return;
    }

    const catTotals = {};
    expenseTxs.forEach(t => {
        catTotals[t.category] = (catTotals[t.category] || 0) + t.amount;
    });

    const colors = ['#8b5cf6', '#3b82f6', '#10b981', '#f59e0b', '#ec4899', '#06b6d4'];
    let colorIdx = 0;
    let accumulatedAngle = 0;

    for (const [cat, total] of Object.entries(catTotals)) {
        const percent = total / totalExpense;
        const strokeDash = percent * (2 * Math.PI * 70);
        const strokeOffset = -accumulatedAngle * (2 * Math.PI * 70);
        const color = colors[colorIdx % colors.length];

        const circle = document.createElementNS('http://www.w3.org/2000/svg', 'circle');
        circle.setAttribute('cx', '110');
        circle.setAttribute('cy', '110');
        circle.setAttribute('r', '70');
        circle.setAttribute('fill', 'none');
        circle.setAttribute('stroke', color);
        circle.setAttribute('stroke-width', '28');
        circle.setAttribute('stroke-dasharray', `${strokeDash} ${(2 * Math.PI * 70) - strokeDash}`);
        circle.setAttribute('stroke-dashoffset', strokeOffset.toString());
        circle.style.transform = 'rotate(-90deg)';
        circle.style.transformOrigin = '110px 110px';
        svg.appendChild(circle);

        accumulatedAngle += percent;

        // Legend item
        const leg = document.createElement('div');
        leg.className = 'chart-legend-item';
        leg.innerHTML = `
            <div style="display: flex; align-items: center; gap: 8px;">
                <span class="legend-color-dot" style="background: ${color};"></span>
                <span>${cat}</span>
            </div>
            <span style="font-weight: 700; color: var(--text-primary);">${currencySymbol}${total.toFixed(2)} (${Math.round(percent * 100)}%)</span>
        `;
        legend.appendChild(leg);

        colorIdx++;
    }
}

// --- Budgets ---
function renderBudgets() {
    const bar = document.getElementById('overallBudgetBar');
    const percentEl = document.getElementById('overallBudgetPercent');
    const detailsEl = document.getElementById('overallBudgetDetails');
    if (!bar) return;

    const totalExpense = transactions.filter(t => !t.isIncome).reduce((sum, t) => sum + t.amount, 0);

    if (monthlyBudgetLimit > 0) {
        const percent = Math.min(100, Math.round((totalExpense / monthlyBudgetLimit) * 100));
        const remaining = Math.max(0, monthlyBudgetLimit - totalExpense);
        percentEl.innerText = `${percent}% Used`;
        bar.style.width = `${percent}%`;
        detailsEl.innerText = `${currencySymbol}${remaining.toFixed(2)} left of ${currencySymbol}${monthlyBudgetLimit.toFixed(2)}`;
    } else {
        percentEl.innerText = 'No Limit';
        bar.style.width = '0%';
        detailsEl.innerText = 'Click "+ Set Limit" to configure your monthly budget';
    }
}

function openBudgetPrompt() {
    const input = prompt('Enter your Monthly Budget Limit:', monthlyBudgetLimit > 0 ? monthlyBudgetLimit.toString() : '1000');
    if (input !== null) {
        const val = parseFloat(input);
        if (!isNaN(val) && val >= 0) {
            monthlyBudgetLimit = val;
            saveState();
        }
    }
}

// --- Auth Modal (Strict Verification) ---
function openAuthModal(mode = 'SIGN_IN') {
    const modal = document.getElementById('authModal');
    if (!modal) return;
    modal.classList.add('active');
    setAuthMode(mode);
}

function closeAuthModal() {
    if (!currentUser) {
        alert('Please sign in or create an account first.');
        return;
    }
    const modal = document.getElementById('authModal');
    if (modal) modal.classList.remove('active');
}

function setAuthMode(mode) {
    authMode = mode;
    const signInBtn = document.getElementById('authModeSignInBtn');
    const signUpBtn = document.getElementById('authModeSignUpBtn');
    const signUpFields = document.getElementById('signUpFields');
    const confirmPasswordField = document.getElementById('confirmPasswordField');
    const submitBtn = document.getElementById('authSubmitBtn');
    const modalTitle = document.getElementById('authModalTitle');

    if (mode === 'SIGN_IN') {
        signInBtn.className = 'type-btn active expense';
        signUpBtn.className = 'type-btn';
        signUpFields.style.display = 'none';
        confirmPasswordField.style.display = 'none';
        submitBtn.innerText = 'Sign In to Spendify';
        modalTitle.innerText = 'Sign In';
    } else {
        signInBtn.className = 'type-btn';
        signUpBtn.className = 'type-btn active income';
        signUpFields.style.display = 'block';
        confirmPasswordField.style.display = 'block';
        submitBtn.innerText = 'Create Account';
        modalTitle.innerText = 'Sign Up';
    }
}

function submitAuthForm() {
    const email = document.getElementById('authEmail').value.trim();
    const password = document.getElementById('authPassword').value;
    const registeredAccounts = JSON.parse(localStorage.getItem('spendify_registered_accounts')) || {};

    if (!email || !email.includes('@')) {
        alert('Please enter a valid email address.');
        return;
    }

    if (authMode === 'SIGN_UP') {
        const fullName = document.getElementById('authFullName').value.trim();
        const confirmPassword = document.getElementById('authConfirmPassword').value;

        if (!fullName || fullName.length < 2) {
            alert('Please enter your full name (at least 2 characters).');
            return;
        }

        if (password.length < 6) {
            alert('Password must be at least 6 characters.');
            return;
        }

        if (password !== confirmPassword) {
            alert('Passwords do not match.');
            return;
        }

        if (registeredAccounts[email]) {
            alert(`An account with email '${email}' already exists. Please switch to Sign In.`);
            return;
        }

        registeredAccounts[email] = {
            userId: 'user_' + Date.now(),
            email: email,
            displayName: fullName,
            password: password,
            createdAt: new Date().toISOString()
        };
        localStorage.setItem('spendify_registered_accounts', JSON.stringify(registeredAccounts));

        currentUser = {
            userId: registeredAccounts[email].userId,
            email: email,
            displayName: fullName
        };
    } else {
        // Sign In Verification
        if (!registeredAccounts[email]) {
            alert(`No account found with '${email}'. Please switch to Sign Up to create your account.`);
            return;
        }

        if (registeredAccounts[email].password !== password) {
            alert('Incorrect password. Please check and try again.');
            return;
        }

        currentUser = {
            userId: registeredAccounts[email].userId,
            email: email,
            displayName: registeredAccounts[email].displayName
        };
    }

    localStorage.setItem('spendify_user', JSON.stringify(currentUser));
    saveState();
    const modal = document.getElementById('authModal');
    if (modal) modal.classList.remove('active');
    alert(`Success! Logged in as ${currentUser.displayName || currentUser.email}.`);
}

// --- Profile Modal & Log Out ---
function openProfileModal() {
    if (!currentUser) {
        openAuthModal('SIGN_IN');
        return;
    }

    const modal = document.getElementById('profileModal');
    if (!modal) return;

    const initial = (currentUser.displayName || currentUser.email || 'U').charAt(0).toUpperCase();

    const avatarLarge = document.getElementById('modalUserAvatarLarge');
    const nameEl = document.getElementById('modalUserName');
    const emailEl = document.getElementById('modalUserEmail');

    if (avatarLarge) avatarLarge.innerText = initial;
    if (nameEl) nameEl.innerText = currentUser.displayName || 'Spendify User';
    if (emailEl) emailEl.innerText = currentUser.email || '';

    // Calculate live metrics
    let totalIncome = 0;
    let totalExpense = 0;
    transactions.forEach(t => {
        if (t.isIncome) totalIncome += t.amount;
        else totalExpense += t.amount;
    });

    const txCountEl = document.getElementById('profileTxCount');
    const spentEl = document.getElementById('profileTotalSpent');
    const incomeEl = document.getElementById('profileTotalIncome');

    if (txCountEl) txCountEl.innerText = transactions.length;
    if (spentEl) spentEl.innerText = `${currencySymbol}${totalExpense.toFixed(2)}`;
    if (incomeEl) incomeEl.innerText = `${currencySymbol}${totalIncome.toFixed(2)}`;

    modal.classList.add('active');
    if (window.lucide) window.lucide.createIcons();
}

function closeProfileModal() {
    const modal = document.getElementById('profileModal');
    if (modal) modal.classList.remove('active');
}

function signOutUser() {
    if (confirm('Are you sure you want to log out from Spendify?')) {
        currentUser = null;
        localStorage.removeItem('spendify_user');
        closeProfileModal();
        saveState();
        openAuthModal('SIGN_IN');
        alert('You have been logged out successfully.');
    }
}

function promptChangeCurrency() {
    const code = prompt('Enter Currency Code (USD, EUR, INR, GBP, JPY):', currencyCode);
    if (code) {
        const symbolMap = { 'USD': '$', 'EUR': '€', 'INR': '₹', 'GBP': '£', 'JPY': '¥' };
        currencyCode = code.toUpperCase();
        currencySymbol = symbolMap[currencyCode] || '$';
        saveState();
    }
}

function clearAllData() {
    if (confirm('Are you sure you want to clear all data and reset to 0?')) {
        transactions = [];
        monthlyBudgetLimit = 0;
        localStorage.removeItem('spendify_tx');
        localStorage.removeItem('spendify_budget');
        saveState();
        alert('All data has been reset.');
    }
}

function exportCSV() {
    if (transactions.length === 0) {
        alert('No transactions to export.');
        return;
    }

    let csv = 'ID,Title,Amount,Type,Category,Payment,Date\n';
    transactions.forEach(t => {
        csv += `"${t.id}","${t.title}",${t.amount},"${t.isIncome ? 'INCOME' : 'EXPENSE'}","${t.category}","${t.payment}","${t.date}"\n`;
    });

    const blob = new Blob([csv], { type: 'text/csv' });
    const url = URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url;
    a.download = `Spendify_Export_${new Date().toISOString().slice(0,10)}.csv`;
    a.click();
}

function escapeHtml(str) {
    return (str || '').replace(/&/g, '&amp;').replace(/</g, '&lt;').replace(/>/g, '&gt;').replace(/"/g, '&quot;');
}

document.addEventListener('DOMContentLoaded', () => {
    saveState();
    if (window.lucide) {
        window.lucide.createIcons();
    }
    if (!currentUser) {
        openAuthModal('SIGN_IN');
    }
});
