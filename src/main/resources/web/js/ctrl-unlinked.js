function UnlinkedIssuesController( $http, $scope, Loading ) {
	
	$http.get( "api/unlinked-issues" ).then(
		function(response) {
			$scope.issues = response.data;
			$scope.totalCount = response.data.length;
			$scope.unlinkedCount = response.data.length;
			
			angular.forEach( $scope.issues, function(issue) {
				issue.linkedToPain = false;
				if ( issue.links ) {
					angular.forEach( issue.links, function(linkedIssue) {
						if ( linkedIssue.key.indexOf( "PG" ) != -1 ) {
							issue.linkedToPain = true;
						}
					});
				}
				
				if ( issue.linkedToPain ) $scope.unlinkedCount--;
			});
		},
		Loading.error
	);
}
