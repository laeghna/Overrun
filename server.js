var http = require('http');
var fs = require('fs');
var crypto = require('crypto');
var BigInteger = require('node-biginteger');
var config = fs.readFileSync("overrun.json");

var express = require('express');
var app = express();


var google = require('google-auth-library');
var OAuth2 = new google().OAuth2;

var CLIENT_ID = '711195024447-ltgs11u6ho1otmfsocoeql0le3e9n4hm.apps.googleusercontent.com';
var CLIENT_SECRET = 'yQqOP0KLnM6kM_cL2qoa8IqM';

var oauth2Client = new OAuth2(CLIENT_ID, CLIENT_SECRET);

// express config
const PORT = 8080;
const apiurl = 'https://cssgate.insttech.washington.edu:8080/';
app.set("title", "Overrun");
// require('./routes')(app, c);


// body-parser config
var bp = require('body-parser');
app.use(bp.json());
app.use(bp.urlencoded({ extended: true }));


// MySQL config
var Client = require('mariasql');

//var c = new Client(config.mysqlConfig);
var c = new Client({
    "host"    : "cssgate.insttech.washington.edu",
    "user"    : "earowell",
    "password": "azLats*",
    "db"      : "earowell"
});

// SQL prepared statements
var createUser = c.prepare('INSERT INTO User (email, pass) ' +
    'VALUES ( :email, :pass );');
var login = c.prepare('SELECT * FROM User WHERE email = :email' +
    ' AND pass = :pass;');
var getUser = c.prepare('SELECT * FROM User WHERE email = :email;');
var getUsers = c.prepare('SELECT email FROM User;');
var createGame = c.prepare('INSERT INTO Game (userId, score, zombiesKilled, level, shotsFirst)' +
    'VALUES ( :userId, :score, :zombiesKilled, :level, :shotsFired );');
var getUserStats = c.prepare('SELECT COUNT(*) AS TotalGames, ' +
    'SUM(score) AS Totalscore, MAX(score) AS Highscore, ' +
    'MAX(zombiesKilled) AS MostZombiesKilled, ' +
    'MAX(level) AS HighestLevel, ' +
    'MAX(shotsFired) AS MostShotsFired ' +
    'FROM Game WHERE email = :email; ');
var getStats = c.prepare('SELECT email, ' +
    'COUNT(*) AS TotalGames, ' +
    'SUM(score) AS Totalscore, ' +
    'MAX(score) AS Highscore, ' +
    'MAX(zombiesKilled) AS MostZombiesKilled, ' +
    'MAX(level) AS HighestLevel, ' +
    'MAX(shotsFired) AS MostShotsFired ' +
    'FROM Game');


/**
 * @api {POST} /api/user Create new user.
 * @apiName CreateUser
 * @apiGroup User
 *
 * @apiParam {String} email The user's email.
 * @apiParam {String} pass User's password.
 *
 * @apiSuccess {String} email The user's email.
 *
 * @apiError MissingParameters Some parameters were missing.
 * @apiErrorExample {json} MissingParameters
 *          HTTP/1.1 400 Bad Request
 *          {
 *              "error": "Some parameters were missing."
 *          }
 *
 * @apiError Invalid-Email Was not a valid email address.
 * @apiErrorExample {json} Invalid-Email
 *          HTTP/1.1 400 Bad Request
 *          {
 *              "error": "Invalid email format."
 *          }
 *
 * @apiError DisplayNameConflict
 * @apiErrorExample {json} Email-Conflict
 *          HTTP/1.1 409 Conflict
 *          {
 *              "error": "A user already exists with the email provided."
 *          }
 *
 * @apiVersion 0.1.0
 */
app.post('/api/user', (req, res) => {

    console.dir(req.body);

    if (!req.body || !req.body.email || !req.body.pass) {
        return res.status(400).json({ 'error': 'Some parameters were missing.' });
    }

    // checks for valid email.
    if (!validateEmail(req.body.email)) {
        return res.status(400).json({ 'error': 'Invalid email format.' });
    }

    c.query(createUser({
        email: req.body.email,
        pass : req.body.pass
    }), (err, rows) => {
        if (err) {
            // duplicate
            if (err.code === 1062) {
                res.status(409).json({ 'error': 'A user already exists with the email provided.' });
            }

            console.dir(err);

            res.status(409).json({ 'error': 'User could not be created.' });
        } else {
            console.dir(rows);
            res.status(200).json({ email: req.body.email });
        }
    });
});

/**
 * @api {GET} /api/users Get all users' information.
 * @apiName GetAllUsers
 * @apiGroup User
 *
 * @apiSuccess {String} email The user's email.
 *
 * @apiSuccessExample {json} Success-Response
 *          HTTP/1.1 200 OK
 *          {
 *              "email": {String},
 *          }
 *
 * @apiVersion 0.1.0
 */
app.get('/api/users', (req, res, next) => {

    // email not supplied,  gets all users
    if (!req.query || !req.query.email) {
        next();
    }
    // email supplied, gets individual user
    else {
        c.query(getUser({
            email: req.query.email
        }), (err, rows) => {
            res.send(JSON.stringify(rows));
        });
    }
});

/**
 * @api {GET} /api/users Get user's information.
 * @apiName GetUser
 * @apiGroup User
 *
 * @apiSuccess {Object[]} profile The user's profile object.
 * @apiSuccess {String} email The user's email.
 *
 * @apiSuccessExample {json} Success-Response:
 *          HTTP/1.1 200 OK
 *          [
 *          {
 *              "email": {String},
 *          },
 *          {
 *              ...
 *          }
 *          ]
 *
 * @apiVersion 0.1.0
 */
app.get('/api/users', (req, res) => {
    c.query(getUsers(), (err, rows) => {
        if (err) {
            res.status(400).json({ 'error': 'Could not get users.' });
        } else {
            console.dir(rows);
            res.send(JSON.stringify(rows));
        }
    });
});


/**
 * @api {POST} /api/game Create game record.
 * @apiName PostGame
 * @apiGroup Game
 *
 * @apiParam {Number} userId The user's ID that played the game.
 * @apiParam {Number} score The score earned in the game.
 * @apiParam {Number} zombiesKilled The number of zombies killed.
 * @apiParam {Number} level The last level reached.
 * @apiParam {Number} shotsFired The number of shots fired during the game.
 *
 * @apiSuccess {Number} gameId The newly inserted game's ID.
 *
 *  * @apiSuccessExample {json} Success-Response:
 *          HTTP/1.1 200 OK
 *          {
 *              "gameId": {number}
 *          }
 *
 * @apiVersion 0.1.0
 */
app.post('/api/game', (req, res) => {

    if (!req.query.email || !req.query.score || !req.query.zombiesKilled
        || !req.query.level || !req.query.shotsFired) {
        return res.status(400).send('Some parameters were not supplied correctly.');
    }

    c.query(createGame({
        email        : req.query.email,
        score        : req.query.score,
        zombiesKilled: req.query.zombiesKilled,
        level        : req.query.level,
        shotsFired   : req.query.shotsFired
    }), (err, result) => {
        console.dir(result);
        res.status(200).json({ gameId: result.info.insertId });
    });
});


/**
 * @api {GET} /api/leaderboard Get all users leaderboard.
 * @apiName GetLeaderboardStats
 * @apiGroup Leaderboard
 *
 * @apiSuccessExample {json} Success-Response:
 *          HTTP/1.1 200 OK
 *          [{
 *              "email": {string},
 *              "TotalGames": {number},
 *              "TotalScore": {number},
 *              "Highscore": {number},
 *              "MostZombiesKilled": {number},
 *              "HighestLevel": {number}
 *          },
 *          ...
 *          ]
 *
 * @apiVersion 0.1.0
 */
app.get('/api/leaderboard', (req, res) => {
    c.query(getStats(),
        (err, rows) => {

            if (err) {

            }

            res.status(200).send(JSON.stringify(rows));
        });
    // .on('result', (result) => {
    //     var results = [];
    //
    //     result.on('data', (row) => {
    //         results.push(row);
    //     }).on('end', () => {
    //         res.send(JSON.stringify(results));
    //     });
    // });
});


/**
 * @api {GET} /api/leaderboard/user/:email Get user leaderboard.
 * @apiName UserLeaderboard
 * @apiGroup Leaderboard
 * @apiDescription Gets the leaderboard stats for the given email account.
 *
 * @apiParam email The user's Google email account.
 *
 * @apiSuccessExample {json} Success-Response:
 *          HTTP/1.1 200 OK
 *          {
 *              "TotalGames": {number},
 *              "TotalScore": {number},
 *              "Highscore": {number},
 *              "MostZombiesKilled": {number},
 *              "HighestLevel": {number}
 *          }
 *
 * @apiVersion 0.1.0
 */
app.get('/api/leaderboard/user/:email', (req, res) => {

    c.query(getUserStats({
        email: req.params.email
    }), (err, result) => {
        res.send(JSON.stringify(result));
    });
    // .on('result', (result) => {
    //     result.on('data', (row) => {
    //         res.send(JSON.stringify(row));
    //     });
    // });
});


/**
 * @api {POST} /api/login Log user in.
 * @apiName Login
 * @apiGroup User
 * @apiDescription If an id_token parameter is supplied, it will sign the user in with
 *                 their Google account by verifying the token provided with the Google API
 *                 client. Once valid, a check is done to see if the Google account has
 *                 been registered with the app before. If not, an account is created.
 *                 Account information is returned back.
 *
 * @apiParam id_token The JWT provided by the Google API client that is going to be validated.
 * @apiParam email The user's email
 *
 * @apiSuccessExample {json} Success-Response:
 *          HTTP/1.1 200 OK
 *          {
 *              "email": "john@example.com",
 *              "firstName": "John",
 *              "lastName": "Smith",
 *              "email_verified": true
 *          }
 *
 * @apiVersion 0.1.0
 */
app.post('/api/login', (req, res) => {


    // sign in with email and pass
    if (!req.query.id_token) {
        console.log("in email and pass");

        if (!req.body.email || !req.body.pass) {
            return res.status(400).json({ 'error': 'Parameter missing.' });
        }
        console.dir("pass: " + req.body.pass);
        c.query(login({
            email: req.body.email,
            pass : req.body.pass
        }), (error, result) => {
            if (error) {
                res.status(400).json({ 'error': 'Something went wrong while logging in.' });
            } else if (!result || !result.info || !result.info.numRows) {
                res.status(401).json({ 'error': 'Email or password did not match.' })
            } else {
                //console.log(result);
                if (result.info.affectedRows == 0) {
                    console.log("Failed login");
                    res.status(401).json({ 'error': 'Email or password did not match.' })
                } else {
                    console.log("Success");
                    res.status(200).json({
                        email: result[0].email,
                        firstName: "",
                        lastName: ""
                    });
                }
            }
        });
    }
    // sign in with google account by verifying id_token
    else {

        console.log("in validate token");

        validateToken(req.query.id_token, (err, response) => {
            console.log("verifying");
            if (err) {
                //return res.status(500).send('Token could not be verified.');
                return res.status(500).json({ error: "Token could not be verified.", email_verified: false });
            } else {
                var tokenInfo = response.getPayload();

                c.query(getUser({ email: tokenInfo.email }),
                    (error, result) => {
                        //console.dir(error);
                        console.dir(result);

                        if (result.length) {
                            console.log("user already created");
                            result[0].email_verified = true;
                            console.dir(tokenInfo);
                            //return res.status(200).json(result);
                        } else {
                            console.log("creating user");

                            //console.dir(tokenInfo);
                            c.query(createUser({
                                email: tokenInfo.email,
                                pass : 'google',
                            }), (error, result) => {
                                if (error) {
                                    return res.status(500).json({ error: "Error occurred.", email_verified: false });
                                }
                            });
                        }
                        var responseObj = {
                            email_verified: true,
                            email         : tokenInfo.email,
                            firstName     : tokenInfo.given_name,
                            lastName      : tokenInfo.family_name
                        };
                        return res.status(200).json(responseObj);

                    });
            }
        });
    }
});


function validateToken(token, callback) {
    oauth2Client.verifyIdToken(token, config.CLIENT_ID, callback);
}

function handleError(err, res, message) {
    console.dir(err.message);

    res.send("Error: " + message);
    throw err;
}

// source: https://goo.gl/0TFRJt
function validateEmail(email) {

    var re = /^(([^<>()\[\]\\.,;:\s@"]+(\.[^<>()\[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;
    return re.test(email);
}

app.listen(PORT, function () {
    console.log("Server listening on : http://localhost:%s", PORT);
});
