const sinon = require('../node_modules/sinon');
const chai = require('../node_modules/chai');

beforeEach(function () {
    this.sandbox = sinon.sandbox.create()
})

afterEach(function () {
    this.sandbox.restore()
})