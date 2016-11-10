/**
 * Created by Shrey on 11/8/2016.
 */

var mysql = require('./mysql');

/*
 exports.setScores=function(req,res){
 "use strict";
 var username=req.param("username");
 var score=req.param("score");
 var usernameArray=new Array();
 var scoreArray=new Array();
 var finalArray = new Array();


 var query="insert into user(username,score) values('"+username+ "','" + score + "');";
 console.log(query);
 mysql.execute(function(err,results){
 if(err){
 throw err;
 }
 else
 {

 console.log("inserted");
 var dataEntry = "select * from user Order by score LIMIT 10;";
 console.log(dataEntry);


 mysql.execute(function (err, results) {
 if (err) {
 throw err;
 }
 else {
 console.log(results[0]);
 console.log(results.length);

 if (results.length > 0) {

 for (let i = 0; i < results.length; i++) {




 usernameArray[i] = results[i].username;
 scoreArray[i] = results[i].score;


 }
 res.send(usernameArray+"#"+scoreArray);



 }
 }
 }, dataEntry);
 }
 },query);

 };
 */


exports.setScores = function (req, res) {
    "use strict";
    var columnName;
    var player = req.param("player");
    var score = req.param("score");
    var id = req.param("id");
    var playerNumber = req.param("playernumber");
    if(playerNumber==1)
    {
         columnName = "player1_score";
    }
    else
    {
        columnName="player2_score";
    }


var query="Update game SET `"+columnName+"`="+score+" where id="+id;

    mysql.execute(function (err, result) {

        var query1 = "Select * from game where id="+id;
        mysql.execute(function (err,result1) {

            res.send(result1);
        },query1)

    },query);


};


exports.getGame = function (req, res) {

    var player = req.param("player");

    query = "select * from game where roomAvailable = 1";
    console.log(query);

    mysql.execute(function (err, result) {

        console.log(result);
        if (result.length == 0) {

            var query2 = "insert into game(player1, roomAvailable) values('" + player + "', 1);";
            console.log(query2);
            mysql.execute(function (err, result2) {

                console.log(result2.insertId);
                var answer = {"id": result2.insertId, "player1": player, "player2": null, "playernumber": 1};
                res.send(JSON.stringify(answer));

            }, query2);

        } else if (result.length == 1) {

            var query3 = "update game set player2 ='" + player + "', roomAvailable = 0 where id=" + result[0].id;
            mysql.execute(function (err, result3) {
                console.log(result3);
                var answer = {"id": result[0].id, "player1": result[0].player1, "player2": player, "playernumber": 2};
                res.send(JSON.stringify(answer));
            }, query3);

        }

    }, query);


    /*    if(query){
     //joingame
     query2 = "insert into game player2= user";
     send to client player1 and player2 and id;
     } else {
     //create game
     query2 = "insert into game player1 = user, player2 = null";
     send response to await
     client wait, send call every 5 sec;
     }*/

}

/*exports.setscorenew = function(req, res){

 get id ,player name, score from request;
 query = "insert into game where id is id and player name is player name, score ";
 if player2 score not yet, send win, else send lose;
 send top 10 also;

 }*/



