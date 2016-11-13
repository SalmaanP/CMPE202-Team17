/**
 * Created by karan on 11/5/2016.
 */

var express = require('express');

exports.getScore = function (req, res, next) {

    var results = {"Karan":100,"Salmaan":20};
    res.send(results);

};
