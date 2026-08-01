// ============================================================
// Theme Switcher - Multiple Color Themes
// ============================================================

(function() {
    // Available themes
    const themes = {
        light: {
            '--bg-gradient-start': '#667eea',
            '--bg-gradient-end': '#764ba2',
            '--text-color': '#ffffff',
            '--card-bg': 'rgba(255, 255, 255, 0.15)',
            '--card-border': 'rgba(255, 255, 255, 0.2)',
            '--sidebar-bg': 'rgba(255, 255, 255, 0.15)',
            '--glass-card': 'rgba(255, 255, 255, 0.15)',
            '--stat-card-bg-1': 'rgba(102, 126, 234, 0.4)',
            '--stat-card-bg-2': 'rgba(46, 204, 113, 0.4)',
            '--stat-card-bg-3': 'rgba(241, 196, 15, 0.4)',
            '--stat-card-bg-4': 'rgba(231, 76, 60, 0.4)',
            '--stat-card-bg-5': 'rgba(52, 152, 219, 0.4)',
            '--stat-card-bg-6': 'rgba(155, 89, 182, 0.4)',
            '--brand-color': '#ffd700'
        },
        dark: {
            '--bg-gradient-start': '#0f0c29',
            '--bg-gradient-end': '#1a1a2e',
            '--text-color': '#e0e0e0',
            '--card-bg': 'rgba(255, 255, 255, 0.05)',
            '--card-border': 'rgba(255, 255, 255, 0.08)',
            '--sidebar-bg': 'rgba(0, 0, 0, 0.4)',
            '--glass-card': 'rgba(0, 0, 0, 0.4)',
            '--stat-card-bg-1': 'rgba(102, 126, 234, 0.2)',
            '--stat-card-bg-2': 'rgba(46, 204, 113, 0.2)',
            '--stat-card-bg-3': 'rgba(241, 196, 15, 0.2)',
            '--stat-card-bg-4': 'rgba(231, 76, 60, 0.2)',
            '--stat-card-bg-5': 'rgba(52, 152, 219, 0.2)',
            '--stat-card-bg-6': 'rgba(155, 89, 182, 0.2)',
            '--brand-color': '#ffd700'
        },
        blue: {
            '--bg-gradient-start': '#0c3483',
            '--bg-gradient-end': '#a2b6df',
            '--text-color': '#ffffff',
            '--card-bg': 'rgba(255, 255, 255, 0.1)',
            '--card-border': 'rgba(255, 255, 255, 0.15)',
            '--sidebar-bg': 'rgba(12, 52, 131, 0.5)',
            '--glass-card': 'rgba(12, 52, 131, 0.3)',
            '--stat-card-bg-1': 'rgba(12, 52, 131, 0.4)',
            '--stat-card-bg-2': 'rgba(46, 204, 113, 0.3)',
            '--stat-card-bg-3': 'rgba(241, 196, 15, 0.3)',
            '--stat-card-bg-4': 'rgba(231, 76, 60, 0.3)',
            '--stat-card-bg-5': 'rgba(52, 152, 219, 0.3)',
            '--stat-card-bg-6': 'rgba(155, 89, 182, 0.3)',
            '--brand-color': '#ffffff'
        },
        green: {
            '--bg-gradient-start': '#0c5234',
            '--bg-gradient-end': '#85c9a2',
            '--text-color': '#ffffff',
            '--card-bg': 'rgba(255, 255, 255, 0.1)',
            '--card-border': 'rgba(255, 255, 255, 0.15)',
            '--sidebar-bg': 'rgba(12, 82, 52, 0.5)',
            '--glass-card': 'rgba(12, 82, 52, 0.3)',
            '--stat-card-bg-1': 'rgba(12, 82, 52, 0.4)',
            '--stat-card-bg-2': 'rgba(46, 204, 113, 0.3)',
            '--stat-card-bg-3': 'rgba(241, 196, 15, 0.3)',
            '--stat-card-bg-4': 'rgba(231, 76, 60, 0.3)',
            '--stat-card-bg-5': 'rgba(52, 152, 219, 0.3)',
            '--stat-card-bg-6': 'rgba(155, 89, 182, 0.3)',
            '--brand-color': '#ffd700'
        },
        purple: {
            '--bg-gradient-start': '#4a0e78',
            '--bg-gradient-end': '#c084e8',
            '--text-color': '#ffffff',
            '--card-bg': 'rgba(255, 255, 255, 0.1)',
            '--card-border': 'rgba(255, 255, 255, 0.15)',
            '--sidebar-bg': 'rgba(74, 14, 120, 0.5)',
            '--glass-card': 'rgba(74, 14, 120, 0.3)',
            '--stat-card-bg-1': 'rgba(74, 14, 120, 0.4)',
            '--stat-card-bg-2': 'rgba(46, 204, 113, 0.3)',
            '--stat-card-bg-3': 'rgba(241, 196, 15, 0.3)',
            '--stat-card-bg-4': 'rgba(231, 76, 60, 0.3)',
            '--stat-card-bg-5': 'rgba(52, 152, 219, 0.3)',
            '--stat-card-bg-6': 'rgba(155, 89, 182, 0.3)',
            '--brand-color': '#ffd700'
        },
        sunset: {
            '--bg-gradient-start': '#f12711',
            '--bg-gradient-end': '#f5af19',
            '--text-color': '#ffffff',
            '--card-bg': 'rgba(255, 255, 255, 0.12)',
            '--card-border': 'rgba(255, 255, 255, 0.2)',
            '--sidebar-bg': 'rgba(241, 39, 17, 0.4)',
            '--glass-card': 'rgba(241, 39, 17, 0.25)',
            '--stat-card-bg-1': 'rgba(241, 39, 17, 0.4)',
            '--stat-card-bg-2': 'rgba(46, 204, 113, 0.3)',
            '--stat-card-bg-3': 'rgba(241, 196, 15, 0.3)',
            '--stat-card-bg-4': 'rgba(231, 76, 60, 0.3)',
            '--stat-card-bg-5': 'rgba(52, 152, 219, 0.3)',
            '--stat-card-bg-6': 'rgba(155, 89, 182, 0.3)',
            '--brand-color': '#ffffff'
        }
    };

    // Theme names for display
    const themeNames = {
        light: '☀️ Light',
        dark: '🌙 Dark',
        blue: '🔵 Blue',
        green: '🟢 Green',
        purple: '🟣 Purple',
        sunset: '🌅 Sunset'
    };

    // Save theme preference to server
    function saveThemePreference(theme) {
        fetch('/api/users/theme', {
            method: 'PUT',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ theme: theme })
        }).catch(() => console.log('Theme preference not saved to server'));
    }

    // Apply theme
    function applyTheme(theme) {
        const themeColors = themes[theme];
        if (!themeColors) return;

        for (const [key, value] of Object.entries(themeColors)) {
            document.documentElement.style.setProperty(key, value);
        }
        document.documentElement.setAttribute('data-theme', theme);
        localStorage.setItem('theme', theme);

        const themeBtn = document.getElementById('themeToggle');
        if (themeBtn) {
            themeBtn.innerHTML = `<i class="fas fa-palette"></i> ${themeNames[theme] || theme}`;
        }

        saveThemePreference(theme);
    }

    // Add theme switcher to top bar
    function addThemeSwitcher() {
        let topBar = document.querySelector('.top-bar .user-info');
        if (!topBar) {
            topBar = document.querySelector('.top-bar');
            if (!topBar) return;
        }

        const currentTheme = localStorage.getItem('theme') || 'light';

        const switcherHtml = `
            <div class="dropdown d-inline-block me-2">
                <button class="btn-glass" id="themeToggle" data-bs-toggle="dropdown" aria-expanded="false" style="background:rgba(255,255,255,0.1);border:none;padding:5px 12px;border-radius:30px;color:white;cursor:pointer;">
                    <i class="fas fa-palette"></i> ${themeNames[currentTheme] || 'Light'}
                </button>
                <ul class="dropdown-menu dropdown-menu-end" style="background:rgba(20,20,30,0.95);backdrop-filter:blur(20px);border:1px solid rgba(255,255,255,0.1);">
                    ${Object.keys(themes).map(key => `
                        <li>
                            <a class="dropdown-item theme-option ${key === currentTheme ? 'active' : ''}" 
                               href="#" data-theme="${key}" 
                               style="color:white;padding:8px 20px;border-radius:8px;${key === currentTheme ? 'background:rgba(255,255,255,0.1);' : ''}">
                                ${themeNames[key]}
                            </a>
                        </li>
                    `).join('')}
                </ul>
            </div>
        `;
        topBar.insertAdjacentHTML('afterbegin', switcherHtml);

        // Add event listeners
        document.querySelectorAll('.theme-option').forEach(item => {
            item.addEventListener('click', function(e) {
                e.preventDefault();
                const theme = this.dataset.theme;
                applyTheme(theme);
                // Update active state
                document.querySelectorAll('.theme-option').forEach(opt => opt.classList.remove('active'));
                this.classList.add('active');
            });
        });
    }

    // Load saved theme on page load
    function loadSavedTheme() {
        const saved = localStorage.getItem('theme');
        if (saved && themes[saved]) {
            applyTheme(saved);
        } else {
            // Check for system preference
            const prefersDark = window.matchMedia('(prefers-color-scheme: dark)').matches;
            if (prefersDark) {
                applyTheme('dark');
            } else {
                applyTheme('light');
            }
        }
    }

    // Initialize
    if (document.readyState === 'loading') {
        document.addEventListener('DOMContentLoaded', function() {
            loadSavedTheme();
            addThemeSwitcher();
        });
    } else {
        loadSavedTheme();
        addThemeSwitcher();
    }
})();