<!doctype html>
<html lang="en">
  <head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <meta name="author" content="Andrea Cimmino">

    <title>KG Builder</title>

    <#include "./fragments/css-imports.ftl"/>
  </head>

  <body>
    
  <#include "./fragments/navbar.html"/>

  <div class="container-fluid">
    <div class="row">

      <#include "./fragments/sidebar.html"/>
      

      <main class="col-md-9 ms-sm-auto col-lg-10 px-md-4">
        <div class="d-flex justify-content-between flex-wrap flex-md-nowrap align-items-center pt-3 pb-2 mb-3 border-bottom">
          <h1 class="h2">Endpoints available to publish data</h1>
          <div class="btn-toolbar mb-2 mb-md-0">
            <div class="btn-group me-2">
              <button type="button" class="btn btn-sm btn-outline-success" id="addNewEndpoint" data-bs-toggle="modal" data-bs-target="#editModal"><i class="bi bi-plus-circle"></i> Add</button>
             
            </div>
          </div>
        </div>

        <div class="table-responsive">
          <table class="table table-striped align-middle" id="dtBasicExample">
            <thead>
               <tr style="text-align:center;">
                  <th class="th-sm" style="text-align:center;">Type</th>
                  <th class="th-sm" style="text-align:center;">Name</th>
                  <th class="th-sm" style="text-align:center;">Format</th>
                  <th class="th-sm" style="text-align:center;">Status</th>
                  <th class="th-sm" style="text-align:center;">#</th>
               </tr>
            </thead>
            <tbody id="tableBody">
              <#list endpoints as endpoint>
                <tr style="text-align: center;">
                  <td>${endpoint.@type}</td>
                  <td>${endpoint.name}</td>
                  <td>${endpoint.format}</td>
                  <td>${endpoint.status}</td>
                  <td>
                    <button type="button" class="btn btn-sm btn-info" data-bs-toggle="modal" data-bs-target="#testModal" onclick="testEndpoint('${endpoint.id}')" style="margin-right:5px"> <i class="bi bi-play"></i></button>
                    <button type="button" class="btn btn-sm btn-warning" data-bs-toggle="modal" data-bs-target="#editModal" id="openEditModal" onclick="editObject('${endpoint.id}')" style="margin-right:5px"> <i class="bi bi-pencil-fill"></i></button>
                    <button onclick="deleteRow('${endpoint.id}')" type="button" class="btn btn-sm btn-danger"><i class="bi bi-trash3-fill"></i></button></td>

                  </td>
                </tr>
              <#else>
                <tr style="text-align: center;">
                  <td>No data :(</td>
                  <td></td>
                  <td></td>
                  <td></td>
                  <td></td>
                </tr>
              </#list>
            </tbody>
          </table>
        </div>

        <#include "./fragments/toasts.html"/>
        
      </main>
    </div>
  </div>

  <#include "./fragments/js-imports.ftl"/>
   

    <!-- MODALS -->
    <!-- Add Modal -->
    <div class="modal fade"  id="editModal" tabindex="-1" aria-labelledby="editModalLabel" aria-hidden="true">
      <div class="modal-dialog modal-dialog-centered modal-xl">
        <div class="modal-content">
          <div class="modal-header">
            <h1 class="modal-title fs-5" id="editModalLabel">Endpoint's data</h1>
            <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
          </div>
          <div class="modal-body">
            <!-- Modal Body-->

            <form id="inputForm">
              <div class="form-group">
                <input type="text" class="form-control" id="id" style="display: none;">
                <input type="text" class="form-control" id="status" style="display: none;">

                <label for="name">Name</label>
                <input type="text" class="form-control" id="name" aria-describedby="nameHelp" placeholder="Enter a descriptive name for this endpoint">
              </div>
              <div class="form-group">
                <label for="endpointType">Endpoint's type:</label>
                <select class="form-select" id="@type" onchange="showModalOptions()">
                      <option value="None" class="selected disabled">None</option>
                      <option value="Link" >Link</option>
                      <option value="TriplestoreDescribe" >Triplestore resources</option>
                      <option value="TriplestoreSparql" >Triplestore SPARQL</option>
                      <option value="SPARQL" >SPARQL query</option>
                </select> 
              </div>
              <div class="form-group" id="endpointBasicInfo" style="display:none">
                <label for="url">Link:</label>
                <input  type="text" class="form-control" id="url" placeholder="Enter the link of the endpoint">   
                <label for="format">Format:</label>
                          <select class="form-select" id="format">
                                <option value="JSONLD11">JSONLD11</option>
                                <option value="RDFXML">RDFXML</option>
                                <option value="N3">N3</option>
                                <option value="NT">NT</option>
                                <option value="NTRIPLES">NTRIPLES</option>
                                <option value="JSON">JSON</option>
                                <option value="TTL">TTL</option>
                                <option value="TSV">TSV</option>
                                <option value="TURTLE" default>TURTLE</option>
                                <option value="JSONLD">JSONLD</option>
                                <option value="CSV">CSV</option>
                                <option value="NTRIPLE">NTRIPLE</option>
                                <option value="XML">XML</option>
                                <option value="MULTIPLE">MULTIPLE</option>
                          </select>
              </div>
              <div class="form-group" id="endpointSecurity" style="display:none">
                <label for="username">Username:</label>
                <input  type="text" class="form-control" id="username" placeholder="Enter a valid username for the triplestore">  
                <label for="password">Password:</label>
                <input  type="text" class="form-control" id="password" placeholder="Enter a valid username for the triplestore">  
              </div>
              <div class="form-group" id="endpointAliasURL" style="display:none">
                <label for="publishedURL">URI in the publisher to be replaced:</label>
                <input  type="text" class="form-control" id="publishedURL" placeholder="Enter an alias URL for the triplestore resources">  
                <label for="aliasURL">URI in the triplestore:</label>
                <input  type="text" class="form-control" id="aliasURL" placeholder="Enter an alias URL for the triplestore resources">    
                <label for="format"></label>

                <label class="form-check-label" for="flexCheckChecked">
                  Replace output URLs:
                </label>
                <input class="form-check-input" type="checkbox" value="false" id="replaceUris">
              </div>
              <div class="form-group" id="endpointSPARQL" style="display:none">
                <label for="query">SPARQL query:</label>
                <textarea class="form-control" id="query" rows="13"></textarea>
              </div>
            </form>
            
            <!-- /Modal body-->
          </div>
          <div class="modal-footer">
            <button type="button" class="btn btn-secondary" data-bs-dismiss="modal" id="closeModal">Close</button>
            <button type="button" class="btn btn-primary" onclick="updateRow()">Save</button>
          </div>
        </div>
      </div>
    </div>
    <!-- /Add modal-->

         <!-- Test modal -->
           <div class="modal fade"  id="testModal" tabindex="-1" aria-labelledby="testModalLabel" aria-hidden="true">
                <div class="modal-dialog modal-dialog-centered modal-xl">
                  <div class="modal-content">
                    <div class="modal-header">
                      <div class="spinner-grow text-primary" role="status" id="endpointTestSpinner" style="display:none;">
                        <span class="sr-only"></span>
                      </div>

                      <h5 class="modal-title">Data from endpoint</h5>
                      
                      <button type="button" onclick="location.reload()" class="btn-close" data-bs-dismiss="modal" onclick="location.reload()" aria-label="Close"></button>
                    </div>
                    <div class="modal-body">
                      <div class="row" style="display: ""; margin-top:10px;">
                        <div class="col-md-12 ms-auto">
                          <textarea class="form-control" id="endpointTestData" rows="13"></textarea>
                        </div>
                      </div>
                    </div>
                    <div class="modal-footer">
                      <button type="button" class="btn btn-secondary" data-bs-dismiss="modal" onclick="location.reload()">Close</button>
                    </div>
                  </div>
                </div>
              </div>
         <!--/test modal-->
  </body>

</html>
<script>
  var base_domain = "/api/endpoints";

  $(document).ready(function(){
    $("#menu_endpoints").addClass('active'); // setup active link
  });

  // only for smart tables
  $(document).ready(function () {
      $('#dtBasicExample').DataTable({
          "order": [[0, 'desc']],
          "pagingType": "simple",
          "searching": true
      });
      $('.dataTables_length').addClass('bs-select');
  });

  function testEndpoint(id){
    document.getElementById("endpointTestData").value = "";
    var xmlHttp = new XMLHttpRequest();
    xmlHttp.onreadystatechange = function() { 
        if (xmlHttp.readyState == 4 && xmlHttp.status >= 200 && xmlHttp.status < 300){
            //console.log(xmlHttp.responseText);
            document.getElementById("endpointTestData").value = xmlHttp.responseText;
        }else if(xmlHttp.readyState == 4 && xmlHttp.status > 299){
          //$('#testModal').modal('hide');
          var message = formatErrorText(xmlHttp);
          console.log("CREATE/UPDATE operation response: "+message+" ("+xmlHttp.status+")");
          document.getElementById("endpointTestData").value = message;
          //$('#toastMessage').text(message);
          //$('#toastBackendError').toast('show');
        }
       $("#endpointTestSpinner").css("display", "none");
    }; 
    xmlHttp.open("GET", "/api/endpoints/test/"+id, true); // true for asynchronous 
    xmlHttp.send(null);
    $("#endpointTestSpinner").css("display", "");     
  }


  // Additional show/hide functionality for editModal
  // 1. Hide additional fields when adding new endpoint
  document.getElementById("addNewEndpoint").addEventListener("click", hideModalOptions);
  // 2. When choosing an endpoint to edit show only fields relevant
  //    * This event is fired when the modal has been made visible to the user
  $('#editModal').on('shown.bs.modal', function (e) {
    console.log("Opened modal");
    showModalOptions();
  });

  function hideModalOptions(){
      $("#endpointData").css("display", "none");
      $("#endpointSecurity").css("display", "none");
      $("#endpointSPARQL").css("display", "none");
      $("#endpointAliasURL").css("display", "none");
      $("#endpointBasicInfo").css('display', 'none');
  }

  function showModalOptions(){
      console.log("Running showModalOptions");
      hideModalOptions();
      var endpointType =  document.getElementById("@type").value;
      if(endpointType == "TriplestoreDescribe"){
        $("#endpointAliasURL").css('display', '');
        $("#endpointSecurity").css('display', '');
        $("#endpointBasicInfo").css('display', '');
      }
      if(endpointType == "Link"){
        $("#endpointBasicInfo").css('display', '');
      }
      if(endpointType == "SPARQL"){
        $("#endpointBasicInfo").css('display', '');
        $("#endpointSecurity").css('display', '');
        $("#endpointSPARQL").css('display', '');
      }
      if(endpointType == "TriplestoreSparql"){
        $("#endpointBasicInfo").css('display', '');
      }

    }

</script>

