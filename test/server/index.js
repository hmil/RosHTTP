var express = require('express');
var app = express();
var morgan = require('morgan');

app.use(morgan('combined'));

app.get('/status/:statusCode', function(req, res) {
  res.sendStatus(req.params.statusCode);
});

app.get('/redirect/temporary/*', function(req, res) {
  res.statusCode = 302;
  res.append('Location', '/' + req.param(0));
  res.write("redirecting...");
  res.end();
});

app.get('/redirect/permanent/*', function(req, res) {
  res.redirect(301 ,'/' + req.params(0));
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
