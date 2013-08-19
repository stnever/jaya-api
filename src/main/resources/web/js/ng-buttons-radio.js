angular.module('ngButtonsRadio', [])
       .directive('buttonsRadio', function() {
        return {
            restrict: 'E',
            scope: { model: '=', options:'='},
            controller: function($scope){
                $scope.activate = function(option){
                    $scope.model = option;
                };
            },
            template: "<div class='btn-group'><button type='button' class='btn btn-primary' "+
                        "ng-class='{active: option == model}'"+
                        "ng-repeat='option in options' "+
                        "ng-click='activate(option)'>{{option}} "+
                      "</button></div>"
        };
    });
