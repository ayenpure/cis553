'use strict';

const args = process.argv;
var brand = "Doritos"

if(args.length >= 3)
  brand = args[2]

var googleTrends = require('/home/abhishek/node_modules/google-trends-api/lib/google-trends-api.min.js');

googleTrends.interestByRegion({keyword: brand, startTime: new Date('2017-02-01'), 
  endTime: new Date('2017-02-06'), geo: 'US'})
.then((res) => {
  console.log(res);
})
.catch((err) => {
  console.log(err);
})
