const mysql = require('mysql');
const bucket = require('ibm-cos-sdk')
const mime = require('mime-types');

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
//Checking for empty parameters
if(body.name.length>0 && body.temp.length>0 && body.humd.length>0 && body.lght.length>0 && body.descp.length>0){
    if(body.image.length>0){
        //If image upload to IBM Storage bucket
        var res = await uploadImage(cos,process.env.bucketName,body.fileName,body.image)
        console.log(res.result)
        if(res.result=="done"){
            body.image="https://cloud-object-storage-cos-standard-ep4.s3.eu-de.cloud-object-storage.appdomain.cloud/"+body.fileName
        }else{
            body.image=null
        }
    }else{
        body.image=null
    }
    //Insert plant data to database
    return new Promise(function(resolve,reject){
        con.query('insert into Plants values (null,?,?,?,?,?,?)',[body.name,body.humd,body.temp,body.lght,body.image,body.descp],function(err,result){
        if(err){
            console.log(err)
        }else{
            resolve({result:"Plant added to database successfully"})
        }
    })
    })
}else{
    return {result:"Can't have empty or null parameters"}
}
}

//Function to upload image to IBM Storage bucket
async function uploadImage(cos,bucketName,fileName,file){
var body = new Buffer.from(file,'base64')
const ContentType = mime.contentType(fileName) || 'application/octet-stream'

return cos.putObject({
    Bucket: bucketName, 
    Key: fileName, 
    Body: body,
    ContentType
}).promise()
.then(() => {
    return ({result:"done"})
})
.catch((e) => {
    return ({result:{error:"error"}})
});
}