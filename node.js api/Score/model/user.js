var mongoose = require('mongoose');
var Schema = mongoose.Schema;

var mongoURL = "mongodb://localhost:27017/Genus";

mongoose.connect(mongoURL,{server:{poolSize:100}});



// create a schema
var userSchema = new Schema({

    username: {
        type: String,
        required: true
    },
    score: {
        type: String,
        required: true
    },

});

var User = mongoose.model('User', userSchema);

module.exports = User;