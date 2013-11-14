app.controller("ShipsGameController", function ($scope, $http, $routeParams) {

    var gameId = $routeParams.gameId;
    var playerId = $routeParams.playerId;

    $scope.userBoard = createGameFields();
    $scope.opponentBoard = createGameFields();


    $http.get('/rest/gameEvents/'+gameId + '/'+playerId).
        success(function(data, status, headers, config) {
            alert("Game event received! "+JSON.stringify(data));
        }).
        error(function(data, status, headers, config) {
            alert("Game event failed to receive!");
        });

    function createGameFields() {
        var fields = [];
        for (var y = 0; y < 10; y++) {
            for (var x = 0; x < 10; x++) {
                var field = {};
                field.x = x;
                field.y = y;
                field.ship = false;
                fields.push(field);
            }
        }
        return fields;
    }

});