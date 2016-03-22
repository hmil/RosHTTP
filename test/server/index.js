var express = require('express');
var app = express();


app.get('/status/:statusCode', function(req, res) {
  res.sendStatus(req.params.statusCode);
});

app.get('/redirect/temporary/:dest', function(req, res) {
  res.redirect('/' + req.params.dest);
});

app.get('/redirect/permanent/:dest', function(req, res) {
  res.redirect(301 ,'/' + req.params.dest);
});


app.get('/', function (req, res) {
  res.send('Hello World!');
});

app.get('/echo/:text', function(req, res) {
  res.set('Content-Type', 'text/plain');
  res.send(req.params.text);
})

app.use('/runtime', express.static('runtime'));

app.listen(3000, function () {
  console.log('Test server listening on port 3000.');
});
