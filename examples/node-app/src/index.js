const { sum } = require('./sum');

if (require.main === module) {
  console.log(`node-app ready: 2 + 3 = ${sum(2, 3)}`);
}

module.exports = { sum };
