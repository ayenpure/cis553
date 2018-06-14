'use strict';

const args = process.argv;
var brand = 'Toyota'
var startDate = '2017-08-01'
var endDate = '2018-02-28'
var loc = 'US-OR'

if(args.length >= 3)
  brand = args[2]
if(args.length >= 4)
  loc = args[3]
if(args.length >= 5)
  startDate = args[4]
if(args.length >= 6)
  endDate = args[5]

var googleTrends = require('/home/abhishek/node_modules/google-trends-api/lib/google-trends-api.min.js');

googleTrends.interestOverTime({keyword: brand, 
  startTime: new Date(startDate), 
  endTime: new Date(endDate), 
  //granularTimeResolution: true,
  geo: loc})
.then((res) => {
  console.log(res);
})
.catch((err) => {
  console.log(err);
})
