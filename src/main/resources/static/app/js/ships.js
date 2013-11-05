app.controller("ShipsController", function ($scope) {

    $scope.gameName = "BattleShips";

    $scope.gameFields = createGameFields();
    $scope.shipElementsToPlace = 20;

    $scope.placedShips = [];

    $scope.gameFieldClicked = function(field) {
        if(field.ship) {
            field.ship = false;
            $scope.shipElementsToPlace++;
            $scope.placedShips.splice($scope.placedShips.indexOf(field), 1);
        } else {
            if($scope.shipElementsToPlace > 0) {
                field.ship = true;
                $scope.shipElementsToPlace--;
                $scope.placedShips.push(field);
            } else {
                alert("You cannot place more ships.");
            }
        }
    };


    $scope.joinAGame = function() {
        alert("Join a game with a ships: "+JSON.stringify(createJoinAGameMessage($scope.placedShips)));
    };


    function createJoinAGameMessage(placedShips) {
        var message = {};
        message.event = "JoinAGame";
        message.ships = [];
        for(var i = 0; i < placedShips.length; i++) {
            var ship = placedShips[i];
            message.ships.push({x:ship.x, y:ship.y});
        }
        return message;
    }


    function createGameFields() {
        var fields = [];
        for (var y = 0; y < 100; y++) {
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