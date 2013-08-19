function PainsController( $scope, $http ) {
	$http.get( "api/pains" ).success( function(data) { $scope.pains = data; } );
}

function PainDetailsController( $scope, $http, $routeParams, Loading, $q ) {
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
	
	Loading.start();
	$q.all([
		$http.get( "api/customers" ),
		$http.get( "api/pains/" + $routeParams.id )
	]).then(function(responses) {
		$scope.customers = responses[0].data;
		$scope.pain = responses[1].data;
		calculateAverages($scope.pain);
		Loading.hide();
	});
	
	$scope.collapseComments = true;
	$scope.collapseOpinion = true;
	$scope.collapseResults = true;
	
	$scope.possibleOpinions = [ 1, 2, 3, 4 ];
}

function PainCustomerOpinionController( $scope, $http, Loading ) {
	// Quando este controller eh instanciado, no escopo sera colocado um objeto "Customer" que representa o customer corrente.
	// Iremos buscar no escopo pai qual a opiniao atual do usuario sobre este customer, e colocar a opiniao neste escopo.
	$scope.currentUserOpinion = findFirst( $scope.pain.opinions, function(o) { return o.customerId == $scope.customer.id && o.userId == $scope.currentUser.userId } );
	if ( $scope.currentUserOpinion == null ) {
		console.log( "opinion did not exist, creating one" );
		$scope.currentUserOpinion = { userId: $scope.currentUser.userId };
		$scope.pain.opinions.push( $scope.currentUserOpinion );
	}
	
	// Quando a opinion atual muda, avisa o server.
	var sendOpinionToServer = function(newVal, oldVal) {
		if ( oldVal === newVal ) {
			console.log( "listener initialized, values haven't really changed" );
			return;
		}
		$http.put( "api/pains/" + $scope.pain.id + "/opinions/" + $scope.customer.id,
			{ value: $scope.currentUserOpinion.value, comment: $scope.currentUserOpinion.comment } ).error( Loading.error );
		console.log( "changing " + $scope.customer.id + " value to " + $scope.currentUserOpinion.value );
	}
	$scope.$watch( 'currentUserOpinion', sendOpinionToServer, true );
}

function EditPainController( $scope, $http, $routeParams ) {
}

