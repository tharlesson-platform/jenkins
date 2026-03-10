const { sum } = require('../src/sum');

test('sum should work', () => {
  expect(sum(2, 3)).toBe(5);
});
