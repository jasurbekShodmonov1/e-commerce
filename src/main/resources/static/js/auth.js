// Spring Security bilan xavfsiz gaplashuvchi universal Wrapper
async function secureFetch(url, options = {}) {
    // 1. Agar headers bo'lmasa, obyekt ochamiz
    options.headers = options.headers || {};

    // 2. LocalStorage'dan tokenni olamiz va Header'ga Bearer sifatida qo'shamiz
    const token = localStorage.getItem('accessToken');
    if (token) {
        options.headers['Authorization'] = `Bearer ${token}`;
    }

    try {
        let response = await fetch(url, options);

        // 3. Agar Spring Security 401 (Unauthorized) qaytarsa - token eskirgan bo'lishi mumkin!
        if (response.status === 401) {
            console.warn("Access Token eskirgan, yangilashga urunib ko'ramiz...");

            // Tokenni yangilashga so'rov yuboramiz (/api/auth/refresh ochiq yo'l)
            const newAccessToken = await refreshAccessToken();

            if (newAccessToken) {
                // Yangi tokenni eski so'rovga bog'lab, qaytadan yuboramiz
                options.headers['Authorization'] = `Bearer ${newAccessToken}`;
                response = await fetch(url, options);
            } else {
                // Agar refresh token ham o'tmagan bo'lsa, sessiya tugagan - majburiy logout
                alert("Sessiya muddati tugadi. Iltimos, qayta tizimga kiring!");
                handleLogout();
                return null;
            }
        }
        return response;
    } catch (error) {
        console.error("Tarmoq xatoligi:", error);
        throw error;
    }
}

// Tokenni yangilash funksiyasi
async function refreshAccessToken() {
    const currentRefreshToken = localStorage.getItem('refreshToken');
    if (!currentRefreshToken) return null;

    try {
        const response = await fetch('/api/auth/refresh', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ refreshToken: currentRefreshToken })
        });

        if (response.ok) {
            const data = await response.json(); // TokenResponse
            localStorage.setItem('accessToken', data.accessToken);
            if (data.refreshToken) {
                localStorage.setItem('refreshToken', data.refreshToken);
            }
            return data.accessToken;
        }
    } catch (e) {
        console.error("Refresh token xatosi:", e);
    }
    return null;
}

// Tizimdan chiqish funksiyasi
async function handleLogout() {
    const currentRefreshToken = localStorage.getItem('refreshToken');
    if (currentRefreshToken) {
        try {
            // /api/auth/logout ham ochiq yo'l, lekin joriy tokenni o'chirishi uchun yuboramiz
            await fetch('/api/auth/logout', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ refreshToken: currentRefreshToken })
            });
        } catch (e) { console.error(e); }
    }
    localStorage.clear();
    window.location.href = '/login.html';
}