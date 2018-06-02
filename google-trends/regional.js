'use strict';

const args = process.argv;
var brand = "Pepsi"

if(args.length >= 3)
  brand = args[2]

var googleTrends = require('/home/abhishek/node_modules/google-trends-api/lib/google-trends-api.min.js');

googleTrends.interestByRegion({keyword: brand, 
  startTime: new Date('2010-03-01'), 
  endTime: new Date('2018-03-04'), 
  geo: 'US'})
.then((res) => {
  console.log(res);
})
.catch((err) => {
  console.log(err);
})
