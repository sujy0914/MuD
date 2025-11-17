const express = require('express');
const path = require('path');

const app = express();
const port = 3000;

const userRouter = require('./routes/users');

// HTML, CSS 파일이 있는 public 폴더 설정
app.use(express.static(path.join(__dirname, 'public')));

// 기본 route
app.get('/', (req, res) => {
  res.sendFile(path.join(__dirname, 'public', 'main.html'));
});

app.use('/api/users', userRouter);


app.listen(port, () => {
  console.log(`Server is running on http://localhost:${port}`);
});
