const http = require('http');
const fs = require('fs');
const bcrypt = require('bcrypt');
const config = JSON.parse(fs.readFileSync("overrun.json"));


// Google OAuth config
const google = require('google-auth-library');
const OAuth2 = new google().OAuth2;

const CLIENT_ID = config.oauthConfig.CLIENT_ID;
const CLIENT_SECRET = config.oauthConfig.CLIENT_SECRET;

const oauth2Client = new OAuth2(CLIENT_ID, CLIENT_SECRET);


// MySQL config
const mysql = require('mysql');
const c = new mysql.createConnection(config.mysqlConfig);

// SQL prepared statements
const login = 'SELECT * FROM User WHERE email = ? AND hash = ?;';
const getUser = 'SELECT * FROM User WHERE email = ?;';
const createUser = 'INSERT INTO User (email, salt, hash) ' +
    'VALUES ( ?, ?, ? );';


module.exports = (db) => {


    /**
     * @api {POST} /api/login/google Log user in (Google).
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
    const loginGoogle = (req, res) => {

        // sign in with google account by verifying id_token
        console.log("in validate token");

        validateToken(req.query.id_token, (err, response) => {
            console.log("verifying");
            if (err) {
                console.dir(err);
                //return res.status(500).send('Token could not be verified.');
                return res.status(500).json({ error: "Token could not be verified.", email_verified: false });
            } else {
                const tokenInfo = response.getPayload();

                db.query(getUser, [tokenInfo.email],
                    (error, result) => {
                        if (error) console.dir(error);
                        else console.log("result: %s", result);

                        if (result.length) {
                            console.log("user already created");
                            result[0].email_verified = true;
                            console.dir(tokenInfo);

                            const responseObj = {
                                email_verified: true,
                                email         : tokenInfo.email,
                                firstName     : tokenInfo.given_name,
                                lastName      : tokenInfo.family_name
                            };
                            return res.status(200).json(responseObj);
                        } else {
                            console.log("creating user: " + tokenInfo.email);

                            //console.dir(tokenInfo);
                            db.query(createUser, [
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
    };


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
    const loginCustom = (req, res) => {
        if (!req.query.email || !req.query.pass) {
            return res.status(400).json({ 'error': 'Some parameters were missing.' });
        }
        console.log("email: " + req.query.email + " pass: " + req.query.pass);
        db.query(getUser, [req.query.email], (err, rows) => {

            if (!rows.length) return res.status(400).json({ 'error': 'No user matching that email.' });

            const salt = rows[0].salt;

            bcrypt.hash(req.query.pass, salt, (err, hash) => {
                if (err) return res.status(500).json({ 'error': 'Internal server error.' });

                db.query(login, [
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
    };


    /**
     * @api {POST} /api/login/facebook Log user in (Facebook).
     * @apiName Login (Facebook)
     * @apiGroup User
     * @apiDescription This will sign the user in with their Facebook account.
     *
     * @apiParam email User's Facebook email.
     *
     * @apiSuccessExample {json} Success-Response:
     *          HTTP/1.1 200 OK
     *          {
     *              "email": "john@facebook.com",
     *              "firstName": "",
     *              "lastName": "",
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
    const loginFacebook = (req, res) => {

        if (!req.query.email) {
            return res.status(400).json({ 'error': 'Some parameters were missing.' });
        } else {
            db.query(getUser, [req.query.email],
                (error, result) => {
                    if (error) console.dir(error);
                    else console.log("result: %s", result);

                    if (result.length) {
                        console.log("user already created");

                        const responseObj = {
                            email_verified: true,
                            email         : req.query.email,
                            firstName     : '',
                            lastName      : ''
                        };
                        return res.status(200).json(responseObj);
                    } else {
                        console.log("creating user: " + req.query.email);

                        //console.dir(tokenInfo);
                        c.query(createUser, [
                            req.query.email,
                            'facebook',
                            ''
                        ], (error, result) => {
                            if (error) {
                                return res.status(500).json({ error: "Error occurred.", email_verified: false });
                            }
                            const responseObj = {
                                email_verified: true,
                                email         : req.query.email,
                                firstName     : '',
                                lastName      : ''
                            };
                            return res.status(200).json(responseObj);
                        });
                    }
                });
        }
    };

    return {
        loginCustom: loginCustom,
        loginFacebook: loginFacebook,
        loginGoogle: loginGoogle
    }
};

function validateToken(token, callback) {
    oauth2Client.verifyIdToken(token, CLIENT_ID, callback);
}
