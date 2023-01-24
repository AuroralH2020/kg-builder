<!doctype html>
<html lang="en">
  <head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <meta name="author" content="Andrea Cimmino">

    <title>Publisher</title>
    <link href="https://oeg-upm.github.io/helio-publisher/src/main/resources/static/css/favicons/helio-favicon-32x32.png" rel="shortcut icon" type="image/png">

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
              <#list aggregated as endpoint>
                <tr style="text-align: center;">
                  <td>${endpoint.@type}</td>
                  <td>${endpoint.name}</td>
                  <td>${endpoint.format}</td> <!--TODO: Show in the table the number of endpoints-->
                  <td>${endpoint.status}</td>
                  <td>
                    <button type="button" class="btn btn-sm btn-info" data-bs-toggle="modal" data-bs-target="#testModal" onclick="testEndpoint('${endpoint.id}')" style="margin-right:5px"> <i class="bi bi-play"></i></button>
                    <button type="button" class="btn btn-sm btn-warning" data-bs-toggle="modal" data-bs-target="#editModal" id="openEditModal" onclick="editObjectOVERRIDE('${endpoint.id}')" style="margin-right:5px"> <i class="bi bi-pencil-fill"></i></button>
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
            <h1 class="modal-title fs-5" id="editModalLabel">Aggregated Endpoints information</h1>
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
                <label for="endpointType">Aggregation type:</label>
                <select class="form-select" id="@type" onchange="showModalOptions()">
                      <option value="None" class="selected disabled">None</option>
                      <option value="JsonWrapper" >Aggregated JSON</option>
                      <option value="AggregatedRDF" >Aggregated RDF</option>
                      <option value="AggregatedJSONLD11" >Aggregated JSON-LD 1.1</option>
                      <option value="SparqlOverLinks" >Sparql over links</option>
                      <option value="DistributedSparql" >Distributed Sparql</option>
                </select>
              </div>
              
              <div class="form-group" id="endpointAggregatedRDF" style="display:none">
                <label for="format">Output RDF serialisation:</label>
                          <select class="form-select" id="format">
                                <option value="RDFXML">RDFXML</option>
                                <option value="N3">N3</option>
                                <option value="NT">NT</option>
                                <option value="NTRIPLES">NTRIPLES</option>
                                <option value="TTL">TTL</option>
                                <option value="TURTLE" default>TURTLE</option>
                                <option value="JSONLD">JSONLD</option>
                                <option value="NTRIPLE">NTRIPLE</option>
                                <option value="MULTIPLE">MULTIPLE</option>
                          </select>
              </div>
              <hr/>
              <div class="form-group">
                <label for="endpoints">Aggregated endpoints:</label>
                  <ul class="list-group list-group-flush" id="endpoints">
                    
                  </ul>
                  <label for="endpointsSelector">Choose endpoints to add:</label>
                  <select class="form-select" id="endpointsSelector" data-dselect-search="true" data-dselect-creatable="true" data-dselect-clearable="true" data-dselect-max-height="300px" data-dselect-size="sm" onchange="chooseEndpoint()">
                    <optgroup label="EndpointsSelector">
                      <#list endpoints as endpoint>
                         <option value="${endpoint.id}">${endpoint.name}</option>
                      <#else>
                         
                      </#list>
                    </optgroup>
                  </select>
                 
              </div>
              <div class="form-group" id="endpointAggregatedJSONLD11" style="display:none">
                <label for="query">Frame:</label>
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
                      
                      <button type="button" class="btn-close" onclick="location.reload()" data-bs-dismiss="modal" aria-label="Close"></button>
                    </div>
                    <div class="modal-body">
                      <div class="row" style="display: ""; margin-top:10px;">
                        <div class="col-md-12 ms-auto">
                          <textarea class="form-control" id="endpointTestData" rows="13"></textarea>
                        </div>
                      </div>
                    </div>
                    <div class="modal-footer">
                      <button type="button" class="btn btn-secondary" onclick="location.reload()" data-bs-dismiss="modal">Close</button>
                    </div>
                  </div>
                </div>
              </div>
         <!--/test modal-->
  </body>

</html>
<script>
  var base_domain = "/api/aggregated";
  ignoreObjectKeys.push("endpointsSelector");
  $(document).ready(function(){
    $("#menu_aggregated").addClass('active'); // setup active link
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
            // EL TESTING NO PUEDE HACER RE-LOAD
        }else if(xmlHttp.readyState == 4 ){
          //$('#testModal').modal('hide');
          var message = formatErrorText(xmlHttp);
          console.log("CREATE/UPDATE operation response: "+message+" ("+xmlHttp.status+")");
          document.getElementById("endpointTestData").value = message;
         // $('#toastMessage').text(message);
         // $('#toastBackendError').toast('show');
        }
       $("#endpointTestSpinner").css("display", "none");
    }; 
    xmlHttp.open("GET", "/api/aggregated/test/"+id, true); // true for asynchronous 
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
      $("#endpointAggregatedJSONLD11").css("display", "none");
      $("#endpointAggregatedRDF").css("display", "none");
  }

  function showModalOptions(){
      console.log("Running showModalOptions");
      hideModalOptions();
      var endpointType =  document.getElementById("@type").value;
      if(endpointType == "AggregatedRDF"){
        $("#endpointAggregatedRDF").css('display', '');
      }
      if(endpointType == "AggregatedJSONLD11"){
        $("#endpointAggregatedJSONLD11").css('display', '');
      }

    }
  function chooseEndpoint(){
    var endpointChoosen =  document.getElementById("endpointsSelector").value;
    var endpointChoosenName =  $("#endpointsSelector option:selected").text();
    //$().insertAfter($('#endpoints'));
   addEndpoint(endpointChoosen, endpointChoosenName)
    
  }
  function addEndpoint(id, name){
    var ids = $('#endpoints li').map(function(){
                   return $(this).attr('id');
                });
    var contained = false;
    for (let i = 0; i < ids.length; i++) {
      var existingId = ids[i];
      contained = id==existingId;
      if(contained)
        break;
    }
    console.log("*>"+contained);
    if(!contained){    //  
      $('#endpoints').append('<li class="list-group-item d-flex justify-content-between align-items-center" id="'+id.trim()+'" >'+name.trim()+' <button type="button" class="btn btn-outline-danger btn-sm" onclick="removeLi(\''+id.trim()+'\')"><i class="bi bi-trash3-fill"></i></button></li>');
    }
  }
  function removeLi(id){
    console.log("invoked!");
    $('#endpoints li').filter(function(){
                   if($(this).attr('id') == id){
                      $(this).remove();
                   }
                   return true;
                });
  }
  // OVERRIDED METHODS
  function editObjectOVERRIDE(id){
    $('#endpoints').empty();
    var xmlHttp = new XMLHttpRequest();
    xmlHttp.onreadystatechange = function() { 
      if (xmlHttp.readyState == 4 && xmlHttp.status >= 200 && xmlHttp.status < 300){
        clearForm();
        var json = JSON.parse(xmlHttp.responseText);
        const keys = Object.keys(json);
         for (let i = 0; i < keys.length; i++) {
          var key = keys[i];
          var value = json[key];
          var valueType = Object.prototype.toString.call(value);
            console.log("[EDIT] "+key+" -> "+value+" :"+valueType );
            if(valueType == '[object Object]'){
                document.getElementById(key).value = value.id;
            }else if(valueType == '[object Array]'){
                for (let j = 0; j < value.length; j++) {
                  var nestedJson = value[j];
                  addEndpoint(nestedJson.id,nestedJson.name);
                }
            }else if(valueType == '[object String]'){
              document.getElementById(key).value = value;
            }else{
              console.log("eror, unknown data type!");
            }
        }
        
      } else if (xmlHttp.readyState == 4 ){
        var message = formatErrorText(xmlHttp);
        console.log("READ operation response: "+message+" ("+xmlHttp.status+")");
        $('#toastMessage').text(message);
        $('#toastBackendError').toast('show');
      }
    }; 
    console.log("READ operation request: "+base_domain+"/"+id);
    if(Boolean(id)){
      xmlHttp.open("GET", base_domain+"/"+id, true); // true for asynchronous 
      xmlHttp.send(null);
    }else{
      $('#toastMessage').text("Selected element has no id, refreshing the page will solve this issue");
      $('#toastBackendError').toast('show');
    }

  }
</script>

