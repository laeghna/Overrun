var http = require('http');


var express = require('express');
var app = express();


var google = require('google-auth-library');
var OAuth2 = new google().OAuth2;

var CLIENT_ID = '711195024447-ltgs11u6ho1otmfsocoeql0le3e9n4hm.apps.googleusercontent.com';
var CLIENT_SECRET = 'yQqOP0KLnM6kM_cL2qoa8IqM';

var oauth2Client = new OAuth2(CLIENT_ID, CLIENT_SECRET);

// express config
const PORT = 8080;
const apiurl = '';
app.set("title", "Overrun");
// require('./routes')(app, c);


// body-parser config
var bp = require('body-parser');
app.use(bp.json());
app.use(bp.urlencoded({ extended: true }));


// MySQL config
var Client = require('mariasql');
var c = new Client({
    host    : 'cssgate.insttech.washington.edu',
    user    : 'earowell',
    password: 'azLats*',
    db      : 'earowell'
});

// MySQL prepared statements
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
var getStats = c.prepare('SELECT email, COUNT(*) AS TotalGames, ' +
                         'SUM(score) AS Totalscore, MAX(score) AS Highscore, ' +
                         'MAX(zombiesKilled) AS MostZombiesKilled, ' +
                         'MAX(level) AS HighestLevel, ' +
                         'MAX(shotsFired) AS MostShotsFired ' +
                         'FROM Game');



/**
 * @apiDefine User endpoints.
 *
 * Endpoints for creating users and getting user information.
 */

/**
 * @api {POST} /api/user
 * @apiName CreateUser
 * @apiGroup User
 *
 * @apiParam {String} email The user's email.
 * @apiParam {String} firstName The user's first name.
 * @apiParam {String} lastName The user's last name.
 *
 * @apiSuccess {String} userId The user's newly created ID.
 *
 * @apiError IncorrectParameters
 *
 * @apiVersion 1.0.0
 */
app.post('/api/user', (req, res) => {
    if (!req.body || !req.body.email || !req.body.firstName || !req.body.lastName) {
        return res.status(400).send('Some parameters were not supplied correctly.');
    }

    // TODO: check for unique email

    console.dir(req.body);

    c.query(createUser({
        email    : req.body.email,
        firstName: req.body.firstName,
        lastName : req.body.lastName
    }))
        .on('result', (result) => {
            console.dir(result);
            //res.status(200).send("User inserted with id: " + result.info.insertId);
            res.status(200).json({ userId: result.info.insertId });
        });
});

/**
 * @api {GET} /api/users
 * @apiName GetAllUsers
 * @apiGroup User
 *
 * @apiSuccess {String} userId The user's unique ID.
 * @apiSuccess {String} email The user's email.
 * @apiSuccess {String} firstName The user's first name.
 * @apiSuccess {String} lastName The user's last name.
 *
 * @apiSuccessExample {json} Success-Response:
 *          HTTP/1.1 200 OK
 *          {
 *              "userId": 123,
 *              "email": john@example.com,
 *              "firstName": John,
 *              "lastName": Smith
 *          }
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
 * @api {GET} /api/users
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
 *              "userId": 123,
 *              "email": john@example.com,
 *              "firstName": John,
 *              "lastName": Smith
 *          },
 *          {
 *              ...
 *          }
 *          ]
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
 * @api {POST} /api/game
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
 *              "gameId": 123
 *          }
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
            //res.status(200).send("User inserted with id: " + result.info.insertId);
            res.status(200).json({ gameId: result.info.insertId });
        });
});


/**
 * @api {GET} /api/leaderboard
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
 * @api {GET} /api/leaderboard/user/:email
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
 * @api {POST} /api/signin
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



