app.controller("ShipsGameController", function ($scope, $http, $routeParams) {


    var gameId = parseInt($routeParams.gameId);
    var playerId = parseInt($routeParams.playerId);

    var eventsHandled = 0;

    $scope.userBoard = createGameFields();
    $scope.opponentBoard = createGameFields();


    $scope.opponentFieldClicked = function (field) {
        $http.post('/rest/game/' + gameId + '/fire', createFireMessage(field), {headers : {"Content-Type":"application/json; charset=UTF-8"}}).
            error(function (data, status, headers, config) {
                alert("Connection error on fire!");
            });
    };

    function createFireMessage(field) {
        var message = {};
        message.playerId = playerId;
        message.location = {};
        message.location.x = field.x;
        message.location.y = field.y;
        return message;
    }

    function listenToGameEvents() {
        $http.get('/rest/gameEvents/' + gameId + '/' + playerId + '/' + eventsHandled, {headers : {"Content-Type":"application/json; charset=UTF-8"}}).
            success(function (data, status, headers, config) {
                processEvents(data);
                //listenToGameEvents();
            }).
            error(function (data, status, headers, config) {
                //listenToGameEvents();
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
                field.missed = false;
                fields.push(field);
            }
        }
        return fields;
    }

    function processEvents(events) {
        for (var i = 0; i < events.length; i++) {
            var event = events[i];
            if (event.name == "CurrentUserJoined") {
                processCurrentUserJoined(event.event);
            } else if (event.name == "OpponentJoined") {
                processOpponentJoined(event.event);
            } else if (event.name == "Fired") {
                processFired(event.event);
            } else {
                alert("Unknown message " + event.name)
            }
            eventsHandled++;
        }
    }

    function processCurrentUserJoined(event) {
        for (var i = 0; i < event.playerShips.length; i++) {
            var ship = event.playerShips[i];
            $scope.userBoard[ship.x + ship.y * 10].ship = true;
        }
    }

    function processOpponentJoined(event) {

    }

    function processFired(event) {
        var field = null;
        if (event.playerId == playerId) {
            field = $scope.opponentBoard[event.location.x + event.location.y * 10];
        } else {
            field = $scope.userBoard[event.location.x + event.location.y * 10];
        }

        field.hit = event.hit;
        field.missed = !event.hit;
    }

});