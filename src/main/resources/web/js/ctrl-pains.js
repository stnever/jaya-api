function PainsController( $scope, $http, Loading ) {
	$scope.loadingPage = Loading.task().start();
	$http.get( "api/pains" ).success( function(data) { $scope.pains = data; $scope.loadingPage.success() } ).error( $scope.loadingPage.error );
}

function PainDetailsController( $scope, $http, $routeParams, Loading, $q, $timeout ) {
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
	
	$scope.loadingPage = Loading.task().start();
	$scope.refreshingResults = Loading.task();

	$q.all([
		$http.get( "api/customers" ),
		$http.get( "api/pains/" + $routeParams.id ),
		$http.get("api/pains/" + $routeParams.id + "/results")
	]).then(function(responses) {
		$scope.customers = responses[0].data;
		$scope.pain = responses[1].data;
		$scope.aggregateResults = responses[2].data;
		calculateAverages($scope.pain);
		$scope.loadingPage.success();
		$scope.refreshingResults.success();
	});
	
	$scope.collapseComments = true;
	$scope.collapseOpinion = true;
	$scope.collapseResults = true;
	
	$scope.possibleOpinions = [ 1, 2, 3, 4 ];
	
	$scope.refreshResults = function() {
		$scope.refreshingResults = Loading.task().start();
		$http.get("api/pains/" + $scope.pain.id + "/results").then(
			function(response) { $scope.aggregateResults = response.data; $scope.refreshingResults.success(); },
			function(response) { $scope.refreshingResults.error() }
		);
	}
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

