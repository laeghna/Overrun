process.env.NODE_ENV = 'test';


let chai = require('chai');
let chaiHttp = require('chai-http');
let server = require('../server');
let should = chai.should();
let qs = require('query-string');

chai.use(chaiHttp);

describe('Games', () => {
    beforeEach((done) => {

    });
});


describe('/GET games', () => {

    it('it should GET all the games', (done) => {
        chai.request(server)
            .get('/api/games')
            .end((err, res) => {

                res.should.have.status(200);
                res.body.should.be.a('array');
                if (res.body && res.body.length) {
                    res.body[0].should.have.property('email');
                    res.body[0].should.have.property('score');
                    res.body[0].should.have.property('zombiesKilled');
                    res.body[0].should.have.property('level');
                    res.body[0].should.have.property('shotsFired');
                }
                done();
            });
    });

    it('it should GET limit games to a certain amount', (done) => {
        chai.request(server)
            .get('/api/games?limit=10')
            .end((err, res) => {

                res.should.have.status(200);
                res.body.should.be.a('array');
                if (res.body && res.body.length) {
                    res.body.length.should.be.below(11);
                    res.body[0].should.have.property('email');
                    res.body[0].should.have.property('score');
                    res.body[0].should.have.property('zombiesKilled');
                    res.body[0].should.have.property('level');
                    res.body[0].should.have.property('shotsFired');
                }
                done();
            });
    });
});

describe('/GET Leaderboard', () => {

    it('it should display stats for all users', (done) => {
        chai.request(server)
            .get('/api/leaderboard')
            .end((err, res) => {
                res.should.have.status(200);
                res.body.should.be.a('array');

                if (res.body && res.body.length) {
                    res.body[0].should.have.property('email');
                    res.body[0].should.have.property('totalGames');
                    res.body[0].should.have.property('totalScore');
                    res.body[0].should.have.property('highScore');
                    res.body[0].should.have.property('mostZombiesKilled');
                    res.body[0].should.have.property('highestLevel');
                }
                done();
            });
    });
});

describe('/POST game', () => {


    it('it should POST a game', (done) => {
        let game = {
            email: 'blah@blah.com',
            score: 0,
            level: 0,
            zombiesKilled: 0,
            shotsFired: 0
        };

        chai.request(server)
            .post('/api/game?' + qs.stringify(game))
            .end((err, res) => {
                res.should.have.status(201);
                res.body.should.be.a('object');
                res.body.should.have.property('gameId');
                res.body.gameId.should.be.a('number');
                done();
            });
    });

    it('it should not POST a game without email', (done) => {
        let game = {
            score: 0,
            level: 0,
            zombiesKilled: 0,
            shotsFired: 0
        };

        chai.request(server)
            .post('/api/game?' + qs.stringify(game))
            .end((err, res) => {
                res.should.have.status(400);
                res.body.should.be.a('object');
                res.body.should.have.property('error');
                res.body.error.should.be.eq('Some parameters were missing.');
                done();
            });
    });


    it('it should not POST a game without score', (done) => {
        let game = {
            email: 'blah@blah.com',
            level: 0,
            zombiesKilled: 0,
            shotsFired: 0
        };

        chai.request(server)
            .post('/api/game?' + qs.stringify(game))
            .end((err, res) => {
                res.should.have.status(400);
                res.body.should.be.a('object');
                res.body.should.have.property('error');
                res.body.error.should.be.eq('Some parameters were missing.');
                done();
            });
    });

    it('it should not POST a game without level', (done) => {
        let game = {
            score: 0,
            email: 'blah@blah.com',
            zombiesKilled: 0,
            shotsFired: 0
        };

        chai.request(server)
            .post('/api/game?' + qs.stringify(game))
            .end((err, res) => {
                res.should.have.status(400);
                res.body.should.be.a('object');
                res.body.should.have.property('error');
                res.body.error.should.be.eq('Some parameters were missing.');
                done();
            });
    });

    it('it should not POST a game without zombiesKilled', (done) => {
        let game = {
            score: 0,
            level: 0,
            email: 'blah@blah.com',
            shotsFired: 0
        };

        chai.request(server)
            .post('/api/game?' + qs.stringify(game))
            .end((err, res) => {
                res.should.have.status(400);
                res.body.should.be.a('object');
                res.body.should.have.property('error');
                res.body.error.should.be.eq('Some parameters were missing.');
                done();
            });
    });

    it('it should not POST a game without shotsFired', (done) => {
        let game = {
            score: 0,
            level: 0,
            zombiesKilled: 0,
            email: 'blah@blah.com',
        };

        chai.request(server)
            .post('/api/game?' + qs.stringify(game))
            .end((err, res) => {
                res.should.have.status(400);
                res.body.should.be.a('object');
                res.body.should.have.property('error');
                res.body.error.should.be.eq('Some parameters were missing.');
                done();
            });
    })

});