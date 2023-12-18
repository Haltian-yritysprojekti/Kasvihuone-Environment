const fs = require('fs')
const mqtt = require('mqtt')
const con = require('./database')
const key = fs.readFileSync('./sales-cloudext-prfi00parking/sales-cloudext-prfi00parking.key')
const cert = fs.readFileSync('./sales-cloudext-prfi00parking/sales-cloudext-prfi00parking.pem')
const { error } = require('console')

//Setting MQTT options for connection
const options = {
    protocol:"mqtt",
    Port:8883,
    cert:cert,
    key:key,
    rejectUnauthorized:false
}

//Making MQTT connection to desired url
const client = mqtt.connect('mqtt://a39cwxnxny8cvy.iot.eu-west-1.amazonaws.com',options)

//Debug if something wrong with connection
client.on('error',(...arg)=>{
    console.log(arg)
})

//On connection subcribing to wanted topic
client.on('connect',()=>{
    console.log('connected')
    client.subscribe('cloudext/json/pr/fi/prfi00parking/#',function(err){
        if(!err){
            console.log('subscribed')
        }else{
            console.log('subscribe failed')
        }
    })
    
})

//Handling message and changing data to database on specific sensor 
client.on('message',(message,topic,packet)=>{
    var a = Buffer.from(packet.payload.buffer)
    var b = a.toJSON()
    var c=[]
    var d = ""
    for(x of b.data)
    {
        c.push(String.fromCharCode(x))
    }
    c.forEach(element => {
        if(element=="{"){
            d=element
        }else if(element==","){
            d+="\n"+element
        }else if(element=="}"){
            d+="\n"+element
        }else{
            d+=element
        }
    });
    c=[]
    c.push(d)
    var res = JSON.parse(c)
    console.log('message received')
    update(res)
})

//Updating database based on message id
function update(res){
    console.log("update")
    var date = new Date()
    var dateNow = date.getFullYear()+"-"+(date.getMonth()+1)+"-"+date.getDate().toString().padStart(2,"0")
    var timeNow = date.getHours()+":"+date.getMinutes()+":"+date.getSeconds()
    switch (res.tsmId){
        //Environment data updated based on sensor and humidity and temperature data added for graph.
        case 12100:
            con.query('update Environment set temp=?, humd=?, lght=?, airp=? where sensor=?',[res.temp,res.humd,res.lght,res.airp,res.tsmTuid],function(err,result){
                if(err){
                    console.log(err)
                }else{
                    console.log("Environment of "+res.tsmTuid+" updated")
                    con.query('insert into TempOt values (null,?,?,?,?)',[res.temp,dateNow,timeNow,res.tsmTuid],function(err,result){
                        if(err){
                            console.log(err)
                        }else{
                            console.log("Temperature of "+res.tsmTuid+" updated")
                        }
                    })
                    con.query('insert into HumdOt values (null,?,?,?,?)',[res.humd,dateNow,timeNow,res.tsmTuid],function(err,result){
                        if(err){
                            console.log(err)
                        }else{
                            console.log("Humidity of "+res.tsmTuid+" updated")
                        }
                    })
                }
            })
        break;
        case 12101:
            //Changing sensors door status.
            con.query('update Environment set door=? where sensor=?',[res.hall,res.tsmTuid],function(err,result){
                if(err){
                    console.log(err)
                }else{
                    console.log("Door "+res.tsmTuid+" updated")
                }
            })
        break;
        case 13100:
            //Following visitor count by date.
            con.query('select date_format(date,"%Y-%m-%d") as date,count from Visitors where sensor=? order by date desc limit 1',[res.tsmTuid],function(err,result){
                if(err){
                    console.log(err)
                }else{
                    if(result[0].date==dateNow){
                        con.query('update Visitors set count=? where sensor=? and date=? ',[result[0].count+res.moveCount,res.tsmTuid,result[0].date],function(err,result){
                            if(err){
                                console.log(err)
                            }else{
                                console.log("Visitors of "+res.tsmTuid+" updated")
                            }
                        })
                    }else{
                        con.query('insert into Visitors values (null,?,?,?)',[res.tsmTuid,res.moveCount,dateNow],function(err,result){
                            if(err){
                                console.log(err)
                            }else{
                                console.log("Visitors of "+res.tsmTuid+" added")
                            }
                        })
                    }
                }
            })
        break;
        default:
            //Logging to see other messages
            console.log(res)
    }
}
