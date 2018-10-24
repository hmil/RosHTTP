var express = require('express');
var app = express();
var morgan = require('morgan');
var querystring = require('querystring');
var bodyParser = require('body-parser');
var multipart = require('connect-multiparty');
var fs = require('fs');
var path = require('path');
var cookieParser = require('cookie-parser')
var cors = require('cors')

app.use(morgan('combined'));
app.use(cookieParser())
app.use(cors({credentials: true, origin: true}))
app.use(multipart());

// parse application/x-www-form-urlencoded
app.use(bodyParser.urlencoded({ extended: false }));

// parse application/json
app.use(bodyParser.json());

// parse all other types
app.use(bodyParser.raw({
  type: 'text/plain'
}));

// Profiling middleware
app.use(function(req, res, next) {
  var beginTime = Date.now();
  var end = res.end;
  res.end = function() {
    var endTime = Date.now();
    console.log("Spent " + (endTime - beginTime) + "ms on " + req.path);
    end.apply(this, arguments);
  };
  next();
});

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

app.get('/raw_greeting', function(req, res) {
  res.write("Hello world").end(); // Does not send Content-Type header
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

app.get('/echo_repeat/:text', function(req, res) {
  res.set('Content-Type', 'text/plain');
  var repeat = req.query.repeat || 1;
  var delay = req.query.delay || 1000;
  function sendOne() {
    res.write(req.params.text);
    repeat--;
    if (repeat > 0) {
      setTimeout(sendOne, delay);
    } else {
      res.end();
    }
  }
  sendOne();
});

app.get('/multibyte_string', function(req, res) {
  res.send("12\uD83D\uDCA978");
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

app.all('/headers', function(req, res) {
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

app.all('/empty_body/:statusCode', function(req, res) {
  res.set('X-Status-Code', req.params.statusCode);
  res.status(req.params.statusCode).send('');
});

app.post('/compare/:name', function(req, res) {
  bodyParser.raw()(req, res, function(err) {
    var fdata = fs.readFileSync(path.join(__dirname, "../resources", req.params.name));

    if (fdata.compare(req.body) == 0) {
      res.status(200).send('Ok');
    } else {
      res.status(400).send(req.body);
    }
  });
});

function logOverwrite(line) {
  process.stdout.clearLine();  // clear current text
  process.stdout.cursorTo(0);  // move cursor to beginning of line
  process.stdout.write(line);
}

app.post('/streams/in', function(req, res) {
  var count = 0;
  req.on('data', function(chunk) {
    var prev = count;
    count += chunk.length
    if (prev % 16777216 > count % 16777216) {
      logOverwrite("Upload stream : " + count + " loaded");
    }
  });
  req.on('end', function() {
    process.stdout.write("\n");
    res.send('Received ' + count + ' bytes.');
  })
});

app.get('/send_cookie', function(req, res) {
  if (req.cookies === undefined || Object.keys(req.cookies).length === 0) {
    res.cookie('connected', 'blue');
	res.status(200).send('New cookie created. Returning it now.');
  } else {
	var connected = req.cookies['connected'];
	res.status(200).send('Cookie already existed: ', connected, '.',
	                     '(This is OK.) Returning it now.');
  }
});

app.get('/receive_cookie', function(req, res) {
  var connected = req.cookies['connected'];

  if (connected === undefined) {
    res.status(500).send('Cookie not received!');
  } else {
    if (connected === 'blue') {
      res.status(200).send('Cookie received.')
    } else {
      res.status(500).send('A cookie received, but data was wrong!')
    }
  }
});

var ONE_MILLION = 1000000;
app.get('/streams/out', function(req, res) {
  var buff = Buffer.alloc(8192, 97);
  var remaining = ONE_MILLION;
  var increment = remaining / 100;
  res.set('Content-Type', 'application/octet-stream');
  res.set('Transfer-Encoding', 'chunked');

  function niceWrite() {
    while (remaining > 0) {
      remaining --;
      if (remaining % increment === 0) {
        logOverwrite("Download stream: " + remaining + " chunks to go.")
      }
      if (res.write(buff) === false) {
        res.once('drain', niceWrite);
        return;
      }
    }
    process.stdout.write("\n");
    res.end();
  }
  niceWrite();
});

app.use('/resources', express.static('../resources'));
app.use('/runtime', express.static('runtime'));

app.listen(3000, function () {
  console.log('Test server listening on port 3000.');
});
