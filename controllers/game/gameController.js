
const tableName = (process.env.NODE_ENV === 'test') ? 'TestGame' : 'Game';

// SQL prepared statements
const createGame = `INSERT INTO ${tableName} (email, score, zombiesKilled, level, shotsFired)
    VALUES ( ?, ?, ?, ?, ? );`;
const getUserStats = `SELECT COUNT(*) AS TotalGames, 
    SUM(score) AS Totalscore, MAX(score) AS Highscore, 
    MAX(zombiesKilled) AS MostZombiesKilled, 
    MAX(level) AS HighestLevel, 
    MAX(shotsFired) AS MostShotsFired 
    FROM ${tableName} WHERE email = ?; `;
const getStats = `SELECT email, 
    COUNT(*) AS totalGames, 
    SUM(score) AS totalScore, 
    MAX(score) AS highScore,  
    MAX(zombiesKilled) AS mostZombiesKilled,  
    MAX(level) AS highestLevel, 
    MAX(shotsFired) AS mostShotsFired 
    FROM ${tableName} GROUP BY email;`;
const sqlGetGameScores = `SELECT * FROM ${tableName} ORDER BY score DESC;`;
const getUsersGameScores = `SELECT * FROM ${tableName} WHERE email = ? ORDER BY score DESC;`;


module.exports = (db) => {

    /**
     * @api {POST} /api/games Create game record.
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
     * @apiVersion 0.1.0
     */
    const postGameScore = (req, res) => {

        if (req.query && !req.query.email || !req.query.score || !req.query.zombiesKilled
            || !req.query.level || !req.query.shotsFired) {
            return res.status(400).json({ 'error': 'Some parameters were missing.' });
        }

        db.query(createGame, [
            req.query.email,
            req.query.score,
            req.query.zombiesKilled,
            req.query.level,
            req.query.shotsFired
        ], (err, result) => {
            if (err) return res.status(500).json({ 'error': err.message });
            //console.dir(result);
            return res.status(201).json({ gameId: result.insertId });
        });
    };

    /**
     * @api {GET} /api/games Get all users game scores.
     * @apiName GetGameScores
     * @apiGroup Game
     *
     * @apiParam email (Optional) Limits the game scores to this user.
     * @apiParam limit (Optional) Limit the number of game scores returned.
     *
     * @apiSuccessExample {json} Success-Response:
     *          HTTP/1.1 200 OK
     *          [{
     *              "email": {String},
     *              "score": {Number},
     *              "zombiesKilled": {Number},
     *              "level": {Number},
     *              "shotsFired": {Number}
     *          },
     *          ...
     *          ]
     *
     * @apiVersion 0.1.0
     */
    const getGameScores = (req, res) => {

        let sqlQuery;
        if (req.query && req.query.email) {
            sqlQuery = getUsersGameScores;
        } else {
            sqlQuery = sqlGetGameScores;
        }

        // add limit if provided and check if it is a number
        if (req.query && req.query.limit && !isNaN(req.query.limit)) {
            sqlQuery = sqlQuery.replace(/;/, ' ').concat('LIMIT ', req.query.limit, ';');
        }

        db.query(sqlQuery, [(req.query && req.query.email) ? req.query.email : ''], (err, result) => {
            if (err) return res.status(500).json({ 'error': err.message });
            else {
                return res.status(200).json(result);
            }
        });
    };


    /**
     * @api {GET} /api/leaderboard Get all users leaderboard scores.
     * @apiName GetLeaderboardStats
     * @apiGroup Leaderboard
     *
     * @apiSuccessExample {json} Success-Response:
     *          HTTP/1.1 200 OK
     *          [{
     *              "email": {String},
     *              "totalGames": {Number},
     *              "totalScore": {Number},
     *              "highscore": {Number},
     *              "mostZombiesKilled": {Number},
     *              "highestLevel": {Number}
     *          },
     *          ...
     *          ]
     *
     * @apiVersion 0.1.0
     */
    const getLeaderboardStats = (req, res, next) => {
        if (req.query && req.query.email) {
            next();
        } else {
            db.query(getStats, (err, rows) => {
                if (err) return res.status(500).json({ 'error': 'Internal server error.' });

                return res.status(200).json(rows);
            });
        }
    };


    /**
     * @api {GET} /api/leaderboard Get user leaderboard.
     * @apiName UserLeaderboard
     * @apiGroup Leaderboard
     * @apiDescription Gets the leaderboard stats for the given email account.
     *
     * @apiParam email The user's email account.
     *
     * @apiSuccessExample {json} Success-Response:
     *          HTTP/1.1 200 OK
     *          {
     *              "email": {String}
     *              "totalGames": {Number},
     *              "totalScore": {Number},
     *              "highScore": {Number},
     *              "mostZombiesKilled": {Number},
     *              "highestLevel": {Number}
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
    const getUserLeaderboard = (req, res) => {

        if (req.query && !req.query.email) {
            return res.status(400).json({ 'error': 'Some parameters were missing.' })
        }

        db.query(getUserStats, [req.query.email], (err, result) => {
            if (err) return res.status(500).json({ 'error': 'Internal server error.' });

            return res.status(200).json(result);
        });
    };

    return {
        postGameScore      : postGameScore,
        getGameScores      : getGameScores,
        getLeaderboardStats: getLeaderboardStats,
        getUserLeaderboard : getUserLeaderboard
    }
};
