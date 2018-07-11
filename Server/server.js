var app = require("express")();
var server = require("http").Server(app);
var io = require("socket.io")(server);
var port = 3000;

var sessions = [];
var session_id = 0;

server.listen(port, function(){
	console.log("Server running at " + port);
});

io.on("connection", function(socket){
	
	socket.on("disconnect", function(){
		
		var obj = getSessionAndPlayerById(socket.id);
		
		socket.broadcast.emit("player_disconnected", obj.player);
		console.log(obj.player.name + " has disconnected");
		obj.session.players.splice(obj.index, 1);
		
		
		// if this was the last player in the session, remove it
		
		if(obj.session.players.length == 0){
			
			var index = sessions.indexOf(obj.session);
			
			if(index > -1){
				sessions.splice(index, 1);
			}
		}
		

		
	});
	
	socket.on("set_data", function(player){
			
			console.log("Player connected : " + player);
			
			var p = new Player(socket.id, player);
			
			// asign a room to the player
			
			sessions.forEach(function(e){
				
				if(e.players.length < 4){
				
					// add player
					
					p.session_id = e.session_id;
					
					e.players.push(p);
					
					socket.broadcast.emit("new_player", p);
					
					socket.emit("get_players", e.players, e.mapIndex);
					
				}
			
			});
			
			if(p.session_id != -1){
				return;
			}
			
			// there are not sessions available so we create a new one 
			
			var s = new Session(session_id);
			
			// add it to the sessions list
			
			sessions.push(s);
			
			// set the the player's session id
			
			p.session_id = s.session_id;
			
			// add the player to the new session
			
			s.players.push(p);
			
			// increase session id
			
			session_id += 1;
			
			// notify other players that a new player entered the session /
			// this is ignored by players in other sessions by checking the player's session id
			
			socket.broadcast.emit("new_player", p);
			
			// set the new player's list
			
			socket.emit("get_players", s.players, s.mapIndex);
			
			
	});
	
	socket.on("player_message", function(data, session_id){
		socket.broadcast.emit("player_message", socket.id, data, session_id);
		socket.emit("player_message", socket.id, data, session_id);
	});
	
	socket.on("player_toggle", function(){
		
		var obj = getSessionAndPlayerById(socket.id);
		
		var p = obj.player;
		var s = obj.session;
		
		p.ready = !p.ready;
		
		socket.broadcast.emit("player_toggle", socket.id, p.session_id);
		socket.emit("player_toggle", socket.id, p.session_id);
		
		// check if all players are ready
		
		if(s.players.length > 1){
			
			var gameReady = true;
			
			for(var j = 0; j < s.players.length; j++){
				if(!s.players[j].ready){
					gameReady = false;
					break;
				}
			}
			
			if(gameReady){
				socket.broadcast.emit("game_ready");
				socket.emit("game_ready");
			}else{
				socket.broadcast.emit("game_canceled");
				socket.emit("game_canceled");
			}
		}	
	});
	
	
	socket.on("set_map", function(mapIndex){
		
		var obj = getSessionAndPlayerById(socket.id);
		
		obj.session.mapIndex = mapIndex;
		
		socket.broadcast.emit("set_map", mapIndex, obj.session.session_id);
		socket.emit("set_map", mapIndex, obj.session.session_id);
	});
	
	// in game events
	
	socket.on("playerMoved", function(data){
		
		socket.broadcast.emit("playerMoved", {
			id: socket.id,
			dir: data
		});
		
	});
	
	socket.on("playerPos", function(data){
		
		socket.broadcast.emit("playerPos", {
			id: socket.id,
			pos: data
		});
		
	});
	
	socket.on("playerStopped", function(){
		socket.broadcast.emit("playerStopped", socket.id);
	});
	socket.on("bombDropped", function(data){
		socket.broadcast.emit("bombDropped", data);
	});
	socket.on("powerUpDropped", function(data){
		socket.broadcast.emit("powerUpDropped", data);
	});
	socket.on("powerUpTaken", function(data){
		socket.broadcast.emit("powerUpTaken", data);
	});
	socket.on("playerDead", function(){
		socket.broadcast.emit("playerDead", socket.id);
	});
	socket.on("playerSpeed", function(speed){
		socket.broadcast.emit("playerSpeed", speed, socket.id);
	});
	
	
});

function getSessionAndPlayerById(id){
	
	var session, player, index;
	
	sessions.forEach(function(s){
		for(var i = 0; i < s.players.length; i++){
			var p = s.players[i];
			if(p.id == id){
				session = s;
				player = p;
				index = i;
			}
		}
	});
	
	return {
		session: session,
		player: player,
		index: index
	}; 
	
}

function Player(id, name){
	this.id = id;
	this.name = name;
	this.ready = false;
	this.session_id = -1; // the session which the player belongs to
}

function Session(id){
	this.session_id = id;
	this.players = [];
	this.mapIndex = 0;
}
