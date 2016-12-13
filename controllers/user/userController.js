const bcrypt = require('bcrypt');

// SQL prepared statements
const createUser = 'INSERT INTO User (email, salt, hash) ' +
    'VALUES ( ?, ?, ? );';
const getUser = 'SELECT * FROM User WHERE email = ?;';
const getUsers = 'SELECT email FROM User;';


module.exports = (db) => {


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
    const postUser = (req, res) => {

        if (req.query && !req.query.email || !req.query.pass) {
            return res.status(400).json({ error: 'Some parameters were missing.' });
        }

        // checks for valid email.
        if (!validateEmail(req.query.email)) {
            return res.status(400).json({ error: 'Invalid email format.' });
        }

        bcrypt.genSalt(10, (err, salt) => {

            if (err) return res.status(500).json({ error: 'Internal server error.' });

            bcrypt.hash(req.query.pass, salt, (err, hash) => {
                if (err) return res.status(500).json({ error: 'Internal server error.' });

                db.query(createUser, [req.query.email, salt, hash], (err, rows) => {
                    if (err) {

                        // duplicate
                        if (err.errno === 1062) {
                            return res.status(409).json({ error: 'A user already exists with the email provided.' });
                        }

                        console.dir(err);

                        return res.status(500).json({ error: 'User could not be created.' });
                    } else {
                        console.log(rows);
                        return res.status(201).json({ email: req.query.email });
                    }
                });
            });
        });
    };

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
    const getUser = (req, res, next) => {

        // email not supplied, gets all users
        if (!req.query || !req.query.email) {
            next();
        }
        // email supplied, gets individual user
        else {
            db.query(getUser, [req.query.email], (err, rows) => {
                    if (err) return res.status(500).json({ 'error': 'Error fetching user.' });
                    res.json({ 'email': rows[0].email });
                }
            );
        }
    };

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
    const getUsers = (req, res) => {
        db.query(getUsers, (err, rows) => {
            if (err) {
                return res.status(400).json({ 'error': 'Could not get users.' });
            } else {
                console.dir(rows);
                return res.json(rows);
            }
        });
    };


    return {
        getUser: getUser,
        getUsers: getUsers,
        postUser: postUser
    }
};

// source: https://goo.gl/0TFRJt
function validateEmail(email) {

    const re = /^(([^<>()\[\]\\.,;:\s@"]+(\.[^<>()\[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;
    return re.test(email);
}