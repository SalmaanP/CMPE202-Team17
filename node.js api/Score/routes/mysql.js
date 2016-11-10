var ejs= require('ejs');
var mysql = require('mysql');


var pool = [];
var count = 0;
var queue = [];

var queueasMap = new Map();


var pSize = 500;
var qSize = 100;


var ConnectionPool = mysql.createPool({
	connectionLimit: pSize,
	queueLimit: qSize,
	waitForConnection: true,
	host: 'localhost',
	user: 'root',
	password: 'root',
	database: 'genus'
});



function CreateConnectionPool() {

	for (var i = 0; i < pSize; i++) {
		var connection = mysql.createConnection({
			host: 'localhost',
			user: 'root',
			password: 'root',
			database: 'genus'
		});
		pool.push(connection);

	}
}

function getConnection(callback) {

	console.log("connection requested");

	if (isConnectionFree()) {

		console.log("connection free");
		callback(pool.pop());

	} else {

		console.log("connection not free");
		if (isQueueFree()) {

			console.log('in queue');
			queue.push(count);
			queueasMap.set(count, false);
			token = count;
			count++;


		} else {

			console.log('connection refused');
			return null;
		}
	}
}


function releaseConnection(connection) {

	pool.push(connection);
	console.log('connection is released');
	queueasMap.set(queue.pop(), true);
	queue.shift();

}

function isConnectionFree() {

	if (pool.length <= 0)
		return false;
	else
		return true;

}

function isQueueFree() {

	if (queue.length >= qSize)
		return false;
	else
		return true;
}


function execute(callback,sqlQuery){
	console.log("in execute");
	ConnectionPool.getConnection(function (err,connection)
	{
		if(err)
		{
			throw err;
		}
		else
		{
			connection.query(sqlQuery, function(err, rows, fields) {
				if(err)
				{
					throw err;
				}
				else
				{	// return err or result
					callback(err, rows);
				}
			});
			connection.release();


		}
	});
	console.log("got connection");


}



exports.execute = execute;

exports.CreateConnectionPool = CreateConnectionPool;
exports.getConnection = getConnection;
exports.releaseConnection = releaseConnection;



