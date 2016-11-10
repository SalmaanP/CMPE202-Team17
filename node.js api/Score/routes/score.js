/**
 * Created by Shrey on 11/8/2016.
 */

var mysql = require('./mysql');

exports.setScores = function (req, res) {
    "use strict";
    var columnName;
    var player = req.param("player");
    var score = req.param("score");
    var id = req.param("id");
    var playerNumber = req.param("playernumber");
    if (playerNumber == 1) {
        columnName = "player1_score";
    }
    else {
        columnName = "player2_score";
    }

    var insertGlobal = "Insert into user(player,score) Values ('" + player + "','" + score + "')";
    var query = "Update game SET `" + columnName + "`=" + score + " where id=" + id;

    mysql.execute(function (err, result1) {

        console.log("Inserted");
    }, insertGlobal);

    mysql.execute(function (err, result) {

        var query1 = "Select * from game where id=" + id;
        mysql.execute(function (err, result1) {

            res.send(result1);
        }, query1)

    }, query);


};

exports.checkRoom = function (req, res) {

    var room_id = req.param("id");
    var answer;
    query = "select * from game where id =" + room_id;

    mysql.execute(function (err, result) {

        if (result[0].player2 == null) {
            answer = {"ready": "no"};
        } else {
            answer = {"ready": "yes"};
        }
        res.send(JSON.stringify(answer));

    }, query);

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

};


exports.topTen = function (req, res) {

   var  query = "select * from user Order by score ASC limit 10";
console.log(query);
    mysql.execute(function (err, result) {

        res.send(result);

    }, query);

};


