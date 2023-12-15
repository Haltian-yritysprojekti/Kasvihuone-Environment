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
//Getting plant data in database
var res = await getPlant(con,body.id)
//Checking if all parameters not empty
if(body.name.length>0 && body.temp.length>0 && body.humd.length>0 && body.lght.length>0 && body.descp.length>0){
    if(body.image.length>0){
        //If image upload to IBM Storage bucket
        var response = await uploadImage(cos,process.env.bucketName,body.fileName,body.image)
        if(response.result=="done"){
            body.image="https://cloud-object-storage-cos-standard-ep4.s3.eu-de.cloud-object-storage.appdomain.cloud/"+body.fileName
            if(res.result[0].image.length>0){
                //If new image uploaded to IBM Storage delete old image
               await deleteImage(cos,process.env.bucketName,res.result[0].image.slice(92))
            }
        }
    }else{
        body.image=res.result[0].image
    }
    //Update data of plant to database
    return new Promise(function(resolve,reject){
        con.query('update Plants set name=?, humd=?, temp=?, descp=?, lght=?, image=? where id=?',[body.name,body.humd,body.temp,body.descp,body.lght,body.image,body.id],function(err,result){
            if(err){
                console.log(err)
            }else{
                resolve({result:"Plant "+body.name+" successfully updated"})
            }
        })
    })
}else{
    //Find new data and replace old data
    if(body.name.length>0){
        res.result[0].name=body.name
    }
    if(body.temp.length>0){
        res.result[0].temp=body.temp
    }
    if(body.humd.length>0){
        res.result[0].humd=body.humd
    }
    if(body.descp.length>0){
        res.result[0].descp=body.descp
    }
    if(body.lght.length>0){
        res.result[0].lght=body.lght
    }
    if(body.image.length>0){
        //Upload new image to IBM Storage
        var resp = await uploadImage(cos,process.env.bucketName,body.fileName,body.image)
        if(resp.result=="done"){
            if(res.result[0].image!=null && res.result[0].image.length>0){
                //Delete old image from IBM Storage
                await deleteImage(cos,process.env.bucketName,res.result[0].image.slice(92)) 
            }
            res.result[0].image="https://cloud-object-storage-cos-standard-ep4.s3.eu-de.cloud-object-storage.appdomain.cloud/"+body.fileName
        }
    }
    //Update plant data to database
    return new Promise(function(resolve,reject){
        con.query('update Plants set name=?, humd=?, temp=?, descp=?, lght=?, image=? where id=?',[res.result[0].name,res.result[0].humd,res.result[0].temp,res.result[0].descp,res.result[0].lght,res.result[0].image,body.id],function(err,result){
            if(err){
                console.log(err)
            }else{
                resolve({result:"Plant "+res.result[0].name+" successfully updated"})
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

//Function to delete old images in IBM Storage bucket
async function deleteImage(cos,bucketName,name){
return cos.deleteObject({
    Bucket:bucketName,
    Key:name
}).promise()
.then(()=>{
    return ({result:"deleted"})
})
.catch((e) => {
    return ({result:{error:"error"}})
});
}