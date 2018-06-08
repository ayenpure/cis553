'use strict';

const args = process.argv;
var brand = ['Pepsi', 'Doritos']
var startDate = '2010-03-01'
var endDate = '2010-03-02'

if(args.length >= 3)
  brand = args[2]
if(args.length >= 4)
  brand = args[3]
if(args.length >= 5)
  brand = args[4]

var googleTrends = require('/home/abhishek/node_modules/google-trends-api/lib/google-trends-api.min.js');

googleTrends.interestByRegion({keyword: brand, 
  startTime: new Date(startDate), 
  endTime: new Date(endDate), 
  geo: 'US'})
.then((res) => {
  console.log(res);
})
.catch((err) => {
  console.log(err);
})
