/**
 * Created by Shrey on 11/8/2016.
 */

var mysql = require('./mysql');

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
            var dataEntry = "select * from user Order by score DESC LIMIT 10;";
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

}


