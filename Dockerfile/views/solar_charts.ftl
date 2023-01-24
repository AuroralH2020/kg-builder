<!DOCTYPE html>
<html lang="en">
  <head>
    <title>Publisher</title>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <!-- Base imports -->
    <link href="https://oeg-upm.github.io/helio-publisher/src/main/resources/static/css/favicons/helio-favicon-32x32.png" rel="shortcut icon" type="image/png">
    
    <!-- Boostrap -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.2.2/dist/css/bootstrap.min.css" rel="stylesheet" integrity="sha384-Zenh87qX5JnK2Jl0vWa8Ck2rdkQ2Bzep5IDxbcnCeuOxjzrPF/et3URy9Bv1WTRi" crossorigin="anonymous">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.9.1/font/bootstrap-icons.css">
    <script src="https://cdn.jsdelivr.net/npm/chart.js"></script>
       <style>
		  html, body {
		  	height: 100%;
			}
    </style>
  </head>

<body class="d-flex flex-column h-100">
    <!-- Page Header -->
    <header class="navbar navbar-dark sticky-top bg-dark flex-md-nowrap p-0 shadow" style="background-color: #e3f2fd;">
	  <a class="navbar-brand col-md-3 col-lg-2 me-0 px-3 fs-6" href="#">
	    <img src="https://oeg-upm.github.io/helio-publisher/src/main/resources/static/img/favicon_io/favicon-32x32.png" alt="Helio logo" style="margin-top:5px;"/>
	  </a>
	  <button class="navbar-toggler position-absolute d-md-none collapsed" type="button" data-bs-toggle="collapse" data-bs-target="#sidebarMenu" aria-controls="sidebarMenu" aria-expanded="false" aria-label="Toggle navigation">
	    <span class="navbar-toggler-icon"></span>
	  </button>
	  <div class="navbar-nav">
	    <div class="nav-item text-nowrap">
	      
	    </div>
	  </div>
	</header>

    <!-- Page Content -->
      	<main class="flex-shrink-0">
	      	<div class="container" style="margin-top:30px"> 
	      		<div class="row" >
	      			<textarea rows="25"> ${data} </textarea>
	      			<div>
  							<canvas id="myChart"></canvas>
							</div>

							

							<script>
								function random_rgba() {
								    var o = Math.round, r = Math.random, s = 255;
								    return 'rgba(' + o(r()*s) + ',' + o(r()*s) + ',' + o(r()*s) + ',' + r().toFixed(1) + ')';
								}
								var json = ${data};
								var values = json.hasMeasurement;
								measurements = {}; // key is property, value is measurement, aligned with temporal ticks from labels
								labels = [];
								var firstTime = true;
								for (const elem in  values) {
									var fragment = values[elem];
								  var tmps = fragment.timestamp;
								  var value = fragment.value;
								  var property = fragment.property;
								  // adding labels TX
								  labels.push(tmps.substring(10));	
								  // adding measurements
								  if(measurements[property] !== undefined){
								  	var oldValues = measurements[property];
								  	oldValues.push( value)
										measurements[property] = oldValues;
								  }else{
								  	measurements[property] = [ value ];
								  }
								}
								labels = [...new Set(labels)];
								// Creating datasets
								var datasetsBuilt = [];
								for (const property in  measurements) {

									var dataset = {};
									dataset['label'] = property;
									dataset['data'] = measurements[property];
									dataset['fill'] = false;
									dataset['borderColor'] = random_rgba();
									dataset['tension'] = 0.1;
									dataset['borderDash'] = [5, 5];
									datasetsBuilt.push(dataset);
								}
								const data = {
								  labels: labels,
								  datasets: datasetsBuilt
								};
							  const ctx = document.getElementById('myChart');
							  new Chart(ctx, {
							    type: 'line',
							    data: data,
							    options: {
							      scales: {
							        y: {
							          beginAtZero: true
							        }
							      }
							    }
							  });
							</script>

	      		</div>
	      	</div>
      	</main>

    <!-- Footer section -->
    <footer class="footer mt-auto py-3 bg-light" style="position: inherit-absolute;  bottom: 0; width:100%; margin-top:100%;">
	  <div class="container" style="text-align: center">
	    <span class="text-muted">The Publisher is part of the <a href="https://helio-ecosystem.github.io/helio-ecosystem/" style='font-family: "Leckerli One"'>Helio Ecosystem</a>.</span>
	  </div>
	</footer>
</body>
<script src="https://cdn.jsdelivr.net/npm/feather-icons@4.28.0/dist/feather.min.js" integrity="sha384-uO3SXW5IuS1ZpFPKugNNWqTZRRglnUJK6UAZ/gxOX80nxEkN9NcGZTftn6RzhGWE" crossorigin="anonymous"></script><script src="https://cdn.jsdelivr.net/npm/chart.js@2.9.4/dist/Chart.min.js" integrity="sha384-zNy6FEbO50N+Cg5wap8IKA4M/ZnLJgzc6w2NqACZaK0u0FXfOWRRJOnQtpZun8ha" crossorigin="anonymous"></script>
<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.6.0/jquery.min.js"></script>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.2.2/dist/js/bootstrap.bundle.min.js" integrity="sha384-OERcA2EqjJCMA+/3y+gxIOqMEjwtxJY7qPCqsdltbNJuaOe923+mo//f6V8Qbsw3" crossorigin="anonymous"></script>
</html>