const mysql = require('mysql');
  
    //Creating access to database with environment variables
    const con = mysql.createPool({
    host     : process.env.databaseHost,
    user     : process.env.user,
    password : process.env.password,
    port     : '3306',
    database : process.env.database
})
//Getting environment tables content in database
exports.handler=(event,context,callback)=>{
    context.callbackWaitsForEmptyEventLoop = false;
    con.query('select * from Environment',function(err,result){
        if (err) throw err;
        callback(null, result)
    })
}