var spawn = require('child_process').spawn;
var request = require('request');
var fs = require('fs');

var API_URL = 'http://drachten.informatik.uni-mannheim.de/api';
var OLD_NAME = 'Good-Old-Larry';
var NEW_NAME = 'Shiny-New-Moe';

var oldPath = '../out/OldBeanMaster.jar';
var jarPath = '../out/BeanMaster.jar';

function spawnJavaProcs(gameString) {
	if (!fs.existsSync(oldPath)) {
		oldPath = jarPath;
    OLD_NAME = NEW_NAME + '-Clone';
	}

	var child1 = spawn('java', ['-jar', oldPath, gameString, OLD_NAME, '0'], {
		stdio: 'inherit'
	});

	var child2 = spawn('java', ['-jar', jarPath,  gameString, NEW_NAME, '6'], {
		stdio: 'inherit'
	});

	return 0;
}

request(API_URL + '/creategame/' + OLD_NAME, function(error, response, body) {
	var gameID = body;
	if(!error && response.statusCode == 200) {
		request(API_URL + '/joingame/' + body + '/' + NEW_NAME, function(error, response, body) {
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
