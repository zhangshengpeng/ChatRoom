var express = require('express');
var router = express.Router();
var mysql = require('mysql');
var fs = require('fs');
var multer = require('multer')
var async = require('async');


var jwt = require('jsonwebtoken');
var cert = fs.readFileSync('./private.key');
var onlineInfo = {}



var connection = mysql.createConnection({      //创建mysql实例
    host:'localhost',
    port:'3306',
    user:'root',
    password:'root',
    database:'mydatabase'
});

exports.connection = connection

connection.connect();
//处理8小时超时问题
var cnt=0;
var conn=function(){
	var sql = "SELECT id FROM andoriduserInfo";
	//查询MySQL中数据库
   connection.query(sql,function(err,result){
		if(err){
			console.log('[SELECT ERROR] - ',err.message);
		}
	})     
    cnt++;
    console.log("Mysql重连接成功! 次数:"+cnt);
}
//conn;
    setInterval(conn, 3600*1000);
//注册
exports.Insert = function(req,res){
	console.log("注册信息：",req.body)
	var  add = 'INSERT INTO andoriduserInfo(id,password,name) VALUES(?,?,?)';
	var  Params = [req.body.id,req.body.password,req.body.name];
	var ins = "INSERT INTO grp(user_id,user_group) VALUES(?,?)";
	var sql = "SELECT id FROM andoriduserInfo WHERE id = '"+req.body.id+"'"

	connection.query(sql,function(err,result){
		if(err){
			console.log('[SELECT ERROR] - ',err.message);
		}
		else if(result.length>0){
			  res.status(200);
              res.send({
              	status:"0"
              });
		}
		else{
			connection.query(add,Params,function(err,result){
				
				if (err) {
					console.log('[INSERT ERROR] - ',err.message);
				}
				else{
					res.status(200);
					res.send({
						status:"1"
					});
				}
			})
			connection.query(ins,[req.body.id,'我的好友'],function(err,result){
			if (err) {
				console.log(err)
			}
		})
		}
	})
}
// 登录
exports.Login = function(req,res){
	var sql = "SELECT * FROM andoriduserInfo WHERE id ='"+req.body.id+"' AND password='"+req.body.password+"' ";

	connection.query(sql,function(err,result){
		if (result.length==0) {
		res.status(200);
		res.send({status:"0"});
	}
	else{
		const token = jwt.sign(
			{
				id:req.body.id,
			},cert,{ 
				 expiresIn: 60*60
			})
		
		res.send({
			token:token,
			status:"1",
			name:result[0].name,
			url:result[0].url,
			id:result[0].id
		});
	}
	})
}
//用户信息
exports.User_info = function(req,res){
    var token = req.cookies.token;
            jwt.verify(token,cert,function(err,decoded){
                if (err||!decoded) {
                  res.redirect('/turnToLogin.html');
                }
                if (decoded.id) {
                  var sql = "SELECT id,name,address,url FROM andoriduserInfo WHERE id = '"+decoded.id+"'"
                 	connection.query(sql,function(err,result){
                 		if(result.length==1){
                 			var info = {};
                 			for(var key in result){
                 				info[key] = result[key]
                 			}
                 			
                 			res.send({
                 				result:info[0]
                 			})
                 			
                 		}
                 			else if (result.length==0) {
                 				console.log("can not find user");
                 				res.end("can not find user");
                 			}
                 			else{
                 				console.log(err)
                 			}
                 	})
                }
            })
  
}
//好友列表
exports.friendList = function(req,res){
	
	var friendList = []
	var index = 0
	var sql = "SELECT user_group FROM grp WHERE user_id = '"+req.body.id+"'"
	connection.query(sql,function(err,groups,fields){
		if (err) {console.log(err)}
			async.map(groups,function(item,callback){
				var groupInfo = {}
				sql  = "SELECT name,url,id FROM andoriduserinfo JOIN friend ON friend.friend_id = andoriduserinfo.id  WHERE  user_id = '"+req.body.id+"' AND user_group = '"+item.user_group+"'";
				connection.query(sql,function(err,friends,fields){
					groupInfo.groupName = item.user_group
					groupInfo.friends = friends
					friendList[index] = groupInfo
					index++
					callback(null,item)
				})

			},function(err,result){
				res.send({friendList:friendList})

			})

	})
}


//上传头像	 
exports.uploadImg  = function(req,res){
	
	 var file = req.files[0];
	 var arr = file.filename.split('.');
	 console.log(arr[0])
	 var sql = "UPDATE andoridUserInfo set url = ? WHERE id = '"+arr[0]+"'"
	 var Params = ["/img/"+file.filename]
	 connection.query(sql,Params,function(err,result){
				
				if (err) {
					console.log('[INSERT ERROR] - ',err.message);
				}
				else{
					res.send("/img/"+file.filename)
				}
			})
}

exports.Group = function(req,res){
		if (req.body.operation == "add") {
			

		}
			else if(req.body.operation == "update"){}
				else if(req.body.operation == "delete"){}
}

exports.Friend = function(req,res){
		if (req.body.operation == "add") {
			var s = "SELECT id FROM andoriduserinfo WHERE id = '"+req.body.friend_id+"'"
			var sql = "INSERT INTO friend(user_id,friend_id,user_group)VALUES(?,?,?)"
			connection.query(s,function(err,result){
				if (result.length != 1 ) {
					console.log("添加好友结果：该用户不存在！")
					res.send({status:0})
				}else{
					connection.query("SELECT * FROM friend WHERE user_id = '"+req.body.user_id+"' AND friend_id = '"+req.body.friend_id+"'",
						function(err,result){
							if (result.length>0) {
								console.log("已经为好友")
							}else{

								connection.query(sql,[req.body.user_id,req.body.friend_id,'我的好友'],function(err,result){
									if (err) {console.log(err)}
								})
								connection.query(sql,[req.body.friend_id,req.body.user_id,'我的好友'],function(err,result){
									if (err) {console.log(err)}
										else{
										console.log("添加成功：")
										res.send({status:1})
										}
								})
							}
						})
					
				}
			})
			
		
		}
			else if(req.body.operation == "delete"){
				var sql1 = "DELETE * FROM friend WHERE user_id = '"+req.body.user_id+"'AND friend_id = '"+req.body.friend_id+"'"
				var sql2 = "DELETE * FROM friend WHERE user_id = '"+req.body.friend_id+"'AND friend_id = '"+req.body.user_id+"'"
				var s = "SELECT * FROM friend WHERE user_id = '"+req.body.user_id+"' AND friend_id = '"+req.body.friend_id+"'"
				
				connection.query(s,function(err,result){
					if (result) {
						connection.query(sql1,function(err,result){
							if (err) {console.log(err)}
						})
						connection.query(sql2,function(err,result){
							if (result) {
								console.log("删除好友成功")
								res.send({status:1})}
						})

					}
					else{
						console.log("好友不存在！")
						res.send({status:0})}

				})
				
			}
}






	

