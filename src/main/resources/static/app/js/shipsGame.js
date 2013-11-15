app.controller("ShipsGameController", function ($scope, $http, $routeParams) {

    var gameId = parseInt($routeParams.gameId);
    var playerId = parseInt($routeParams.playerId);

    $scope.userBoard = createGameFields();
    $scope.opponentBoard = createGameFields();

    var eventsHandled = 0;

    $scope.opponentFieldClicked = function(field) {
        $http.post('/rest/game/'+gameId+'/fire', createFireMessage(field)).
            error(function(data, status, headers, config) {
                alert("Connection error on fire!");
            });
    };

    function createFireMessage(field) {
        var message = {};
        message.playerId = playerId;
        message.location = {}
        message.location.x = field.x;
        message.location.y = field.y;
        return message;
    }

    function listenToGameEvents() {
        $http.get('/rest/gameEvents/'+gameId + '/'+playerId+'/'+eventsHandled).
            success(function(data, status, headers, config) {
                processEvents(data);
                listenToGameEvents();
            }).
            error(function(data, status, headers, config) {
                listenToGameEvents();
            });
    }

    listenToGameEvents();

    function createGameFields() {
        var fields = [];
        for (var y = 0; y < 10; y++) {
            for (var x = 0; x < 10; x++) {
                var field = {};
                field.x = x;
                field.y = y;
                field.ship = false;
                field.hit = false;
                fields.push(field);
            }
        }
        return fields;
    }

    function processEvents(events) {
       for(var i =0; i< events.length; i++) {
           var event = events[i];
           if(event.name == "UserJoined") {
               processUserJoined(event.event);
           } else if (event.name == "PlayerFired") {
               processPlayerFired(event.event);
           }
           eventsHandled++;
       }
    }

    function processUserJoined(event) {
        if(event.playerId == playerId) {

            for(var i = 0; i < event.playerShips.length; i++) {
                var ship = event.playerShips[i];
                $scope.userBoard[ship.x + ship.y * 10].ship = true;
            }
        } else {
            for(var i = 0; i < event.playerShips.length; i++) {
                var ship = event.playerShips[i];
                $scope.opponentBoard[ship.x + ship.y * 10].ship = true;
            }
        }
    }

    function processPlayerFired(event) {
        if(event.playerId == playerId) {
            $scope.opponentBoard[event.location.x + event.location.y * 10].hit = true;
        } else {
            $scope.userBoard[event.location.x + event.location.y * 10].hit = true;
        }
    }

});