<#include "./describe-rdf.ftl"/>
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
	      		<div class="row" style="margin-bottom:30px">
	      			<div class="btn-group" role="group" aria-label="Basic outlined example">
					  	<button type="button" class="btn btn-outline-primary" onclick="hideRows('dp')"><i class="bi bi-eye"></i> <i class="bi bi-eye-slash"></i> Datatype Properties (${dataProperties})</button>
					  	<button type="button" class="btn btn-outline-primary" onclick="hideRows('op')"><i class="bi bi-eye"></i> <i class="bi bi-eye-slash"></i> Object Properties (${objectProperties})</button>
					  	<button type="button" class="btn btn-danger dropdown-toggle" data-bs-toggle="dropdown" aria-expanded="false">
						    download
						  </button>
						  <ul class="dropdown-menu">						   
		                  <li><a href="#" class="dropdown-item" onclick="downloadResource(window.location.pathname, 'text/turtle', 'data.ttl', 1)">TURTLE</a></li>
		                  <li><a href="#" class="dropdown-item" onclick="downloadResource(window.location.pathname, 'text/ntriples', 'data.nt', 1)">N-TRIPLES</a></li>
		                  <li><a href="#" class="dropdown-item" onclick="downloadResource(window.location.pathname,'application/rdf+xml', 'data.xml',2)" type="">RDF/XML</a></li>
		                  <li><a href="#" class="dropdown-item" onclick="downloadResource(window.location.pathname, 'application/json', 'data.json', 3)" type="">JSON-LD</a></li>
		     
						  </ul>
					</div>
	      		</div>

	      	<div class="table-responsive">
	            <table id="dtBasicExample" class="table table-striped align-middle" id="dtBasicExample">
	              <thead>
	                <tr>
	                  <th class="th-sm">Subject</th>
	                  <th class="th-sm">Predicate</th>
	                  <th class="th-sm">Object</th>
	                </tr>
	              </thead>
		              <tbody>
		                <#list triplets as triplet>
		                  	<tr name="${triplet[4]}">
		                    
		                    <td>
			                    <#if triplet[0]?starts_with("_:") >
			                    	${triplet[0]}
			                    <#else>
			                    	<a href="${triplet[0]}" class="link-primary" >${triplet[0]}</a>
			                    </#if>
		                    </td>
		                    
		                    <td><a href="${triplet[1]}" class="link-primary">${triplet[1]}</a></td>
		                    
		                    <#if triplet[4] == "op">
		                    	<td><a href="${triplet[2]}" class="link-primary">${triplet[2]}</a></td>
		                    <#else>
			                    <td>${triplet[2]}
			                    	<#if triplet[3]?? && triplet[3]?contains("@")>
			                    	<span class="badge bg-warning text-dark">${triplet[3]}</span>
			                    	<#else>
			                    	<span class="badge bg-danger text-light">${triplet[3]}</span>
			                    	</#if>
			                    </td>
		                    </#if>
		                    
		                  	</tr>
		                <#else>

		                </#list>
		                
		              </tbody>
	            	</table>
	          </div>
	        </div>
      	</main>

    <!-- Footer section -->
    <footer class="footer mt-auto py-3 bg-light" style="position: relative;  bottom: 0; width:100%; margin-top:30px">
	  <div class="container">
	    <span class="text-muted">Place sticky footer content here.</span>
	  </div>
	</footer>
</body>
<script src="https://cdn.jsdelivr.net/npm/feather-icons@4.28.0/dist/feather.min.js" integrity="sha384-uO3SXW5IuS1ZpFPKugNNWqTZRRglnUJK6UAZ/gxOX80nxEkN9NcGZTftn6RzhGWE" crossorigin="anonymous"></script><script src="https://cdn.jsdelivr.net/npm/chart.js@2.9.4/dist/Chart.min.js" integrity="sha384-zNy6FEbO50N+Cg5wap8IKA4M/ZnLJgzc6w2NqACZaK0u0FXfOWRRJOnQtpZun8ha" crossorigin="anonymous"></script>
<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.6.0/jquery.min.js"></script>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.2.2/dist/js/bootstrap.bundle.min.js" integrity="sha384-OERcA2EqjJCMA+/3y+gxIOqMEjwtxJY7qPCqsdltbNJuaOe923+mo//f6V8Qbsw3" crossorigin="anonymous"></script>

<!-- Tables-->
<link rel="stylesheet" type="text/css" href="https://cdn.datatables.net/v/bs4/dt-1.12.1/datatables.min.css"/>
<script type="text/javascript" src="https://cdn.datatables.net/v/bs4/dt-1.12.1/datatables.min.js"></script>
<!-- Select with search -->
<link rel="stylesheet" href="https://unpkg.com/@jarstone/dselect/dist/css/dselect.css">
<script src="https://unpkg.com/@jarstone/dselect/dist/js/dselect.js"></script>
<script>

$(document).ready(function () {
      $('#dtBasicExample').DataTable({
          "order": [[0, 'desc']],
          "pagingType": "simple",
          "searching": true
      });
      $('.dataTables_length').addClass('bs-select');
  });

function downloadResource(inputUrl, format, fileName, parsingType) {
            $.ajax({
                url: inputUrl,
                type: "GET",
                headers: {
                    "Accept": format
                },
                success: function (data) {
       
                    var parsedData;
                    if(parsingType == 1)
                      parsedData = data;
                    if(parsingType == 2)
                      parsedData = xmlToString(data);
                    if(parsingType == 3)
                      parsedData= JSON.stringify(data);

                    var element = document.createElement('a');
                    element.setAttribute('href', 'data:'+format+',' + encodeURIComponent(parsedData));
                    element.setAttribute('download', fileName);

                    element.style.display = 'none';
                    document.body.appendChild(element);

                    element.click();

                    document.body.removeChild(element);
                },
                error: function (jqxhr, textStatus, errorThrown) {
                  console.log(textStatus, errorThrown)
                }
            });
        }
        
        function xmlToString(xmlData) { 

          var xmlString;
          //IE
          if (window.ActiveXObject){
              xmlString = xmlData.xml;
          }
          // code for Mozilla, Firefox, Opera, etc.
          else{
              xmlString = (new XMLSerializer()).serializeToString(xmlData);
          }
          return xmlString;
      }  

var stateDP = true;
var stateOP = true;

function hideRows(rowId) {
	if(rowId=="dp" && stateDP){
		$("tr[name='dp']").hide();
		stateDP = false;
	}else if(rowId=="dp" && !stateDP){
		$("tr[name='dp']").show();
		stateDP = true;
	}else if(rowId=="op" && stateOP){
		$("tr[name='op']").hide();
		stateOP = false;
	}else if(rowId=="op" && !stateOP){
		$("tr[name='op']").show();
		stateOP = true;
	}
}
</script>
</html>
