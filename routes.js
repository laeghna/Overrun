const express = require('express');
const router = express.Router();

const userCtrl = require('./controllers/userController');
const gameCtrl = require('./controllers/gameController');
const loginCtrl = require('./controllers/loginController');

// routes
router.route('/api/user').post(userCtrl.postUser);
router.route('/api/users').get(userCtrl.getUser);
router.route('/api/users').get(userCtrl.getUsers);

router.route('/api/game').post(gameCtrl.postGameScore);
router.route('/api/games').get(gameCtrl.getGameScores);
router.route('/api/leaderboard').get(gameCtrl.getLeaderboardStats);
router.route('/api/leaderboard/user/:email').get(gameCtrl.getUserLeaderboard);

router.route('/api/login').post(loginCtrl.loginCustom);
router.route('/api/login/google').post(loginCtrl.loginGoogle);
router.route('/api/login/facebook').post(loginCtrl.loginFacebook);


module.exports = router;