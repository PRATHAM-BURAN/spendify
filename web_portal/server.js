const http = require('http');
const fs = require('fs');
const path = require('path');

const PORT = process.env.PORT || 8088;
const APK_PATH = path.resolve(__dirname, '..', 'app', 'build', 'outputs', 'apk', 'debug', 'app-debug.apk');
const PUBLIC_DIR = __dirname;

const MIME_TYPES = {
    '.html': 'text/html',
    '.css': 'text/css',
    '.js': 'application/javascript',
    '.json': 'application/json',
    '.png': 'image/png',
    '.jpg': 'image/jpeg',
    '.svg': 'image/svg+xml',
    '.apk': 'application/vnd.android.package-archive'
};

const server = http.createServer((req, res) => {
    console.log(`[${new Date().toLocaleTimeString()}] ${req.method} ${req.url}`);
    
    // Enable CORS
    res.setHeader('Access-Control-Allow-Origin', '*');
    res.setHeader('Access-Control-Allow-Methods', 'GET, POST, OPTIONS');
    res.setHeader('Access-Control-Allow-Headers', 'Content-Type');
    
    if (req.method === 'OPTIONS') {
        res.writeHead(204);
        res.end();
        return;
    }

    const cleanUrl = req.url.split('?')[0];

    // Serve APK download
    if (cleanUrl === '/Spendify.apk' || cleanUrl === '/app-debug.apk' || cleanUrl === '/download-apk') {
        if (fs.existsSync(APK_PATH)) {
            const stat = fs.statSync(APK_PATH);
            res.writeHead(200, {
                'Content-Type': 'application/vnd.android.package-archive',
                'Content-Length': stat.size,
                'Content-Disposition': 'attachment; filename="Spendify.apk"',
                'Cache-Control': 'no-cache'
            });
            const readStream = fs.createReadStream(APK_PATH);
            readStream.pipe(res);
        } else {
            res.writeHead(404, { 'Content-Type': 'text/plain' });
            res.end('APK not found. Please build it first with .\\gradlew.bat assembleDebug');
        }
        return;
    }

    // Serve static files
    let filePath = path.join(PUBLIC_DIR, cleanUrl === '/' ? 'index.html' : cleanUrl);
    
    // Security check to prevent directory traversal
    if (!filePath.startsWith(PUBLIC_DIR)) {
        res.writeHead(403, { 'Content-Type': 'text/plain' });
        res.end('Forbidden');
        return;
    }

    fs.stat(filePath, (err, stats) => {
        if (err || !stats.isFile()) {
            // Fallback to index.html for SPA
            filePath = path.join(PUBLIC_DIR, 'index.html');
        }

        const ext = path.extname(filePath).toLowerCase();
        const contentType = MIME_TYPES[ext] || 'application/octet-stream';

        fs.readFile(filePath, (readErr, content) => {
            if (readErr) {
                res.writeHead(500, { 'Content-Type': 'text/plain' });
                res.end('Server Error');
            } else {
                res.writeHead(200, { 'Content-Type': contentType });
                res.end(content);
            }
        });
    });
});

server.listen(PORT, '0.0.0.0', () => {
    console.log(`====================================================`);
    console.log(` Spendify Local Browser & Wi-Fi Server is running!`);
    console.log(` - Local URL:   http://localhost:${PORT}`);
    console.log(` - Wi-Fi URL:   http://10.123.179.249:${PORT}`);
    console.log(` - Direct APK:  http://10.123.179.249:${PORT}/Spendify.apk`);
    console.log(`====================================================`);
});
