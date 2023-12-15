const mysql = require('mysql');
const bucket = require('ibm-cos-sdk')
  
    //Creating access to database with environment variables
    const con = mysql.createPool({
    host     : process.env.databaseHost,
    user     : process.env.user,
    password : process.env.password,
    port     : '3306',
    database : process.env.database
})
//Making IBM Storage bucket configurations
var config = {
        endpoint: process.env.endpoint,
        apiKeyId: process.env.apikey,
        serviceInstanceId: process.env.serviceInstance,
    };
    //Creating IBM Storage connection
    var cos = new bucket.S3(config);
 
exports.handler= async function(event,context){
    //Parsing incoming body from event
    var body = JSON.parse(event.body)
    //Getting plant data
    var res = await getPlant(con,body.id)
    //Check if plant has image
    if(res.result[0].image != null){
        //If image found delete from IBM Storage
        var del = await deleteImage(cos,process.env.bucketName,res.result[0].image.slice(92))
     if(del.result=="deleted"){
        //Delete plant from database by id
         return new Promise(function(resolve,reject){
         con.query('delete from Plants where id=?',[body.id],function(err,result){
             if(err){
                 reject({result:"error"})
             }else{
                 resolve({result:"Plant deleted from database"})
             }
         })
     })
     }else{
         return ({result:"Error in deleting try again later"})
     }
     }else{
        //If image null simply delete plant from database
         return new Promise(function(resolve,reject){
         con.query('delete from Plants where id=?',[body.id],function(err,result){
             if(err){
                 reject({result:"error"})
             }else{
                 resolve({result:"Plant deleted from database"})
             }
         })
     })
     }
}

//Function to get plant data
async function getPlant(con,id){
    return new Promise(function(resolve,reject){
        con.query('select * from Plants where id=?',[id],function(err,result){
            if(err){
                reject({err})
            }else{
                resolve({result})
            }
        })
    })
}

//Function to delete image from IBM Storage bucket
async function deleteImage(cos,bucketName,name){
    return cos.deleteObject({
        Bucket:bucketName,
        Key:name
    }).promise()
    .then(()=>{
        return ({result:"deleted"})
    })
    .catch((e) => {
        return ({result:"error"})
    });
}