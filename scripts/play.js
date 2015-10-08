var spawn = require('child_process').spawn;
var request = require('request');
var fs = require('fs');
var path = require('path');

var API_URL = 'http://drachten.informatik.uni-mannheim.de/api';
var PLAYER_NAME = 'Larry';
var PLAYER2_NAME = 'Moe';

var oldPath = '../out/OldBeanMaster.jar';
var jarPath = '../out/BeanMaster.jar';

function spawnJavaProcs(gameString) {
	if (!path.existsSync(oldPath)) {
		oldPath = jarPath;
	}

	var child1 = spawn('java', ['-jar', oldPath, gameString, PLAYER_NAME, '0'], {
		stdio: 'inherit'
	});

	var child2 = spawn('java', ['-jar', jarPath,  gameString, PLAYER2_NAME, '6'], {
		stdio: 'inherit'
	});

	return 0;
}

request(API_URL + '/creategame/' + PLAYER_NAME, function(error, response, body) {
	var gameID = body;
	if(!error && response.statusCode == 200) {
		request(API_URL + '/joingame/' + body + '/' + PLAYER2_NAME, function(error, response, body) {
			if(!error && response.statusCode == 200 && body === '1') {
				var retCode = spawnJavaProcs(gameID);
				if (retCode === 0) {
					console.log('Successfully spawned Java processes. Exiting...');
				} else {
					console.log('Failed to spawn Java processes. Exiting...');
				}
			} else {
				console.log('Could not join game. Exiting...');
			}
		});
	} else {
		console.log('Could not create game. Exiting...');
	}
});
