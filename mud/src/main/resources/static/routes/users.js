// routes/users.js

const express = require('express');
const router = express.Router();
const db = require('../db'); // db.js 경로 맞게 확인

// GET /users/check-duplicate?userId=xxx
router.get('/check-duplicate', async (req, res) => {
    const { userId } = req.query;

    if (!userId) {
        return res.status(400).json({ message: 'userId 쿼리 파라미터가 필요합니다.' });
    }

    try {
        const [rows] = await db.query('SELECT * FROM users WHERE user_id = ?', [userId]);

        const isDuplicate = rows.length > 0; // 'duplicate' -> 'isDuplicate'
        res.json({ isDuplicate }); // 'duplicate' -> 'isDuplicate'
    } catch (error) {
        console.error('DB 오류:', error);
        res.status(500).json({ message: '서버 오류가 발생했습니다.' });
    }
});

module.exports = router;
