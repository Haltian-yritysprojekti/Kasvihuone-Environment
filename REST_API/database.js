const mysql = require('mysql')
const cert = require('./db.json')
//making connection to database
const connection = mysql.createPool({
    host:cert.host,
    user:cert.user,
    password:cert.password,
    database:cert.database
})
module.exports = connection