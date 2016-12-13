const express = require('express');
const router = express.Router();


module.exports = (db) => {

    const userCtrl = require('./controllers/user/userController')(db);
    const gameCtrl = require('./controllers/game/gameController')(db);
    const loginCtrl = require('./controllers/login/loginController')(db);

    // routes
    router.route('/api/user').post(userCtrl.postUser);
    router.route('/api/users').get(userCtrl.getUser);
    router.route('/api/users').get(userCtrl.getUsers);

    router.route('/api/game').post(gameCtrl.postGameScore);
    router.route('/api/games').get(gameCtrl.getGameScores);
    router.route('/api/leaderboard').get(gameCtrl.getLeaderboardStats);
    router.route('/api/leaderboard').get(gameCtrl.getUserLeaderboard);

    router.route('/api/login').post(loginCtrl.loginCustom);
    router.route('/api/login/google').post(loginCtrl.loginGoogle);
    router.route('/api/login/facebook').post(loginCtrl.loginFacebook);

    return router
};