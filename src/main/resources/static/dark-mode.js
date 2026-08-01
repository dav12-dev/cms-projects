// ============================================================
// Dark Mode Toggle
// ============================================================

(function() {
    // Check for saved theme preference
    const savedTheme = localStorage.getItem('theme');
    const prefersDark = window.matchMedia('(prefers-color-scheme: dark)').matches;

    // Set initial theme
    if (savedTheme) {
        document.documentElement.setAttribute('data-theme', savedTheme);
    } else if (prefersDark) {
        document.documentElement.setAttribute('data-theme', 'dark');
        localStorage.setItem('theme', 'dark');
    } else {
        document.documentElement.setAttribute('data-theme', 'light');
        localStorage.setItem('theme', 'light');
    }

    // Create toggle button and add to top bar
    function addDarkModeToggle() {
        // Try different selectors for top bar
        let topBar = document.querySelector('.top-bar .user-info');
        if (!topBar) {
            topBar = document.querySelector('.top-bar');
        }
        if (!topBar) {
            // If no top bar found, create a simple toggle floating
            const body = document.body;
            const toggle = document.createElement('button');
            toggle.id = 'darkModeToggle';
            toggle.style.cssText = `
                position: fixed; bottom: 20px; right: 20px; z-index: 9999;
                background: rgba(255,255,255,0.15);
                border: 1px solid rgba(255,255,255,0.2);
                padding: 12px 18px;
                border-radius: 50px;
                color: white;
                cursor: pointer;
                backdrop-filter: blur(10px);
                font-size: 20px;
            `;
            const currentTheme = document.documentElement.getAttribute('data-theme') || 'light';
            toggle.innerHTML = currentTheme === 'dark' ? '☀️' : '🌙';
            toggle.addEventListener('click', toggleTheme);
            body.appendChild(toggle);
            return;
        }

        const currentTheme = document.documentElement.getAttribute('data-theme') || 'light';
        const icon = currentTheme === 'dark' ? 'fa-sun' : 'fa-moon';
        const label = currentTheme === 'dark' ? 'Light' : 'Dark';

        const toggleHtml = `
            <button id="darkModeToggle" style="background:rgba(255,255,255,0.1);border:none;padding:6px 15px;border-radius:30px;color:white;cursor:pointer;transition:all 0.3s ease;font-size:14px;">
                <i class="fas ${icon}"></i> ${label}
            </button>
        `;
        topBar.insertAdjacentHTML('afterbegin', toggleHtml);

        document.getElementById('darkModeToggle').addEventListener('click', toggleTheme);
    }

    function toggleTheme() {
        const current = document.documentElement.getAttribute('data-theme');
        const newTheme = current === 'dark' ? 'light' : 'dark';
        document.documentElement.setAttribute('data-theme', newTheme);
        localStorage.setItem('theme', newTheme);
        // Update button appearance
        const btn = document.getElementById('darkModeToggle');
        if (btn) {
            const icon = btn.querySelector('i');
            if (icon) {
                icon.className = newTheme === 'dark' ? 'fas fa-sun' : 'fas fa-moon';
            }
            const text = btn.textContent.trim();
            if (text) {
                btn.innerHTML = newTheme === 'dark' ? '<i class="fas fa-sun"></i> Light' : '<i class="fas fa-moon"></i> Dark';
            }
        }
    }

    // Add toggle when DOM is ready
    if (document.readyState === 'loading') {
        document.addEventListener('DOMContentLoaded', addDarkModeToggle);
    } else {
        addDarkModeToggle();
    }
})();