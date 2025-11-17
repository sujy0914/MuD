// db.js

const mysql = require('mysql2');

// 환경에 맞게 설정해 주세요
const pool = mysql.createPool({
    host: 'localhost',
    user: 'root',
    password: '0000',
    database: 'mud',  // 데이터베이스 이름
    waitForConnections: true,
    connectionLimit: 10,
    queueLimit: 0
});

module.exports = pool.promise(); // promise 형태로 사용
