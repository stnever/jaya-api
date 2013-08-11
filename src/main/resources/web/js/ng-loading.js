angular.module('ngLoading', [])
	.factory('Loading', function( $rootScope, $timeout ) {
		return {
			
			start : function(msg) {
				$("#overlay").removeClass("off").addClass("spinning");
			},
			
			success : function( msg, callback, timeout ) {
				$("#overlay").removeClass("spinning");				
				$("#loadingMessage").removeClass("hide").find(".alert").removeClass("alert-danger").addClass("alert-success").find("span").text(msg);
				
				if ( typeof(timeout) !== "undefined" ) {
					$timeout( callback, timeout );
				}
			},
			
			error : function( arg, arg1 ) {
				var msg = null, heading = "";
				
				// quando usado com $http.error(), esta funcao eh chamada com mais de um argumento.
				if ( typeof(arg1) !== "undefined" && arg1 != null ) {
					msg = 'HTTP ' + arg1 + " " + arg;
				}
				
				// Passou um argumento, e eh string; tenta buscar no bundle.
				else if ( angular.isString(arg) ) {
					msg = arg;
				}
				
				// quando usado com $http.then(), esta funcao eh chamada com um objeto Response que contem status e data.
				else {
					heading = "The server returned an error:";
					msg = "HTTP " + arg.status + "\n" + ( angular.isString(arg.data) ? arg.data : JSON.stringify(arg.data) );
				}
				
				var lm = $("#loadingMessage").removeClass("alert-success").addClass("alert-danger");
				lm.find("span").text(msg);
				lm.find("strong").text(heading);
				/*
				$("div.spinner").find("span").text( msg ).parent().removeClass("alert-success").addClass("alert-danger").css({visibility:"visible", opacity: 100});
				$("div.spinner").find("button").click(function() { $("div.spinner").find("span").parent().removeClass("alert-danger").css({visibility:"hidden"}) });
				*/
			},
			
			hide : function() {
				$("#overlay").addClass("off");
				$("#loadingMessage").addClass("hide");
			}
			
		}
	})
	.directive('loadingMessage', function() {
		// The above name 'myDirective' will be parsed out as 'my-directive'
		// for in-markup uses.
		return {
			restrict: 'E', // element (custom tag)
			template: '<div class="spinner alert"><button type="button" class="close">&times;</button><span>&nbsp;</span></div>',
			replace: true //replace the directive element with the output of the template.
		}
	});
