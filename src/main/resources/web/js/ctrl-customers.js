function CustomersController( $scope, $http ) {
	$http.get( "api/customers" ).success( function(data) { $scope.customers = data; } );
}

function CustomerDetailsController( $scope, $http, $routeParams, $q, Loading ) {
	var calculateAverages = function( customer ) {
		customer.pains = [];
		angular.forEach( customer.opinions, function(opinion) {
			var pain = findFirst( customer.pains, function(p) { return p.id == opinion.painId } );
			if ( pain == null ) {
				var painTitle = findFirst( $scope.pains, function(p) { return p.id == opinion.painId } ).title;
				pain = { id: opinion.painId, totalWeight: 0, opinionsCount: 0, opinions: [], title: painTitle };
				customer.pains.push( pain );
			}
			
			pain.opinions.push( opinion );
			pain.totalWeight += opinion.userWeight;
			pain.opinionsCount++;
		});
	}
	
	Loading.start();
	$q.all([
		$http.get( "api/pains" ),
		$http.get( "api/customers/" + $routeParams.id )
	]).then(function(responses) {
		$scope.pains = responses[0].data;
		$scope.customer = responses[1].data;
		calculateAverages($scope.customer);
		Loading.hide();
	});

	$scope.collapseComments = true;
	$scope.collapseResults = true;
	$scope.collapseOpinion = true;
}

function EditCustomerController( $scope, $http, $routeParams, Loading, $location ) {
	var id = $routeParams.id;
	if ( id != "new" ) {
		Loading.start();
		$http.get( "api/customers/" + $routeParams.id ).then(function(response) {
			Loading.hide();
			$scope.customer = response.data;
		});
	} else {
		$scope.customer = {};
	}
	
	$scope.save = function() {
		Loading.start();
		var q = null;
		if ( $scope.customer.id ) {
			q = $http.put("api/customers/" + $scope.customer.id, $scope.customer);
		} else {
			q = $http.post("api/customers", $scope.customer);
		}
		
		q.then(
			function(response) { Loading.success( "Customer saved successfully", function() { Loading.hide(); $location.path("customers") }, 1500 ) },
			Loading.error
		);
	}
	
}

