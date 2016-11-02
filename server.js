var http = require('http');
var fs = require('fs');


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
var config = fs.readFileSync("overrun.json");
var c = new Client(config.mysqlConfig);

// SQL prepared statements
var createUser = c.prepare('INSERT INTO User (email, firstName, lastName) ' +
    'VALUES ( :email, :firstName, :lastName );');
var getUser = c.prepare('SELECT * FROM User WHERE email = :email;');
var getUsers = c.prepare('SELECT * FROM User;');
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
 * @apiParam {String} firstName The user's first name.
 * @apiParam {String} lastName The user's last name.
 *
 * @apiSuccess {String} email The user's email.
 *
 * @apiError IncorrectParameters
 *
 * @apiVersion 0.1.0
 */
app.post('/api/user', (req, res) => {
    if (!req.query || !req.query.email || !req.query.firstName || !req.query.lastName) {
        return res.status(400).send('Some parameters were not supplied correctly.');
    }

    // TODO: check for unique email

    console.dir(req.query);

    c.query(createUser({
        email    : req.query.email,
        firstName: req.query.firstName,
        lastName : req.query.lastName
    }))
        .on('result', (result) => {
            console.dir(result);
            //res.status(200).send("User inserted with id: " + result.info.insertId);
            res.status(200).json({ email: req.query.email });
        });
});

/**
 * @api {GET} /api/users Get all users' information.
 * @apiName GetAllUsers
 * @apiGroup User
 *
 * @apiSuccess {String} email The user's email.
 * @apiSuccess {String} firstName The user's first name.
 * @apiSuccess {String} lastName The user's last name.
 *
 * @apiSuccessExample {json} Success-Response:
 *          HTTP/1.1 200 OK
 *          {
 *              "email": {string},
 *              "firstName": {string},
 *              "lastName": {string}
 *          }
 *
 * @apiVersion 0.1.0
 */
app.get('/api/users', (req, res) => {

    // email not supplied,  gets all users
    if (!req.query || !req.query.email) {
        next();
    }
    // email supplied, gets individual user
    else {
        c.query(getUser({
            email: req.query.email
        }))
            .on('result', (result) => {
                console.dir(result);
                result.on('data', (row) => {
                    res.send(JSON.stringify(row));
                });
            });
    }
});

/**
 * @api {GET} /api/users Get user's information.
 * @apiName GetUser
 * @apiGroup User
 *
 * @apiParam {String} email The user's email to return information for.
 *
 * @apiSuccess {Object[]} profile The user's profile object.
 * @apiSuccess {String} profile.userId The user's unique ID.
 * @apiSuccess {String} profile.email The user's email.
 * @apiSuccess {String} profile.firstName The user's first name.
 * @apiSuccess {String} profile.lastName The user's last name.
 *
 * @apiSuccessExample {json} Success-Response:
 *          HTTP/1.1 200 OK
 *          [
 *          {
 *              "userId": {number},
 *              "email": {string},
 *              "firstName": {string},
 *              "lastName": {string}
 *          },
 *          {
 *              ...
 *          }
 *          ]
 *
 * @apiVersion 0.1.0
 */
app.get('/api/users', (req, res) => {
    c.query(getUsers())
        .on('result', (result) => {
            var results = [];

            result.on('data', (row) => {
                results.push(row);
            }).on('end', () => {
                res.send(JSON.stringify(results));
            });

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

    if (!req.query.userId || !req.query.score || !req.query.zombiesKilled
        || !req.query.level || !req.query.shotsFired) {
        return res.status(400).send('Some parameters were not supplied correctly.');
    }

    c.query(createGame({
        userId       : req.query.userId,
        score        : req.query.score,
        zombiesKilled: req.query.zombiesKilled,
        level        : req.query.level,
        shotsFired   : req.query.shotsFired
    }))
        .on('result', (result) => {
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
    c.query(getStats())
        .on('result', (result) => {
            var results = [];

            result.on('data', (row) => {
                results.push(row);
            }).on('end', () => {
                res.send(JSON.stringify(results));
            });
        });
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

    try {
        c.query(getUserStats({
            email: req.params.email
        }))
            .on('result', (result) => {
                result.on('data', (row) => {
                    res.send(JSON.stringify(row));
                });
            });
    } catch (e) {
        console.error("Error: " + e);
    }

});

/**
 * @api {POST} /api/signin Sign user in.
 * @apiName SignIn
 * @apiGroup User
 * @apiDescription Verifies the token provided by the Google API client. Once valid,
 *                 a check is done to see if the Google account has been registered with
 *                 the app before. If not, an account is created. Account information is
 *                 returned back.
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
 *
 * @apiVersion 0.1.0
 */
app.post('/api/signin', (req, res) => {
    if (!req.query.id_token) {
        return res.status(400).send('ID Token missing.');
    }

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
                        result[0].email_verified = true;
                        console.dir(result);
                        return res.status(200).json(result);
                    } else {
                        c.query(createUser({
                            email    : tokenInfo.email,
                            firstName: tokenInfo.given_name,
                            lastName : tokenInfo.family_name
                        }), (error, result) => {
                            if (error) {
                                return res.status(500).json({ error: "Error occurred.", email_verified: false });
                            } else {
                                result.email_verified = true;
                                return res.status(200).json(result);
                            }
                        });
                    }
                });
        }
    });
});


function validateToken(token, callback) {
    oauth2Client.verifyIdToken(token, CLIENT_ID, callback);
}

function handleError(err, res, message) {
    console.dir(err.message);

    res.send("Error: " + message);
    throw err;
}

app.listen(PORT, function () {
    console.log("Server listening on : http://localhost:%s", PORT);
});
