const http = require('http');
const fs = require('fs');
const bcrypt = require('bcrypt');
const config = JSON.parse(fs.readFileSync("overrun.json"));

const express = require('express');
const app = express();


const google = require('google-auth-library');
const OAuth2 = new google().OAuth2;

// const CLIENT_ID = '417571724567-e4us94dgu6eah1p0icr3bvbgtmgulqqp.apps.googleusercontent.com';
// const CLIENT_SECRET = 'lc5XyqOSS71WUcccPQzDnkZJ';
const CLIENT_ID = config.oauthConfig.CLIENT_ID;
const CLIENT_SECRET = config.oauthConfig.CLIENT_SECRET;

const oauth2Client = new OAuth2(CLIENT_ID, CLIENT_SECRET);

// express config
const PORT = process.env.PORT || 8081;
const apiurl = 'https://cssgate.insttech.washington.edu:8081/';
app.set("title", "Overrun");
// require('./routes')(app, c);


// body-parser config
const bp = require('body-parser');
app.use(bp.json());
app.use(bp.urlencoded({ extended: true }));


// MySQL config
const mysql = require('mysql');
const c = new mysql.createConnection(config.mysqlConfig);

// SQL prepared statements
const createUser = 'INSERT INTO User (email, salt, hash) ' +
    'VALUES ( ?, ?, ? );';
const login = 'SELECT * FROM User WHERE email = ? AND hash = ?;';
const getUser = 'SELECT * FROM User WHERE email = ?;';
const getUserSalt = 'SELECT salt FROM User WHERE email = ?;';
const getUsers = 'SELECT email FROM User;';
const createGame = 'INSERT INTO Game (email, score, zombiesKilled, level, shotsFired)' +
    'VALUES ( ?, ?, ?, ?, ? );';
const getUserStats = 'SELECT COUNT(*) AS TotalGames, ' +
    'SUM(score) AS Totalscore, MAX(score) AS Highscore, ' +
    'MAX(zombiesKilled) AS MostZombiesKilled, ' +
    'MAX(level) AS HighestLevel, ' +
    'MAX(shotsFired) AS MostShotsFired ' +
    'FROM Game WHERE email = ?; ';
const getStats = 'SELECT email, ' +
    'COUNT(*) AS TotalGames, ' +
    'SUM(score) AS Totalscore, ' +
    'MAX(score) AS Highscore, ' +
    'MAX(zombiesKilled) AS MostZombiesKilled, ' +
    'MAX(level) AS HighestLevel, ' +
    'MAX(shotsFired) AS MostShotsFired ' +
    'FROM Game';


/**
 * @api {POST} /api/user Create new user.
 * @apiName CreateUser
 * @apiGroup User
 *
 * @apiParam {String} email The user's email.
 * @apiParam {String} pass User's password.
 *
 * @apiSuccess {String} email The user's email.
 * @apiSuccessExample {json} Successful-Creation:
 *          HTTP/1.1 201 Created
 *          {
 *              "email": {String}
 *          }
 *
 * @apiError MissingParameters Some parameters were missing.
 * @apiErrorExample {json} Missing-Parameters
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

    console.dir(req);

    if (!req.query || !req.query.email || !req.query.pass) {
        return res.status(400).json({ error: 'Some parameters were missing.' });
    }

    // checks for valid email.
    if (!validateEmail(req.query.email)) {
        return res.status(400).json({ error: 'Invalid email format.' });
    }

    const salt = bcrypt.genSalt(10, (err, salt) => {

        if (err) return res.status(500).json({ error: 'Internal server error.' });

        bcrypt.hash(req.query.pass, salt, (err, hash) => {
            if (err) return res.status(500).json({ error: 'Internal server error.' });

            c.query(createUser, [req.query.email, salt, hash], (err, rows) => {
                if (err) {

                    // duplicate
                    if (err.errno === 1062) {
                        return res.status(409).json({ error: 'A user already exists with the email provided.' });
                    }

                    console.dir(err);

                    return res.status(409).json({ error: 'User could not be created.' });
                } else {
                    console.log(rows);
                    return res.status(201).json({ email: req.query.email });
                }
            });
        });
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
        c.query(getUser, [req.query.email], (err, rows) => {
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
 *          [{
 *              "email": {String},
 *          },
 *          {
 *              ...
 *          }]
 *
 * @apiVersion 0.1.0
 */
app.get('/api/users', (req, res) => {
    c.query(getUsers, (err, rows) => {
        if (err) {
            return res.status(400).json({ 'error': 'Could not get users.' });
        } else {
            console.dir(rows);
            return res.send(rows);
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
 * @apiSuccessExample {json} GameRecord-Created:
 *          HTTP/1.1 201 Created
 *          {
 *              "gameId": {Number}
 *          }
 * @apiErrorExample {json} Missing-Parameters
 *          HTTP/1.1 400 Bad Request
 *          {
 *              "error": "Some parameters were missing."
 *          }
 *
 *
 * @apiVersion 0.1.0
 */
app.post('/api/game', (req, res) => {

    if (!req.query.email || !req.query.score || !req.query.zombiesKilled
        || !req.query.level || !req.query.shotsFired) {
        return res.status(400).json({ 'error': 'Some parameters were missing.' });
    }

    c.query(createGame, [
        req.query.email,
        req.query.score,
        req.query.zombiesKilled,
        req.query.level,
        req.query.shotsFired
    ], (err, result) => {
        if (err) return res.status(500).json({ 'error': err.message });
        console.dir(result);
        return res.status(201).json({ gameId: result.insertId });
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
 *              "email": {String},
 *              "TotalGames": {Number},
 *              "TotalScore": {Number},
 *              "Highscore": {Number},
 *              "MostZombiesKilled": {Number},
 *              "HighestLevel": {Number}
 *          },
 *          ...
 *          ]
 *
 * @apiVersion 0.1.0
 */
app.get('/api/leaderboard', (req, res) => {
    c.query(getStats, (err, rows) => {

        if (err) return res.status(500).json({ 'error': 'Internal server error.' });

        return res.status(200).send(JSON.stringify(rows));
    });
});


/**
 * @api {GET} /api/leaderboard/user/:email Get user leaderboard.
 * @apiName UserLeaderboard
 * @apiGroup Leaderboard
 * @apiDescription Gets the leaderboard stats for the given email account.
 *
 * @apiParam email The user's email account.
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
 * @apiErrorExample {json} Missing-Parameters
 *          HTTP/1.1 400 Bad Request
 *          {
 *              "error": "Some parameters were missing."
 *          }
 *
 *
 * @apiVersion 0.1.0
 */
app.get('/api/leaderboard/user/:email', (req, res) => {

    if (!req.param.email) {
        return res.status(400).json({ 'error': 'Some parameters were missing.' })
    }

    c.query(getUserStats, [req.params.email], (err, result) => {
        return res.status(200).send(JSON.stringify(result));
    });
});


/**
 * @api {POST} /api/login Log user in (Google).
 * @apiName Login (Google)
 * @apiGroup User
 * @apiDescription This will sign the user in with their Google account by verifying the
 *                 token provided with the Google API client. Once valid, a check is done
 *                 to see if the Google account has been registered with the app before.
 *                 If not, an account is created. Account information is returned back.
 *
 * @apiParam id_token The JWT provided by the Google API client that is going to be validated.
 *
 * @apiSuccessExample {json} Success-Response:
 *          HTTP/1.1 200 OK
 *          {
 *              "email": "john@example.com",
 *              "firstName": "John",
 *              "lastName": "Smith",
 *              "email_verified": true
 *          }
 * @apiErrorExample {json} Missing-Parameters
 *          HTTP/1.1 400 Bad Request
 *          {
 *              "error": "Some parameters were missing."
 *          }
 *
 * @apiVersion 0.1.0
 */
app.post('/api/login', (req, res, next) => {

    // sign in with email and pass
    if (!req.query.id_token) {
        next();
    }
    // sign in with google account by verifying id_token
    else {
        console.log("in validate token");

        validateToken(req.query.id_token, (err, response) => {
            console.log("verifying");
            if (err) {
                console.dir(err);
                //return res.status(500).send('Token could not be verified.');
                return res.status(500).json({ error: "Token could not be verified.", email_verified: false });
            } else {
                const tokenInfo = response.getPayload();

                c.query(getUser, [tokenInfo.email],
                    (error, result) => {
                        if (error) console.dir(error);
                        else console.log("result: %s", result);

                        if (result.length) {
                            console.log("user already created");
                            result[0].email_verified = true;
                            console.dir(tokenInfo);
                            return res.status(200).json(result);
                        } else {
                            console.log("creating user: " + tokenInfo.email);

                            //console.dir(tokenInfo);
                            c.query(createUser, [
                                tokenInfo.email,
                                'google',
                                ''
                            ], (error, result) => {
                                if (error) {
                                    return res.status(500).json({ error: "Error occurred.", email_verified: false });
                                }
                                const responseObj = {
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
    }
});

/**
 * @api {POST} /api/login Log user in (Email / Password).
 * @apiName Login (Email / Password)
 * @apiGroup User
 * @apiDescription Takes the user's email and retrieves the salt from the database to then
 *                 hash the password with the salt. If this hash matches the stored hash,
 *                 the user is then logged in.
 *
 * @apiParam email The user's email
 * @apiParam pass The user's password
 * @apiSuccessExample {json} Success-Response:
 *          HTTP/1.1 200 OK
 *          {
 *              "email": "john@example.com",
 *              "firstName": "John",
 *              "lastName": "Smith",
 *              "email_verified": true
 *          }
 * @apiErrorExample {json} Missing-Parameters
 *          HTTP/1.1 400 Bad Request
 *          {
 *              "error": "Some parameters were missing."
 *          }
 *
 * @apiVersion 0.1.0
 */
app.post('/api/login', (req, res) => {
    if (!req.query.email || !req.query.pass) {
        return res.status(400).json({ 'error': 'Some parameters were missing.' });
    }
    console.log("email: " + req.query.email + " pass: " + req.query.pass);
    c.query(getUser, [req.query.email], (err, rows) => {

        if (!rows.length) return res.status(400).json({ 'error': 'No user matching that email.' });

        const salt = rows[0].salt;

        bcrypt.hash(req.query.pass, salt, (err, hash) => {
            if (err) return res.status(500).json({ 'error': 'Internal server error.' })

            c.query(login, [
                req.query.email,
                hash
            ], (error, result) => {
                if (error) {
                    return res.status(400).json({ 'error': 'Something went wrong while logging in.' });
                } else if (!result.length) {
                    return res.status(401).json({ 'error': 'Email or password was incorrect.' })
                } else {
                    console.log("Success");
                    return res.status(200).json({
                        email         : result[0].email,
                        firstName     : "",
                        lastName      : "",
                        email_verified: true
                    });
                }
            });
        });
    });
});


function validateToken(token, callback) {
    oauth2Client.verifyIdToken(token, CLIENT_ID, callback);
}

// source: https://goo.gl/0TFRJt
function validateEmail(email) {

    const re = /^(([^<>()\[\]\\.,;:\s@"]+(\.[^<>()\[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;
    return re.test(email);
}

app.listen(PORT, () => {
    console.log("Server listening on : http://localhost:%s", PORT);
});
