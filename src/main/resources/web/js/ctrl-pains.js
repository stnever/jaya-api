function PainsController( $scope, $http ) {
	$http.get( "api/pains" ).success( function(data) { $scope.pains = data; } );
}

function PainDetailsController( $scope, $http, $routeParams ) {
	var calculateAverages = function( pain ) {
		pain.customers = [];
		angular.forEach( pain.opinions, function(opinion) {
			var customer = findFirst( pain.customers, function(c) { return c.id == opinion.customerId } );
			if ( customer == null ) {
				var customerName = findFirst( $scope.customers, function(c) { return c.id == opinion.customerId } ).name;
				customer = { id: opinion.customerId, totalWeight: 0, opinionsCount: 0, opinions: [], name: customerName };
				pain.customers.push( customer );
			}
			
			customer.opinions.push( opinion );
			customer.totalWeight += opinion.userWeight;
			customer.opinionsCount++;
		});
	}
	
	$http.get( "api/customers" ).then( function(response) {
		$scope.customers = response.data;
		return $http.get( "api/pains/" + $routeParams.id )
	}).then( function(response) {
		$scope.pain = response.data;
		calculateAverages($scope.pain)
	});
	
	$scope.collapseComments = true;
	$scope.collapseOpinion = true;
	$scope.collapseResults = true;
}

function EditPainController( $scope, $http, $routeParams ) {
}

