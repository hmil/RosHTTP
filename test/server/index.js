var express = require('express');
var app = express();
var morgan = require('morgan');
var querystring = require('querystring');
var bodyParser = require('body-parser');

app.use(morgan('combined'));

app.use(bodyParser.raw({
  type: '*/*'
}));

app.get('/status/:statusCode', function(req, res) {
  res.set('X-Status-Code', req.params.statusCode);
  res.sendStatus(req.params.statusCode);
});

app.get('/redirect/temporary/*', function(req, res) {
  res.statusCode = 302;
  res.append('Location', '/' + req.params[0]);
  res.write("redirecting...");
  res.end();
});

app.get('/redirect/permanent/*', function(req, res) {
  res.redirect(301 ,'/' + req.params[0]);
});


app.get('/', function (req, res) {
  res.send('Hello World!');
});

app.get('/echo/:text', function(req, res) {
  res.set('Content-Type', 'text/plain');
  res.send(req.params.text);
});

app.get('/query', function(req, res) {
  var url = req.url;
  var qPos = url.indexOf('?') + 1;
  if (qPos > 0) {
    var hPos = url.substr(qPos).indexOf('#');
    var query = url.substring(qPos, hPos < 0 ? undefined : hPos);
    res.send(querystring.unescape(query));
  } else {
    res.send('');
  }
});

app.get('/query/parsed', function(req, res) {
  res.send(req.query);
});

app.get('/headers', function(req, res) {
  res.set('Content-Type', 'text/plain');
  res.send(JSON.stringify(req.headers).replace(/"(?!\\),/g, '"\n').replace(/(^{|}$)/g, ''));
});

app.all('/method', function(req, res) {
  res.set('X-Request-Method', req.method);
  res.send(req.method);
});

app.all('/body', function(req, res) {
  if (!req.headers.hasOwnProperty('content-type')) {
    res.status(400).send("No request body!");
  } else if(req.body.length === 0) {
    res.status(400).send("Empty request body!");
  } else {
    res.set('Content-Type', req.headers['content-type']);
    res.send(req.body);
  }
});

app.use('/runtime', express.static('runtime'));

app.listen(3000, function () {
  console.log('Test server listening on port 3000.');
});
