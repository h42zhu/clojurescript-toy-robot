

const http = require('http');
const path = require('path');
const sqlite3 = require('sqlite3').verbose();
const PORT = 3000;
const dbPath = path.resolve(__dirname, 'data.db');

const db = new sqlite3.Database(dbPath, sqlite3.OPEN_READWRITE | sqlite3.OPEN_CREATE, (err) => {
    if (err) {
        console.error('Error connecting to database:', err.message);
    } else {
        console.log('Connected to the SQLite database.');
        // Create the 'items' table if it doesn't already exist
        db.run(`
            CREATE TABLE IF NOT EXISTS robot_history (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                index_x INTEGER,
                index_y INTEGER
            )
        `, (createErr) => {
            if (createErr) {
                console.error('Error creating table:', createErr.message);
            } else {
                console.log('Table created or already exists.');
            }
        });
    }
});


const server = http.createServer((req, res) => {
    res.setHeader('Access-Control-Allow-Origin', '*');
    res.setHeader('Access-Control-Allow-Methods', 'GET, POST');
    res.setHeader('Access-Control-Allow-Headers', 'Content-Type');

    if (req.method === 'POST' && req.url === '/save') {
        let body = '';
        req.on('data', chunk => {
            body += chunk.toString(); // Accumulate data chunks
        });
        req.on('end', () => {
            try {
                const jsonData = JSON.parse(body);

                if (!jsonData || Object.keys(jsonData).length === 0) {
                    res.writeHead(400, { 'Content-Type': 'application/json' });
                    res.end(JSON.stringify({ error: 'Request body must be a non-empty JSON object.' }));
                    return;
                }

                const dataString = JSON.stringify(jsonData);
                const stmt = `INSERT INTO robot_history (index_x, index_y) VALUES (?, ?)`;

                db.run(stmt, [dataString], function(err) {
                    if (err) {
                        console.error('Error inserting data:', err.message);
                        res.writeHead(500, { 'Content-Type': 'application/json' });
                        res.end(JSON.stringify({ error: 'Failed to save data to database.' }));
                        return;
                    }
                    res.writeHead(201, { 'Content-Type': 'application/json' });
                    res.end(JSON.stringify({ message: 'Data saved successfully!', id: this.lastID }));
                });
            } catch (e) {
                console.error('Error parsing JSON or invalid data:', e.message);
                res.writeHead(400, { 'Content-Type': 'application/json' });
                res.end(JSON.stringify({ error: 'Invalid JSON in request body.' }));
            }
        });
    } else if (req.method === 'GET' && req.url === '/index') {
        const stmt = `SELECT id, index_x, index_y FROM items ORDER BY id DESC LIMIT 1`;

        db.get(stmt, [], (err, row) => {
            if (err) {
                console.error('Error retrieving data:', err.message);
                res.writeHead(500, { 'Content-Type': 'application/json' });
                res.end(JSON.stringify({ error: 'Failed to retrieve data from database.' }));
                return;
            }

            const parsedRow = {
                x: row.index_x,
                y: row.index_y,
            };

            res.writeHead(200, { 'Content-Type': 'application/json' });
            res.end(JSON.stringify(parsedRow));
        });
    }
});

// Start the HTTP server
server.listen(PORT, () => {
    console.log(`Server is running on http://localhost:${PORT}`);
});

// Gracefully close the database connection when the Node.js process exits
process.on('SIGINT', () => {
    db.close((err) => {
        if (err) {
            console.error(err.message);
        }
        console.log('Closed the database connection.');
        process.exit(0);
    });
});