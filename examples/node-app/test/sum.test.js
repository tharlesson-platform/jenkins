const test = require('node:test');
const assert = require('node:assert/strict');
const { sum } = require('../src/sum');

test('sum should work', () => {
  assert.equal(sum(2, 3), 5);
});
