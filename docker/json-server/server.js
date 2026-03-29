const jsonServer = require('json-server');
const multer = require('multer');
const path = require('path');
const fs = require('fs');

const server = jsonServer.create();
const router = jsonServer.router('db.json');
const middlewares = jsonServer.defaults({ static: './public' });

const uploadDir = path.join(__dirname, 'public', 'uploads');
fs.mkdirSync(uploadDir, { recursive: true });

const storage = multer.diskStorage({
    destination: (_req, _file, cb) => cb(null, uploadDir),
    filename: (_req, file, cb) => {
        const uniqueName = Date.now() + '-' + file.originalname;
        cb(null, uniqueName);
    }
});
const upload = multer({ storage });

server.use(middlewares);
server.use(jsonServer.bodyParser);

// Upload endpoint — accepte un champ "file" en multipart
server.post('/upload', upload.single('file'), (req, res) => {
    if (!req.file) {
        return res.status(400).json({ error: 'No file uploaded' });
    }
    res.status(201).json({
        id: Date.now(),
        filename: req.file.filename,
        originalName: req.file.originalname,
        mimeType: req.file.mimetype,
        size: req.file.size,
        url: '/uploads/' + req.file.filename
    });
});

server.use(router);

const port = 3000;
server.listen(port, '0.0.0.0', () => {
    console.log(`JSON Server + upload running on http://0.0.0.0:${port}`);
});
