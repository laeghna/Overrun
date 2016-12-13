const http = require('http');
const fs = require('fs');
const bcrypt = require('bcrypt');

const express = require('express');
const app = express();

const config = require("./overrun");

// MySQL config
const mysql = require('mysql');
const db = new mysql.createConnection(config.mysqlConfig);


// express config
const PORT = process.env.PORT || 8081;
app.set("title", "Overrun");


// body-parser config
const bp = require('body-parser');
app.use(bp.json());
app.use(bp.urlencoded({ extended: true }));

const routes = require('./routes')(db);
app.use(routes);


app.use(escapeCharMiddleware);


const server = app.listen(PORT, () => {
    console.log("Server listening on : http://localhost:%s", PORT);
});

module.exports = server;

// prevents SQL injection
function escapeCharMiddleware(req, res, next) {
    if (req.query) {
        req.query = escapeProps(req.query);
    }

    // keep executing the router middleware
    next()
}

// escapes chars to prevent SQL injection
function escapeProps(obj) {
    for (let prop in Object.getOwnPropertyNames(obj)) {
        obj[prop] = mysql.escape(prop);
    }
    return obj;
}
